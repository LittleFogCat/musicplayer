package com.clearcrane.musicplayer.controller;

import android.util.Log;

import com.clearcrane.musicplayer.common.utils.HardwareUtils;
import com.clearcrane.musicplayer.common.utils.HttpUtil;
import com.clearcrane.musicplayer.common.utils.JsonUtil;
import com.clearcrane.musicplayer.entity.ClientInfoReportRequest;
import com.clearcrane.musicplayer.entity.GetMusicListJsonFileRequest;
import com.clearcrane.musicplayer.entity.GetMusicListResponse;
import com.clearcrane.musicplayer.musicmanager.IMusicManager;
import com.clearcrane.musicplayer.musicmanager.Music;
import com.clearcrane.musicplayer.musicmanager.MusicManager;
import com.clearcrane.musicplayer.ui.UI;
import com.clearcrane.musicplayer.websocket.IWebSocketManager;
import com.clearcrane.musicplayer.websocket.WebSocketManager;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.clearcrane.musicplayer.common.Constant.DEFAULT_WS_ADDR;
import static com.clearcrane.musicplayer.common.Constant.WS_CMD;
import static com.clearcrane.musicplayer.common.Constant.WS_CMD_CONTROL_MUSIC;
import static com.clearcrane.musicplayer.common.Constant.WS_CMD_GET_MUSIC_FILE_JSON;
import static com.clearcrane.musicplayer.common.Constant.WS_CMD_REPORT_CLIENT_INFO;

/**
 * Created by jjy on 2018/5/22.
 * <p>
 * 处理业务逻辑。
 */

@SuppressWarnings("WeakerAccess")
public class Controller {
    private static final String TAG = "Controller";
    private static Controller INSTANCE;
    private String mUri = DEFAULT_WS_ADDR;
    private IWebSocketManager mWebSocket;
    private IMusicManager mMusicManager;
    private UI mUI;

    private boolean mMusicServiceStarted = false;
    private boolean mSocketConnected = false;
    private boolean mHasUI = false;

    private Controller() {
        Log.d(TAG, "Controller: mac = " + HardwareUtils.getMacAddress());
    }

