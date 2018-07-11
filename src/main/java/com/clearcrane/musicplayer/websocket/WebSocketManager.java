package com.clearcrane.musicplayer.websocket;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Date;

import static com.clearcrane.musicplayer.common.utils.Preconditions.checkNotNull;

/**
 * Created by jjy on 2018/5/22.
 * <p>
 * 管理WebSocket的连接。
 */

public class WebSocketManager implements IWebSocketManager {
    private static final String TAG = "WebSocketManager";
    private WebSocketClient mClient;
    private OnWebSocketListener mOnWebSocketListener = message -> Log.d(TAG, "onMessage: " + message);
    private String mUri;
    private HeartbeatThread mHeartbeatThread;
    private boolean mIsConnected;

    public WebSocketManager(String uri) {
        mUri = uri;
        mClient = createClient(uri);
    }

    @Override
    public void setOnWebSocketListener(OnWebSocketListener l) {
        mOnWebSocketListener = l;
    }

    @Override
    public void connect() {
        checkNotNull(mClient);
        mClient.connect();
    }

    @Override
    public void disconnect() {
        checkNotNull(mClient);
        mClient.close();
    }

    @Override
    public void sendMessage(String message) {
        checkNotNull(mClient);
        mClient.send(message);
    }

    private void reconnect() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Log.d(TAG, "websocket reconnecting... /" + new Date());
            mClient = createClient(mUri);
            mClient.connect();
        }, 10000);
    }

    private WebSocketClient createClient(String uri) {
        return new WebSocketClient(URI.create(uri)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                mOnWebSocketListener.onOpen(handshakedata);
                mIsConnected = true;
                mHeartbeatThread = new HeartbeatThread();
                mHeartbeatThread.start();
            }

            @Override
            public void onMessage(String message) {
                mOnWebSocketListener.onMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                mIsConnected = false;
                mHeartbeatThread.exit();
                mOnWebSocketListener.onClose(code, reason, remote);
                reconnect();
            }

            @Override
            public void onError(Exception ex) {
                mIsConnected = false;
                mOnWebSocketListener.onError(ex);
                reconnect();
            }
        };
    }

    private class HeartbeatThread extends Thread {
        private boolean mValid = true;

        @Override
        public void run() {
            while (mIsConnected && mValid) {
                mClient.send("{\"wsCmd\":\"heartbeat\"}");
                try {
                    Thread.sleep(45000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void exit() {
            mValid = false;
        }
    }
}
