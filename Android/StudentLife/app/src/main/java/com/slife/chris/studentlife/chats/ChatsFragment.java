/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.slife.chris.studentlife.chats;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.de.keyboardsurfer.android.widget.crouton.Crouton;
import com.de.keyboardsurfer.android.widget.crouton.Style;
import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.database.ChatsDb;
import com.slife.chris.studentlife.dochat.ChatDialogActivity;
import com.slife.chris.studentlife.utilities.Constants;
import com.slife.chris.studentlife.utilities.PreferencesClass;
import com.slife.chris.studentlife.utilities.SocketInstance;
import com.slife.chris.studentlife.utilities.Time;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatsFragment extends Fragment {

    private Socket mSocket;
    private static final String TAG_PROFILE_PHOTO = "profile_photo";
    private static final String TAG_OPPONENT_USERNAME = "username";
    private static final String TAG_LAST_MESSAGE = "lastMessage";
    private static final String TAG_TIME = "time";
    private  static final String TAG_USER_ID = "user_id";
    private  static final String TAG_CHAT_ID = "chat_id";
    public  static final String TAG_LAST_SEEN = "last_seen";
    private static final String TAG_MESSAGE = "message";
    public  static final String TAG_PHONE_NO = "phoneNo";

    private  JSONArray chatrooms = null;
    private RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.chats_fragment, container, false);

        handleSocket();

        return recyclerView;
    }

    private void handleSocket() {
        readChats();
        mSocket = SocketInstance.getSocket();
        mSocket.connect();
        //remove this login herre
        JSONObject credentials = new JSONObject();
        try {
            credentials.put("user_id","1");
            credentials.put("phoneNo", "0728318609");
            mSocket.emit("login",credentials);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.on("getChats", onGetChats);
        mSocket.on("msg", onNewMessage);

        JSONObject chatDetails = new JSONObject();
        try {
            chatDetails.put("user_id",new PreferencesClass(getActivity()).getUserId());
            mSocket.emit("getChats",chatDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("getChats", onGetChats);
        mSocket.off("msg", onNewMessage);

        //    mSocket.disconnect();
    }
    private Emitter.Listener onGetChats = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                Object json = null;
                try {
                    json = new JSONTokener(args[0].toString()).nextValue();

                    if(json instanceof JSONArray){
                        JSONArray data = (JSONArray) args[0];
                        System.out.println("From on getChats "+data);

                        new LoadAllChatRooms().execute(data);
                    }else{
                        //it is ideally meant to return an array so if it misbehaves do nothing
                        //dfdf
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                }
            });
        }
    };
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    try {
                        JSONObject message = data.getJSONObject("message");
                        Toast.makeText(getActivity(), "Message "+message.toString(), Toast.LENGTH_SHORT).show();
                        try {
                            Crouton.makeText(getActivity(),"@someone :: "+ message.getString("message"), Style.ALERT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new ChatDialogActivity().messageTone(getActivity());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }
    };


    public static class ChatsRecyclerViewAdapter
            extends RecyclerView.Adapter<ChatsRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private static ArrayList<ChatsStructure> chatArrayList;
        private TextDrawable.IBuilder mDrawableBuilder1;


        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final CircleImageView userAvatar;
            public final TextView usernameTextView;
            TextView txtPhoneNo;
            TextView chatsLastMessage;
            TextView chatsTime;
            TextView txtUserId;

            public ViewHolder(View view) {
                super(view);
                mView = view;

                userAvatar = (CircleImageView) view.findViewById(R.id.imgAvatar);
                usernameTextView = (TextView) view.findViewById(R.id.username_C);
                txtPhoneNo = (TextView) view.findViewById(R.id.phoneNo_C);
                chatsLastMessage = (TextView) view.findViewById(R.id.message);
                chatsTime = (TextView) view.findViewById(R.id.chatsTime);
                txtUserId = (TextView) view.findViewById(R.id.userId_C);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + usernameTextView.getText();
            }
        }

        public Object getValueAt(int position) {
            return chatArrayList.get(position);
        }

        public ChatsRecyclerViewAdapter(Context context, ArrayList<ChatsStructure> chats) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mDrawableBuilder1 = TextDrawable.builder()
                    .round();

            chatArrayList = chats;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chats_view, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.usernameTextView.setText(chatArrayList.get(position).getUsername());
            holder.txtPhoneNo.setText(chatArrayList.get(position).getPhoneNo());
            holder.chatsLastMessage.setText(chatArrayList.get(position).getLastMessage());
            holder.chatsTime.setText(new Time().getTimeText(chatArrayList.get(position).getTime()));
            holder.txtUserId.setText(chatArrayList.get(position).getUserId());

            holder.usernameTextView.setTypeface(Constants.getRobotoBold(holder.usernameTextView.getContext()));
            holder.chatsLastMessage.setTypeface(Constants.getRobotoMedium(holder.chatsLastMessage.getContext()));


            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            int color = generator.getRandomColor();
            TextDrawable drawable = mDrawableBuilder1.build(chatArrayList.get(position).getUsername().substring(0,1).toUpperCase(),color);
          //  holder.userAvatar.setImageDrawable(drawable);


            Glide.with(holder.userAvatar.getContext())
                    .load(Constants.IMG_URL+chatArrayList.get(position).getImageAvatar())
                    .placeholder(R.drawable.placeholder_user)
                    .fitCenter()
                    .into(holder.userAvatar);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    String username = ((TextView)  holder.mView.findViewById(R.id.username_C)).getText().toString();
                    String user_id = ((TextView)  holder.mView.findViewById(R.id.userId_C)).getText().toString();
                    String phoneNo = ((TextView)  holder.mView.findViewById(R.id.phoneNo_C)).getText().toString();


                    Bundle b = new Bundle();
                    b.putString("user_id",user_id);


                   Intent intent = new Intent(context, ChatDialogActivity.class);
                    intent.putExtras(b);
                    context.startActivity(intent);


                }
            });



        }

        @Override
        public int getItemCount() {
            return chatArrayList.size();
        }
    }
    class LoadAllChatRooms extends AsyncTask<JSONArray, Void, Void> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Chats Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
          //  pDialog.show();*/
        }

        protected Void doInBackground(JSONArray... args) {

            ChatsDb chatsDb = new ChatsDb(getActivity().getApplicationContext());
            // check for success tag
            try {

                chatsDb.open();
                chatrooms = args[0];

                // looping through All Chat rooms
                for (int i = 0; i < chatrooms.length(); i++) {
                    JSONObject chatroomsJSONObject = chatrooms.getJSONObject(i);

                    System.out.println("chats room obj");
                    System.out.println(chatroomsJSONObject);

                    String user_id = chatroomsJSONObject.getString(TAG_USER_ID);
                    String username = chatroomsJSONObject.getString(TAG_OPPONENT_USERNAME);
                    String chatId = chatroomsJSONObject.getString(TAG_CHAT_ID);
                    String phoneNo = chatroomsJSONObject.getString(TAG_PHONE_NO);
                    String profilePhoto = chatroomsJSONObject.getString(TAG_PROFILE_PHOTO);
                    String lastSeen = chatroomsJSONObject.getString(TAG_LAST_SEEN);


                    JSONObject messageDetail = chatroomsJSONObject.getJSONObject(TAG_MESSAGE);
                    String time = messageDetail.getString(TAG_TIME);
                    String lastMessage = messageDetail.getString(TAG_LAST_MESSAGE);


                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_OPPONENT_USERNAME, username);
                    map.put(TAG_PHONE_NO,phoneNo);
                    map.put(TAG_TIME, time);
                    map.put(TAG_LAST_MESSAGE, lastMessage);
                    map.put(TAG_USER_ID, user_id);
                    map.put(TAG_CHAT_ID,chatId);
                    map.put(TAG_LAST_SEEN,lastSeen);
                    map.put(TAG_PROFILE_PHOTO,profilePhoto);


                    chatsDb.createEntry(chatId,user_id,username,phoneNo,lastMessage,time,profilePhoto,lastSeen);

                }
                chatsDb.close();

            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //pDialog.dismiss();
            readChats();
            // pDialog.dismiss();
        }


    }

    private void readChats() {
        ChatsDb chatsDb = new ChatsDb(getActivity());
        try {
            chatsDb.open();

            ArrayList<ChatsStructure> chatsStructures =getChatRooms(chatsDb.getDbChats());
            if(chatsStructures != null){
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                recyclerView.setAdapter(new ChatsRecyclerViewAdapter(getActivity(),chatsStructures));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        chatsDb.close();
    }

    private ArrayList<ChatsStructure> getChatRooms(ArrayList<HashMap<String, String>> chatroomsArrayList) {
        ArrayList<ChatsStructure> chatRooms = new ArrayList<ChatsStructure>();

        if(chatroomsArrayList != null) {

            int chatRoomsSize =  chatroomsArrayList.size();

            for (int i = 0; i < chatRoomsSize; i++) {

                HashMap<String, String> map2 = chatroomsArrayList.get(i);
                ChatsStructure c1 = new ChatsStructure();
                c1.setUsername(map2.get(TAG_OPPONENT_USERNAME));
                c1.setPhoneNo(map2.get(TAG_PHONE_NO));
                c1.setLastMessage(map2.get(TAG_LAST_MESSAGE));
                c1.setTime(map2.get(TAG_TIME));
                c1.setImageAvatar(map2.get(TAG_PROFILE_PHOTO));
                c1.setUserId(map2.get(TAG_USER_ID));
                chatRooms.add(c1);

            }

            return chatRooms;
        }else{
            return null;
        }
    }
}
