

package com.slife.chris.studentlife.contacts;

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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.database.ContactsDb;
import com.slife.chris.studentlife.database.StudentDb;
import com.slife.chris.studentlife.dochat.ChatDialogActivity;
import com.slife.chris.studentlife.utilities.Constants;
import com.slife.chris.studentlife.utilities.PreferencesClass;
import com.slife.chris.studentlife.utilities.SocketInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ContactsFragment extends Fragment {

    private Socket mSocket;
    private static final String TAG_USERNAME = "username";
    private static final String TAG_STATUS = "status";
    public  static final String TAG_USER_ID = "user_id";
    public  static final String TAG_LAST_SEEN = "last_seen";
    public  static final String TAG_CHAT_ID= "chat_id";
    public  static final String TAG_PHONE_NO = "phoneNo";
    public  static final String TAG_PROFILE_PHOTO = "profile_photo";

    JSONArray contacts = null;

    private RecyclerView recyclerView;
    private String dept,classGroup,regNo;
    public Context context;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.chats_fragment, container, false);
        handleSocket();

        return recyclerView;
    }

    private void handleSocket() {
        readDbContacts();
        mSocket = SocketInstance.getSocket();
        mSocket.connect();
        mSocket.on("getContacts", onGetContacts);

        StudentDb studentDb = new StudentDb(getActivity());
        try {
            studentDb.open();
            ArrayList<HashMap<String,String>> studentData = studentDb.getStudentData();
            dept = studentData.get(0).get("dept");
            classGroup = studentData.get(0).get("classGroup");
            regNo =  studentData.get(0).get("regNo");

            JSONObject chatDetails = new JSONObject();
            try {
                chatDetails.put("user_id",new PreferencesClass(getActivity()).getUserId());
                chatDetails.put("dept",dept);
                chatDetails.put("class_group",classGroup);
                mSocket.emit("getContacts",chatDetails);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        studentDb.close();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("getContacts", onGetContacts);
        //    mSocket.disconnect();
    }
    private Emitter.Listener onGetContacts = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        JSONArray contactsArray = data.getJSONArray("contacts");
                        new LoadAllContacts().execute(contactsArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    };

    public static class ContactsRecyclerViewAdapter
            extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private static ArrayList<ContactStructure> contactsArrayList;
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
            TextView txtStatus;
            TextView txtonlineStatus;
           // TextView lastSeenTv;
           // TextView chatIdTv;

            public ViewHolder(View view) {
                super(view);
                mView = view;

                userAvatar = (CircleImageView) view.findViewById(R.id.imgAvatar);
                usernameTextView = (TextView) view.findViewById(R.id.username);
                txtPhoneNo = (TextView) view.findViewById(R.id.phoneNo);
                txtUserId = (TextView) view.findViewById(R.id.userId);
                txtStatus = (TextView) view.findViewById(R.id.status);
               // lastSeenTv = (TextView) view.findViewById(R.id.last_seen);
             //   chatIdTv = (TextView) view.findViewById(R.id.chat_id);
                txtonlineStatus = (TextView) view.findViewById(R.id.onlineStatus);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + usernameTextView.getText();
            }
        }

        public Object getValueAt(int position) {
            return contactsArrayList.get(position);
        }

        public ContactsRecyclerViewAdapter(Context context, ArrayList<ContactStructure> contacts) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mDrawableBuilder1 = TextDrawable.builder()
                    .round();
            contactsArrayList = contacts;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_view, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.usernameTextView.setText(contactsArrayList.get(position).getUsername());
            holder.txtPhoneNo.setText(contactsArrayList.get(position).getPhoneNo());
            holder.txtonlineStatus.setText(contactsArrayList.get(position).getOnlineStatus());
            holder.txtStatus.setText(contactsArrayList.get(position).getStatus());
            holder.usernameTextView.setTypeface(Constants.getRobotoBold(holder.usernameTextView.getContext()));
            holder.txtStatus.setTypeface(Constants.getRobotoMedium(holder.txtStatus.getContext()));
           // holder.lastSeenTv.setText(contactsArrayList.get(position).getLastSeen());
           // holder.chatIdTv.setText(contactsArrayList.get(position).getChatId());

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            int color = generator.getRandomColor();
            TextDrawable drawable = mDrawableBuilder1.build(contactsArrayList.get(position).getUsername().substring(0,1).toUpperCase(),color);
            //holder.userAvatar.setImageDrawable(drawable);

            holder.txtUserId.setText(contactsArrayList.get(position).getUserId());

           // Toast.makeText( holder.mView.getContext(),Constants.CHAT_SERVER_URL+"/profile_images/"+contactsArrayList.get(position).getImageAvatar(),Toast.LENGTH_LONG).show();
            Glide.with(holder.userAvatar.getContext())
                    .load( Constants.IMG_URL+contactsArrayList.get(position).getImageAvatar())
                    .fitCenter().placeholder(R.drawable.placeholder_user)
                    .into(holder.userAvatar);


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    String username =  holder.usernameTextView.getText().toString();
                    String user_id =   holder.txtUserId.getText().toString();
                    String phoneNo =   holder.txtPhoneNo.getText().toString();
                   // String chat_id = holder.chatIdTv.getText().toString();
                    //String lastSeen = holder.lastSeenTv.getText().toString();

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
            return contactsArrayList.size();
        }
    }

    class LoadAllContacts extends AsyncTask<JSONArray, Void, Void> {


        @Override
        protected Void doInBackground(JSONArray... params) {

            ContactsDb contactsDb = new ContactsDb(getActivity().getApplicationContext());
            try {
                contactsDb.open();

                contacts = params[0];
                System.out.println(contacts);

                // looping through All Contacts

                for (int i = 0; i < contacts.length(); i++) {
                   // if (contacts.getJSONObject(i) instanceof JSONObject) {
                        JSONObject object = contacts.getJSONObject(i);
                        // System.out.println(object);
                        if (object != null) {
                            // Storing each json item in variable
                            String username = object.getString(TAG_USERNAME);
                            String status = object.getString(TAG_STATUS);
                            String user_id = object.getString(TAG_USER_ID);
                            String phone = object.getString(TAG_PHONE_NO);
                            String profilePhoto = object.getString(TAG_PROFILE_PHOTO);
                            String lastSeen = object.getString(TAG_LAST_SEEN);
                            contactsDb.createEntry(user_id, username, phone, status, profilePhoto, lastSeen);
                        }
                    }

               // }
                contactsDb.close();


                //  Toast.makeText(FCMLoginActivity.this,"Login successful",Toast.LENGTH_SHORT).show();

            } catch (SQLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

            /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Contacts Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
       //     pDialog.show();*/
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

           readDbContacts();
            // pDialog.dismiss();
        }

    }

    private void readDbContacts() {
        ContactsDb contactsDb = new ContactsDb(getActivity().getApplicationContext());
        try {
            contactsDb.open();

            //Now Read from Local Db
            ArrayList<ContactStructure> contactStructures = GetContacts( contactsDb.getData());
            contactsDb.close();
            if(contactStructures != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                recyclerView.setAdapter(new ContactsRecyclerViewAdapter(getActivity(),contactStructures));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<ContactStructure> GetContacts(ArrayList<HashMap<String, String>> contactList) {
        ArrayList<ContactStructure> contacts = new ArrayList<>();

        if(contactList != null){

            int contactListSize =  contactList.size();
            for(int i = 0; i < contactListSize; i++){

                HashMap<String, String> map = contactList.get(i);

                ContactStructure c1 = new ContactStructure();
                c1.setUsername(map.get(TAG_USERNAME));
                c1.setChatId(Integer.parseInt(map.get(TAG_CHAT_ID)));
                c1.setLastSeen(map.get(TAG_LAST_SEEN));
                c1.setPhoneNo(map.get(TAG_PHONE_NO));
                c1.setStatus(map.get(TAG_STATUS));
                c1.setOnlineStatus("offline");
                c1.setUserId(map.get(TAG_USER_ID));
                c1.setImageAvatar(map.get(TAG_PROFILE_PHOTO));

                contacts.add(c1);
            }
            return contacts;
        }else{
            return null;
        }

    }
}
