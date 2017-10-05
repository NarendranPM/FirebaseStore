package com.fstore.narendran.firebasestore;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by narendran on 04/10/17.
 */

public class FirebaseService extends com.google.firebase.messaging.FirebaseMessagingService{

    private static final String TAG = FirebaseService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}
