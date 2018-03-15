package com.slife.chris.studentlife.dochat;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.database.ChatMessagesDb;
import com.slife.chris.studentlife.database.ContactsDb;
import com.slife.chris.studentlife.user.UserDetailActivity;
import com.slife.chris.studentlife.utilities.Constants;
import com.slife.chris.studentlife.utilities.ImageManipulate;
import com.slife.chris.studentlife.utilities.IonUpload;
import com.slife.chris.studentlife.utilities.PreferencesClass;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatDialogActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG_MESSAGE_ID = "message_id";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_WHO_SENT = "sender";
    private static final String TAG_TEMP_MESSAGE_ID = "temp_message_id";
    private static final String TAG_FILEPATH = "filepath";
    private static final String TAG_TYPE = "type";
    private static final String TAG_DELIVERED = "delivered";
    private static final String TAG_TIME = "created_at";
    public static final int CONST_GALLERY = 100;
    public static final int CONST_CAMERA = 300;
    private final String TAG_USER_ID = "user_id";


    private JSONArray chatMessages = null;
    private ArrayList<HashMap<String, String>> messageDetailsArrayList = new ArrayList<>();
    ;

    private EditText messageEditText;
    private ListView messagesContainer;
    private ImageView sendButton;
    private ProgressBar progressBar;


    private View stickersFrame;
    private boolean isStickersFrameVisible;
    private ImageView stickerButton;
    private RelativeLayout container;
    private static int opponentID;


    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    //sockets
    private Socket mSocket;
    private static final int TYPING_TIMER_LENGTH = 600;
    private String user_id;
    private ChatDialogAdapter adapter;
    private ProgressDialog progressDialog;
    private ImageView attachButton;
    private LinearLayout addPanel;
    private CircleImageView opponentImage;
    private TextView opponentTv, lastSeenTypingTv,phoneNoTv;
    private String username;
    private String profilePhoto, phoneNo,chatId,lastSeen;
    //user details
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PHONE_NO = "phoneNo";
    public static final String KEY_PROFILE_PHOTO = "profile_photo";
    private  static final String TAG_CHAT_ID = "chat_id";
    public  static final String TAG_LAST_SEEN = "last_seen";
    private LinearLayout emptyLayout;

    private static ArrayList<HashMap<String, String>> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.qiscus_primary_light));
        }
        setContentView(R.layout.activity_chat_dialog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        // swipeRefreshLayout.setProgressViewOffset(false, 0, 128);

        opponentImage = (CircleImageView) findViewById(R.id.opponent_img);
        opponentTv = (TextView) findViewById(R.id.opponent_tv);
        lastSeenTypingTv = (TextView) findViewById(R.id.last_seen_typing_tv);
        phoneNoTv = (TextView) findViewById(R.id.phone_tv);

        Bundle b = getIntent().getExtras();
        //user_id = b.getString("user_id");
        user_id = "1";

        ContactsDb contactsDb = new ContactsDb(ChatDialogActivity.this);
        try {
            contactsDb.open();
            userData = contactsDb.getUserData(user_id);
            username = userData.get(0).get(KEY_USERNAME);
            phoneNo = userData.get(0).get(KEY_PHONE_NO);
            profilePhoto = userData.get(0).get(KEY_PROFILE_PHOTO);
            lastSeen = userData.get(0).get(TAG_LAST_SEEN);
            lastSeen = lastSeen.substring(11,16).toUpperCase();

            chatId = userData.get(0).get(TAG_CHAT_ID);
           // Toast.makeText(getApplicationContext(),"chat ID "+chatId,Toast.LENGTH_LONG).show();
            contactsDb.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String newPhone = "+254"+phoneNo.substring(1,phoneNo.length());
        //phoneNoTv.setText(newPhone);
        opponentTv.setText(username);
        opponentTv.setTypeface(Constants.getRobotoMedium(getApplicationContext()));
        lastSeenTypingTv.setText("LAST ONLINE "+lastSeen+"");
        lastSeenTypingTv.setTypeface(Constants.getRobotoLight(getApplicationContext()));

       Glide.with(this).load(Constants.IMG_URL+profilePhoto).placeholder(R.drawable.placeholder_user).into(opponentImage);

        final Animation logoMoveAnimation = AnimationUtils.loadAnimation(this, R.anim.qiscus_simple_grow);
        attachButton = (ImageView) findViewById(R.id.button_attach);
        addPanel = (LinearLayout) findViewById(R.id.add_panel);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addPanel.getVisibility() == View.GONE) {
                    addPanel.startAnimation(logoMoveAnimation);
                    addPanel.setVisibility(View.VISIBLE);
                } else {
                    addPanel.setVisibility(View.GONE);
                }
            }
        });

        ImageView addImage = (ImageView) findViewById(R.id.button_add_image);
        addImage.setOnClickListener(this);
        ImageView takePhoto = (ImageView) findViewById(R.id.button_pick_picture);
        takePhoto.setOnClickListener(this);

        showLoading();

        //Toast.makeText(ChatDialogActivity.this, "user_id " + user_id, Toast.LENGTH_LONG).show();


        messagesContainer = (ListView) findViewById(R.id.list_message);
        messageEditText = (EditText) findViewById(R.id.field_message);

         emptyLayout = (LinearLayout) findViewById(R.id.empty_chat);


        // container = (RelativeLayout) findViewById(R.id.container);

        adapter = new ChatDialogAdapter(ChatDialogActivity.this);
        messagesContainer.setAdapter(adapter);

        sendButton = (ImageView) findViewById(R.id.button_send);

        messageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll();
            }
        });

       // Snackbar.make(messagesContainer,"Under development !", 3000)
               // .setAction("OK", null).show();

        final AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext(),R.style.AppTheme_Dark_Dialog).create();
        alertDialog.setTitle("Disclaimer !");
        alertDialog.setMessage(getString(R.string.warning));
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"OK",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
       // alertDialog.show();

        initList();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureMessage("text", "");
            }
        });
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  if (null == mUsername) return;
                if (!mSocket.connected()) return;

                if (!mTyping) {
                    mTyping = true;
                    try {

                        JSONObject details = new JSONObject();
                        details.put("user_id", new PreferencesClass(ChatDialogActivity.this).getUserId());
                        details.put("opponentId", user_id);
                        mSocket.emit("typing", details);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



    }

    private void captureMessage(String type, String filepath) {
        String filename = "image_uploads/"+new File(filepath).getName();
        String messageText = messageEditText.getText().toString();

        if (TextUtils.isEmpty(messageText) && type.equals("text")) {
            return;
        }

        try {

            Random random = new Random();
            int rand = random.nextInt(10000000);

            JSONObject message = new JSONObject();
            message.put("user_id", new PreferencesClass(ChatDialogActivity.this).getUserId());
            message.put("message", messageText);
            message.put("temp_message_id", rand);
            message.put("opponentId", user_id);
            message.put("type", type);
            message.put("filepath", filename);

            JSONObject messageEnvelope = new JSONObject();
            messageEnvelope.put("type", "individual");
            messageEnvelope.put("message", message);

            attemptSend(messageEnvelope);
            System.out.println(messageEnvelope);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
        }
        progressDialog.setCancelable(true);
       // progressDialog.show();
    }

    public void dismissLoading() {
        progressDialog.dismiss();
    }

    private void initList() {

        // getMessageDb();

        mSocket = SocketInstance.getSocket();
        mSocket.connect();


        mSocket.on("msg", onNewMessage);
        mSocket.on("insertChatMessage", onInsertChatMessage);
        mSocket.on("getChatMessages", onGetChatMessages);
        mSocket.on("typing", onTyping);
        mSocket.on("polls", onPolling);
        mSocket.on("stop typing", onStopTyping);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                ChatMessagesDb chatMessagesDb = new ChatMessagesDb(ChatDialogActivity.this);
                try {
                    chatMessagesDb.open();
                    ArrayList<ChatDialogStructure> chatDialogStructures = getChatMessages(chatMessagesDb.getDbChatMessages(chatId));
                    if (chatDialogStructures != null) {
                        adapter.removeAll();
                        adapter.notifyDataSetChanged();
                        adapter.add(chatDialogStructures);
                        emptyLayout.setVisibility(View.GONE);
                        //messageTone(ChatDialogActivity.this);
                        scroll();
                    }else{

                    }
                    chatMessagesDb.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });
      t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject chatDetails = new JSONObject();
                try {
                    chatDetails.put("user_id", new PreferencesClass(ChatDialogActivity.this).getUserId());
                    chatDetails.put("opponent", user_id);
                    chatDetails.put("type", "individual");

                   /* Chats chatDb = new Chats(getActivity());
                    chatDb.open();
                    String lastTime = chatDb.getLastDbMessageTime();
                    chatDb.close();
                    chatDetails.put("last_time",lastTime);*/

                    mSocket.emit("getChatMessages", chatDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        t2.start();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("msg", onNewMessage);
        mSocket.off("typing", onTyping);
        mSocket.off("polls", onPolling);
        mSocket.off("insertChatMessage", onInsertChatMessage);
       mSocket.off("stop typing", onStopTyping);
       mSocket.off("getChatMessages", onGetChatMessages);
       // mSocket.disconnect();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;


        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONST_GALLERY:
                    String filepath = getPathFromURI(data.getData());

                   // Toast.makeText(getApplicationContext(), "path " + data.getData(), Toast.LENGTH_SHORT).show();
                    captureMessage("image",filepath);
                    new IonUpload().upload(ChatDialogActivity.this,filepath,"images");

                    break;
                case CONST_CAMERA:
                  //  Toast.makeText(getApplicationContext(), "path " + data.getExtras().get("data"), Toast.LENGTH_SHORT).show();
                    Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                    filepath = getPathFromURI(new ImageManipulate().getImageUri(getApplicationContext(),bitmap));

                    captureMessage("image",filepath);
                    new IonUpload().upload(ChatDialogActivity.this,filepath,"images");

                    break;
            }
        }
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null).loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private Emitter.Listener onGetChatMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject obj = (JSONObject) args[0];

                    JSONArray messagesArray = null;
                   // dismissLoading();
                    try {
                        //check type first
                        messagesArray = obj.getJSONArray("messageArray");
                        System.out.println(messagesArray);

                        if(messagesArray.length() > 0){
                            emptyLayout.setVisibility(View.GONE);
                        }else{
                            emptyLayout.setVisibility(View.VISIBLE);
                        }

                        new LoadAllChatMessages().execute(messagesArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_image:
                Intent fintent = new Intent(Intent.ACTION_GET_CONTENT);
                fintent.setType("image/*");
                try {
                    startActivityForResult(fintent, 100);
                } catch (ActivityNotFoundException e) {

                }
                break;
            case R.id.button_pick_picture:
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, 0);
                break;
            case R.id.button_add_audio:
                break;
        }
    }

    class LoadAllChatMessages extends AsyncTask<JSONArray, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        protected Void doInBackground(JSONArray... args) {


            chatMessages = args[0];
            System.out.println("chat Messages");
            System.out.println(chatMessages);
            insertIntoLocalDb(chatMessages);


            for (int j = 0; j < chatMessages.length(); j++) {

                JSONObject messageObj = null;
                try {
                    messageObj = chatMessages.getJSONObject(j);

                    String message = messageObj.getString(TAG_MESSAGE);
                    String time = messageObj.getString(TAG_TIME);
                    String whoSent = messageObj.getString(TAG_WHO_SENT);
                    String messageId = messageObj.getString(TAG_MESSAGE_ID);
                    String type = messageObj.getString(TAG_TYPE);
                    String filepath = messageObj.getString(TAG_FILEPATH);
                    String delivered = messageObj.getString(TAG_DELIVERED);
                    String tempMessageId = messageObj.getString(TAG_TEMP_MESSAGE_ID);

                    if(delivered.equals("0") && whoSent.equals("opponent")){
                        JSONObject data = new JSONObject();
                        data.put("message_id",messageId);
                        data.put("temp_message_id",tempMessageId);
                        data.put("time",new Date().getTime());
                        data.put("user_id",user_id);
                        //mSocket.emit("acknowledgeReceive",data);
                    }

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TAG_MESSAGE, message);
                    map.put(TAG_TIME, time);
                    map.put(TAG_WHO_SENT, whoSent);
                    map.put(TAG_MESSAGE_ID, messageId);
                    map.put(TAG_TYPE, type);
                    map.put(TAG_FILEPATH, filepath);
                    map.put(TAG_TEMP_MESSAGE_ID, tempMessageId);
                    map.put(TAG_DELIVERED, delivered);


                 //   messageDetailsArrayList.clear();
                    messageDetailsArrayList.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            return null;
        }


        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            // dismiss the dialog once done
        // progressBar.setVisibility(View.GONE);

           getMessageDb();

          // displayMessagesDirect(messageDetailsArrayList);
            //dismissLoading();

        }
    }
    private  void displayMessagesDirect(ArrayList<HashMap<String, String>> messageDetailsArrayList){
        ArrayList<ChatDialogStructure> chatDialogStructures = getChatMessages(messageDetailsArrayList);
        if (chatDialogStructures != null) {
           System.out.println(messageDetailsArrayList);
            adapter.notifyDataSetChanged();
            adapter.addDbMessages(chatDialogStructures);
            messageTone(ChatDialogActivity.this);
            scroll();
        }
    }

    private void getMessageDb() {
        try {
            ChatMessagesDb chatMessagesDb = new ChatMessagesDb(ChatDialogActivity.this);
            chatMessagesDb.open();


            ArrayList<ChatDialogStructure> chatDialogStructures = getChatMessages(chatMessagesDb.getDbChatMessages(chatId));
            if (chatDialogStructures != null) {
              //  System.out.println(chatDialogStructures);
                adapter.removeAll();
               adapter.notifyDataSetChanged();
                adapter.addDbMessages(chatDialogStructures);
               // messageTone(ChatDialogActivity.this);
                scroll();
            }
            chatMessagesDb.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<ChatDialogStructure> getChatMessages(ArrayList<HashMap<String, String>> chatMessagesArrayList) {
        ArrayList<ChatDialogStructure> chatMessages = new ArrayList<>();

        if (chatMessagesArrayList != null) {
            chatMessages.clear();
            for (int i = 0; i < chatMessagesArrayList.size(); i++) {

                HashMap<String, String> map2 = chatMessagesArrayList.get(i);
                ChatDialogStructure c1 = new ChatDialogStructure();
                c1.setSender(map2.get(TAG_WHO_SENT));
                c1.setMessage(map2.get(TAG_MESSAGE));
                c1.setTime(map2.get(TAG_TIME));
                c1.setDelivered(map2.get(TAG_DELIVERED));
                c1.setFilepath(map2.get(TAG_FILEPATH));
                c1.setTempMessageId(map2.get(TAG_TEMP_MESSAGE_ID));
                c1.setMessageId(map2.get(TAG_MESSAGE_ID));
                c1.setType(map2.get(TAG_TYPE));
                adapter.add(c1);
                scroll();

                chatMessages.add(c1);

            }

            return chatMessages;
        } else {
            return null;
        }
    }


    private void attemptSend(JSONObject message) {

        if(mSocket.connected()) {
            messageEditText.setText("");
            try {
                addMessage(message.getJSONObject("message"), "Me");
                mSocket.emit("msg", message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            mSocket.connect();
            Toast.makeText(getApplicationContext(),"Not Sent,Ensure you are connected first",Toast.LENGTH_LONG).show();
        }
    }

    public void messageTone(Context context) {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, alert);
        r.play();
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    try {
                        JSONObject message = data.getJSONObject("message");
                        addMessage(message, "opponent");
                        //  Toast.makeText(ChatDialogActivity.this, "Message "+message.toString(), Toast.LENGTH_SHORT).show();

                       messageTone(ChatDialogActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });
        }
    };

    private Emitter.Listener onInsertChatMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    ChatDialogAdapter chatDialogAdapter = new ChatDialogAdapter(ChatDialogActivity.this);
                    try {
                        String position = chatDialogAdapter.updateMessage(data.getString("temp_message_id"));
                        Toast.makeText(ChatDialogActivity.this, "Message sent successfully ", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //   getActionBar().setTitle("typing...");
                    //addTyping(KEY_USERNAME);
                }
            });
        }
    };
    private Emitter.Listener onPolling = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //addTyping(KEY_USERNAME);
                }
            });
        }
    };
    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // getActionBar().setTitle("Project Castle");
                    removeTyping("chris");
                }
            });
        }
    };

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            try {

                JSONObject details = new JSONObject();
                if (!TextUtils.isEmpty(new PreferencesClass(ChatDialogActivity.this).getUserId())) {
                    details.put("user_id", new PreferencesClass(ChatDialogActivity.this).getUserId());
                    details.put("opponentId", user_id);
                    mSocket.emit("stop typing", details);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void addTyping(String username) {

    }

    private void removeTyping(String username) {

    }

    public void addMessage(JSONObject messageObj, String sender) {
        try {

            ChatDialogStructure chatDialogStructure = new ChatDialogStructure();
            Date date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());

            String message = messageObj.getString(TAG_MESSAGE);
            String type = messageObj.getString(TAG_TYPE);
            String filepath = messageObj.getString(TAG_FILEPATH);
            String tempMessageId = messageObj.getString(TAG_TEMP_MESSAGE_ID);
            String user_id = messageObj.getString(TAG_USER_ID);
            String chat_id = chatId;
            String delivered = "0";
            String time;
            String whoSent;
            String message_id = "";


            if (sender.equals("Me")) {
                time = String.valueOf(timestamp);
                whoSent = "Me";
            } else {
                chatDialogStructure.setSender("opponent");
                whoSent = "opponent";
                time = messageObj.getString(TAG_TIME);
                message_id = messageObj.getString(TAG_MESSAGE_ID);
            }

            chatDialogStructure.setTime(time);
            chatDialogStructure.setSender(whoSent);
            chatDialogStructure.setMessage(message);
            chatDialogStructure.setTempMessageId(tempMessageId);
            chatDialogStructure.setType(type);
            chatDialogStructure.setFilepath(filepath);
            chatDialogStructure.setDelivered(delivered);
            chatDialogStructure.setMessageId(message_id);

            ChatMessagesDb chatsMessagesDb = new ChatMessagesDb(ChatDialogActivity.this);
            chatsMessagesDb.open();
            chatsMessagesDb.createEntry(message_id, message, user_id, chat_id, time, whoSent, type, filepath, tempMessageId, delivered);
            System.out.println("inserted successfully");
            chatsMessagesDb.close();

            adapter.notifyDataSetChanged();
            emptyLayout.setVisibility(View.GONE);
            adapter.add(chatDialogStructure);
            //messageTone(ChatDialogActivity.this);
            scroll();

            //display on adapter
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        scroll();

    }


    //this should ideally be done in its own class please
    private void insertIntoLocalDb(JSONArray chatMessages) {

        ChatMessagesDb chatsMessagesDb = new ChatMessagesDb(ChatDialogActivity.this);
        try {
            chatsMessagesDb.open();
            for (int j = 0; j < chatMessages.length(); j++) {

                JSONObject messageObj = chatMessages.getJSONObject(j);
                String message_id = messageObj.getString(TAG_MESSAGE_ID);
                String message = messageObj.getString(TAG_MESSAGE);
                String time = messageObj.getString(TAG_TIME);
               // String chat_id = messageObj.getString(TAG_CHAT_ID);
                String user_id = messageObj.getString(TAG_USER_ID);
                String whoSent = messageObj.getString(TAG_WHO_SENT);
                String type = messageObj.getString(TAG_TYPE);
                String filepath = messageObj.getString(TAG_FILEPATH);
                String tempMessageId = messageObj.getString(TAG_TEMP_MESSAGE_ID);
                String delivered = messageObj.getString(TAG_DELIVERED);


                chatsMessagesDb.createEntry(message_id, message, user_id, chatId, time, whoSent, type, filepath, tempMessageId, delivered);

//                Toast.makeText(getActivity(),"inserted succesfully" , Toast.LENGTH_LONG).show();
            }
            System.out.println("inserted succesfully");

            chatsMessagesDb.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(ChatDialogActivity.this, "SQL ERROR " + e, Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void loadChatHistory(ArrayList<ChatDialogStructure> chatDialogStructures) {


        for (int i = chatDialogStructures.size() - 1; i >= 0; --i) {
            ChatDialogStructure msg = chatDialogStructures.get(i);
            if (i == chatDialogStructures.size() - 1) {
                showMessage(msg, true);
            }
            showMessage(msg, false);
        }

       // progressBar.setVisibility(View.GONE);
    }


    public void showMessage(ChatDialogStructure message, Boolean notify) {


        adapter.add(message);
        String receiver = "Ana";

        Long time = new Date().getTime() / 1000;
        if (notify) {
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(ChatDialogActivity.this.getApplicationContext(), alert);
            r.play();


        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                scrollDown();
            }
        });
    }


    private String encodeImage(String path) {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagefile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }

    private Bitmap decodeImage(String data) {
        byte[] b = Base64.decode(data, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bmp;
    }

    private void showKeyboard() {
        ((InputMethodManager) messageEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(messageEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void scrollDown() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void scroll() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                scrollDown();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_dialog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_view_contact) {
            Intent i = new Intent(this,UserDetailActivity.class);
            Bundle b = new Bundle();
            b.putString("user_id",user_id);
            i.putExtras(b);
            startActivity(i);
            return true;
        }else if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
