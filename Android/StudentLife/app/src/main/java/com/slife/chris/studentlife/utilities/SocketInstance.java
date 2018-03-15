package com.slife.chris.studentlife.utilities;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketInstance {

    private static Socket mSocket;

    public static synchronized Socket getSocket() {

        if(mSocket == null) {
            try {
                mSocket = IO.socket(Constants.CHAT_SERVER_URL);

            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        return mSocket;
    }

}
