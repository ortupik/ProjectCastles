package com.slife.chris.studentlife.aunthetication;

import android.content.Context;

import com.slife.chris.studentlife.utilities.PreferencesClass;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Chris on 12/4/2016.
 */
public  class Login  {

    private Socket mSocket;
    private PreferencesClass myPrefences;
    private Context context;

    public Login(Context context){
        this.context = context;
        myPrefences = new PreferencesClass(context);
        mSocket = SocketInstance.getSocket();
        mSocket.connect();
        mSocket.on("login", onloggedIn);
        login();
    }

    public void login(){

        JSONObject credentials = new JSONObject();
        try {
            credentials.put("user_id",myPrefences.getUserId() );
            credentials.put("phoneNo",myPrefences.getPhoneNo());
            mSocket.emit("login", credentials);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private Emitter.Listener onloggedIn = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final JSONObject data = (JSONObject) args[0];
            //Toast.makeText(context,"Debug: Logged you in automatically", Toast.LENGTH_SHORT).show();
        }
    };



}