    public static Controller getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Controller();
        }
        return INSTANCE;
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    /**
     * 初始化WebSocket连接
     * 初始化MusicManager
     */
    public void startWork() {
        if (!mSocketConnected) {
            mWebSocket = new WebSocketManager(mUri);
            mWebSocket.setOnWebSocketListener(new IWebSocketManager.OnWebSocketListener() {
                @Override
                public void onMessage(String message) {
                    onWebSocketMessage(message);
                }

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d(TAG, "onOpen: " + handshakedata);
                    mSocketConnected = true;
                    reportMacToServer();
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "onClose: " + reason + code + remote);
                    mSocketConnected = false;
                }

                @Override
                public void onError(Exception ex) {
                    Log.d(TAG, "onError: " + ex);
                    ex.printStackTrace();
                    mSocketConnected = false;
                }
            });
            mWebSocket.connect();
        }

        mMusicManager = MusicManager.getInstance();
        mMusicManager.addOnProgressListener(this::onMusicProgress);
    }

    /**
     * 启动三步：
     * 1. 上报mac到服务器
     */
    public void reportMacToServer() {
        String mac = HardwareUtils.getMacAddress();
        ClientInfoReportRequest report = new ClientInfoReportRequest();
        report.mac = mac;
        mWebSocket.sendMessage(JsonUtil.obj2Json(report));
    }

    /**
     * 启动三步：
     * 2. 获取音乐列表Json文件的URL
     */
    public void getMusicListJsonFile() {
        GetMusicListJsonFileRequest request = new GetMusicListJsonFileRequest();
        mWebSocket.sendMessage(JsonUtil.obj2Json(request));
    }

    /**
     * 启动三步：
     * 3. 获取音乐列表
     */
    public void getMusicList(String url) {
        Log.d(TAG, "getMusicList: " + url);
        HttpUtil http = new HttpUtil(url);
        http.get(this::onMusicListGot);
    }

    /**
     * 解析服务器发送过来的消息
     */
    private void parseIntentFromServer(String json) {
        try {
            JSONObject obj = JsonUtil.getJsonObject(json);
            if (!obj.has(WS_CMD)) {
                return;
            }
            String wsCmd = obj.getString(WS_CMD);
            switch (wsCmd) {
                case WS_CMD_CONTROL_MUSIC:
                    if (!mMusicServiceStarted) {
                        Log.v(TAG, "onWebSocketMessage: MusicServiceNotStarted, Return");
                        return;
                    }
                    String cmd = obj.getString("intent");
                    String url = obj.has("url") ? obj.getString("url") : null;
                    controlMusic(cmd, url);
                    break;
                case WS_CMD_REPORT_CLIENT_INFO:
                    Log.d(TAG, "parseIntentFromServer: report mac success");
                    getMusicListJsonFile();
                    break;
                case WS_CMD_GET_MUSIC_FILE_JSON:
                    if (obj.has("fileUrl")) {
                        String fileUrl = obj.getString("fileUrl");
                        getMusicList(fileUrl);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 控制音乐播放
     *
     * @param cmd 控制指令
     * @param url 音乐url，仅当cmd=="play"有效
     */
    public void controlMusic(String cmd, String url) {
        Log.d(TAG, "controlMusic: " + cmd + ", " + url);
        switch (cmd) {
            case "play":
                if (url == null) {
                    mMusicManager.start();
                } else {
                    mMusicManager.start(url);
                }
                break;
            case "pause":
                mMusicManager.pause();
                break;
            case "next":
                mMusicManager.playNext();
                break;
            case "previous":
                mMusicManager.playPrevious();
                break;
            default:
                Log.w(TAG, "parseIntentFromServer: not support operation: " + cmd);
                break;
        }
    }


    public boolean hasUI() {
        return mHasUI;
    }

    public IMusicManager getMusicManager() {
        return mMusicManager;
    }

    public IWebSocketManager getSocketManager() {
        return mWebSocket;
    }

    /* * * * * * * * * * * 各种回调 * * * * * * * * * * * * * */

    /**
     * websocket服务器发来消息
     */
    public void onWebSocketMessage(String message) {
        Log.v(TAG, "onWebSocketMessage: " + message);
        parseIntentFromServer(message);
    }

    /**
     * 音乐播放状态上报
     */
    private void onMusicProgress(Music music, int progress, int duration, boolean isPlaying) {
        if (!mSocketConnected) {
            Log.w(TAG, "onMusicProgress: Socket is not connected!");
            return;
        }
        if (music == null) {
            Log.v(TAG, "onMusicProgress: music is null");
            return;
        }
        if (mUI != null) {
            mUI.onMusicProgress(music, progress, duration, isPlaying);
        }
        Log.v(TAG, "onMusicProgress: onProgress " + ((float) progress) / duration);
    }

    /**
     * MusicService启动
     */
    public void onMusicServiceStarted() {
        Log.d(TAG, "onMusicServiceStarted: ");
        mMusicServiceStarted = true;
    }

    /**
     * MusicService停止
     */
    public void onMusicServiceStopped() {
        Log.d(TAG, "onMusicServiceStopped: ");
        mMusicServiceStarted = false;
    }

    /**
     * 获取音乐列表成功
     */
    public void onMusicListGot(String response) {
        Log.d(TAG, "onMusicListGot: " + response);
        GetMusicListResponse musicList = JsonUtil.json2Obj(response, GetMusicListResponse.class);
        List<Music> playList = new ArrayList<>();
        for (GetMusicListResponse.Musics music : musicList.Musics) {
            Music m = new Music();
            m.name = music.Name.zhCN;
            m.artist = music.SingerName.zhCN;
            m.url = music.URL_ABS;
            playList.add(m);
        }
        mMusicManager.setPlayList(playList);
    }

    public void setUI(UI ui) {
        mUI = ui;
    }
}
