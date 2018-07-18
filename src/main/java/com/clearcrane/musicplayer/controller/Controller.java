package com.clearcrane.musicplayer.controller;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import com.clearcrane.musicplayer.cache.FileCacheManager;
import com.clearcrane.musicplayer.cache.ICacheManager;
import com.clearcrane.musicplayer.common.Constant;
import com.clearcrane.musicplayer.common.utils.HardwareUtils;
import com.clearcrane.musicplayer.common.utils.HttpUtil;
import com.clearcrane.musicplayer.common.utils.JsonUtil;
import com.clearcrane.musicplayer.common.utils.SpUtils;
import com.clearcrane.musicplayer.common.utils.SystemUtils;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private String mUri;
    private IWebSocketManager mWebSocket;
    private IMusicManager mMusicManager;
    private UI mUI;
    private boolean mIsLocal;

    private boolean mMusicServiceStarted = false;
    private boolean mSocketConnected = false;
    private boolean mHasUI = false;

    private ICacheManager mCacheManager;

    private Controller() {
        Log.d(TAG, "Controller: mac = " + HardwareUtils.getMacAddress());
        mMusicManager = MusicManager.getInstance();
    }

    public static Controller getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Controller();
        }
        return INSTANCE;
    }

    /**
     * 初始化WebSocket连接
     * 初始化MusicManager
     */
    public void startWork(Context context, boolean isLocal) {
        mIsLocal = isLocal;
        mUri = SpUtils.getInstance().get(Constant.SP_KEY_HOME_URL, Constant.DEFAULT_WS_ADDR);
        Log.d(TAG, "startWork: mUri = " + mUri);
        if (!mSocketConnected && !isLocal) {
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

        mCacheManager = FileCacheManager.getInstance(context);

        initMusicManager(context, mIsLocal);
    }

    private void initMusicManager(Context context, @SuppressWarnings("SameParameterValue") boolean local) {
        mMusicManager.addOnProgressListener(this::onMusicProgress);
        if (local) {
//            List<Music> musicList = listMusic(context);
//            if (musicList == null) {
//                return;
//            }
//            mManager.setPlayList(musicList);
//            mManager.startPlay(0);
            List<Uri> musicUriList = mCacheManager.loadCache();
            List<Music> musicList = new ArrayList<>();
            if (musicUriList != null && musicUriList.size() > 0) {
                for (Uri uri : musicUriList) {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    Log.d(TAG, "MediaMetadataRetriever: " + uri.getPath());
                    Log.d(TAG, "MediaMetadataRetriever: " + uri.getEncodedPath());
                    Log.d(TAG, "MediaMetadataRetriever: " + uri.toString());
                    retriever.setDataSource(uri.getPath());
                    String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    String author = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
                    String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                    String year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
                    byte[] cover = retriever.getEmbeddedPicture();
                    Music music = new Music(title, uri.getPath(), artist, author, album, year, cover);
                    musicList.add(music);
                }
            }
            mMusicManager.setPlayList(musicList);
        }
    }

    private List<Music> listMusic(Context context) {
        File dir = new File(context.getFilesDir(), "music");
        Log.d(TAG, "listMusic: " + dir);
        if (!dir.exists() && !dir.mkdirs() || !dir.isDirectory()) {
            return null;
        }

        String[] files = dir.list();
        if (files == null) return null;
        List<Music> musicList = new ArrayList<>();
        for (String file : files) {
            if (!(file.endsWith(".mp3") || file.endsWith("m4a") || file.endsWith("wav"))) {
                continue;
            }
            String uri = dir.getAbsolutePath() + File.separator + file;
            Log.d(TAG, "listMusic: uri = " + uri);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri);
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String author = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
            byte[] cover = retriever.getEmbeddedPicture();
            Music music = new Music(title, uri, artist, author, album, year, cover);
            musicList.add(music);
        }

        return musicList;
    }


    /**
     * 启动三步：
     * 1. 上报mac到服务器
     */
    public void reportMacToServer() {
        String mac = HardwareUtils.getMacAddress();
        String ip = HardwareUtils.getLocalIPAddres();
        ClientInfoReportRequest report = new ClientInfoReportRequest();
        report.mac = mac;
        report.ip = ip;
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
        http.getAsync(this::onMusicListGot);
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
                    if (!mIsLocal) {
                        getMusicListJsonFile();
                    }
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
            case "301":
                if (mIsLocal) {
                    mMusicManager.start();
                } else if (url == null) {
                    mMusicManager.start();
                } else {
                    mMusicManager.start(url);
                }
                break;
            case "302":
                mMusicManager.pause();
                break;
            case "303":
                mMusicManager.playPrevious();
                break;
            case "304":
                mMusicManager.playNext();
                break;
            case "305":
                SystemUtils.getInstance().volumeUp();
                break;
            case "306":
                SystemUtils.getInstance().volumeDown();
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
        if (!mSocketConnected && !mIsLocal) {
            Log.w(TAG, "onMusicProgress: Socket is not connected!");
            return;
        }
        if (music == null) {
            Log.v(TAG, "onMusicProgress: music is null");
            return;
        }
        if (mUI != null) {
            mUI.notifyProgressChanged(music, progress, duration, isPlaying);
            mUI.notifyMusicPositionChanged(0, mMusicManager.getCurrentPlayingIndex() + 1, 0, mMusicManager.getPlayList().size());
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
        // 设置播放列表
        mMusicManager.setPlayList(playList);

        // 更新当前位置显示
        if (mUI != null) {
            mUI.notifyMusicPositionChanged(0, 1, 0, playList.size());
        }

        int cacheNum = SpUtils.getInstance().get(Constant.SP_KEY_CACHE_NUM, Constant.DEFAULT_CACHE_NUM);
        for (int i = 0; i < cacheNum; i++) {
            // 缓存cacheNum首歌，默认10首
            Music music = playList.get(i);
            mCacheManager.download(music.url);
        }
    }

    public void setUI(UI ui) {
        mUI = ui;
    }

    public void notifyUICreated() {
        mUI.notifyMusicPositionChanged(0, mMusicManager.getCurrentPlayingIndex(), 0, mMusicManager.getPlayList().size());
    }
}
