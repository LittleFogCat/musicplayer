package com.clearcrane.musicplayer.websocket;

import android.util.Log;

import org.java_websocket.handshake.ServerHandshake;

/**
 * Created by jjy on 2018/5/22.
 */

public interface IWebSocketManager {
    String TAG = "WebSocketManager";

    interface OnWebSocketListener {
        default void onOpen(ServerHandshake handshakedata) {
            Log.d(TAG, "onOpen: " + handshakedata);
        }

        void onMessage(String message);

        default void onClose(int code, String reason, boolean remote) {
            Log.d(TAG, "onClose: " + reason + ", " + code + (remote ? " :remote" : ""));
        }

        default void onError(Exception ex) {
            Log.w(TAG, "onError: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void setOnWebSocketListener(OnWebSocketListener l);

    void connect();

    void disconnect();

    void sendMessage(String message);
}
