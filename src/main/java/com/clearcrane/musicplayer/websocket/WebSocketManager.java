package com.clearcrane.musicplayer.websocket;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import static com.clearcrane.musicplayer.utils.Preconditions.checkNotNull;

/**
 * Created by jjy on 2018/5/22.
 * <p>
 * 管理WebSocket的连接。
 */

public class WebSocketManager implements IWebSocketManager {
    private static final String TAG = "WebSocketManager";
    private WebSocketClient mClient;
    private OnWebSocketListener mOnWebSocketListener = message -> Log.d(TAG, "onMessage: " + message);

    public WebSocketManager(String uri) {
        mClient = new WebSocketClient(URI.create(uri)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                mOnWebSocketListener.onOpen(handshakedata);
            }

            @Override
            public void onMessage(String message) {
                mOnWebSocketListener.onMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                mOnWebSocketListener.onClose(code, reason, remote);
            }

            @Override
            public void onError(Exception ex) {
                mOnWebSocketListener.onError(ex);
            }
        };
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
}
