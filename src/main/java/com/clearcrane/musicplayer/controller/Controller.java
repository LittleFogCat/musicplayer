package com.clearcrane.musicplayer.controller;

import android.util.Log;

import com.clearcrane.musicplayer.musicmanager.IMusicManager;
import com.clearcrane.musicplayer.musicmanager.MusicManager;
import com.clearcrane.musicplayer.websocket.IWebSocketManager;
import com.clearcrane.musicplayer.websocket.WebSocketManager;

import org.java_websocket.handshake.ServerHandshake;

/**
 * Created by jjy on 2018/5/22.
 */

public class Controller {
    private static final String TAG = "Controller";
    private static Controller INSTANCE;
    private String mUri = "http://192.168.17.187:9999";
    private IWebSocketManager mWebSocket;
    private IMusicManager mMusicManager;

    private boolean mServiceStarted = false;
    private boolean mSocketConnected = false;
    private boolean mHasUI = false;

    private Controller() {
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

    public void startWork() {
        mWebSocket = new WebSocketManager(mUri);
        mWebSocket.setOnWebSocketListener(new IWebSocketManager.OnWebSocketListener() {
            @Override
            public void onMessage(String message) {
                Controller.this.onWebSocketMessage(message);
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d(TAG, "onOpen: " + handshakedata);
                mSocketConnected = true;
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

        mMusicManager = MusicManager.getInstance();
        mMusicManager.setOnServiceStartListener(service -> mServiceStarted = true);
    }

    public void onWebSocketMessage(String message) {
        Log.d(TAG, "onWebSocketMessage: " + message);
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

}
