
package com.slife.chris.studentlife.dochat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.slife.chris.studentlife.R;
import com.slife.chris.studentlife.utilities.Constants;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatDialogAdapter extends BaseAdapter {

    private ArrayList<ChatDialogStructure> chatMessages = new ArrayList<>();
    private Activity context;
    private static boolean isOutgoing;

    private enum ChatItemType {
        Message,
        Sticker
    }

    public void addDbMessages( ArrayList<ChatDialogStructure> chatMessages){
        this.chatMessages = chatMessages;
    }
    public void removeAll(){
        chatMessages.clear();
    }
    public ChatDialogAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public ChatDialogStructure getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }
    //when message is received -- update tick
    public String updateMessage(String temp_message_id) {
             for(ChatDialogStructure c : chatMessages){
                if(c.getMessageId().equals(temp_message_id)){
                    return temp_message_id;
                }
             }
        return "nothing";
    }

    @Override
    public int getViewTypeCount() {
        return ChatItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return ChatItemType.Message.ordinal();

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;
        ChatDialogStructure chatDialogStructure = getItem(position);

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        isOutgoing = (chatDialogStructure.getSender().equals("Me"));
        String typeOfMessage = chatDialogStructure.getType();

        if (convertView == null) {
            convertView = setAlignment(vi,parent,typeOfMessage,isOutgoing);
            holder = createViewHolder(convertView,typeOfMessage,isOutgoing);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(holder != null){
            setHolderValues(holder,chatDialogStructure,typeOfMessage,isOutgoing);
        }



        return convertView;
    }

    private void setHolderValues(ViewHolder holder, ChatDialogStructure chatDialogStructure, String typeOfMessage, boolean isOutgoing) {
        // holder.txtMessageId.setText(chatDialogStructure.getMessageId());

            if (holder.txtTime != null) {
                holder.txtTime.setText(getTimeText(chatDialogStructure.getTime()));
            }
            if (isOutgoing) {
                if (holder.msgSentTick != null) {
                    if (chatDialogStructure.getDelivered().equals("1"))
                        holder.msgSentTick.setBackgroundResource(R.drawable.ic_qiscus_read);
                    else
                        holder.msgSentTick.setBackgroundResource(R.drawable.ic_qiscus_sending);
                }
            } else {
                if (chatDialogStructure.getDelivered().equals("3")) {
                    holder.msgSentTick.setBackgroundResource(R.drawable.msg_clock2_s);
                }
            }

            if (typeOfMessage.equals("text")) {
                if (holder.txtMessage != null) {
                    holder.txtMessage.setText(chatDialogStructure.getMessage());
                }
            } else if (typeOfMessage.equals("image")) {
                if (holder.attachedImage != null && holder.imageLoadHolder != null) {


                    holder.imageLoadHolder.setVisibility(View.INVISIBLE);
                    Glide.with(holder.attachedImage.getContext())
                            .load(Constants.CHAT_SERVER_URL + "/" + chatDialogStructure.getFilepath())
                            .placeholder(R.drawable.placeholder_user)
                            .centerCrop()
                            .into(holder.attachedImage);


                }
                if (holder.txtCaption != null) {
                    holder.txtCaption.setText(chatDialogStructure.getMessage());
                }

            }

        }


    public void add(ChatDialogStructure message) {
        chatMessages.add(message);
    }

    public void add(ArrayList<ChatDialogStructure> messages) {
        chatMessages.addAll(messages);
    }


    private View setAlignment(LayoutInflater vi, ViewGroup parent, String typeOfMessage, boolean isOutgoing) {
        View convertView = null;
        if (isOutgoing) {
            if(typeOfMessage.equals("text")){
                 convertView = vi.inflate(R.layout.item_qiscus_chat_text_me, parent, false);
            }else if(typeOfMessage.equals("image")){
                 convertView = vi.inflate(R.layout.item_chat_img_me, parent, false);
            }else if(typeOfMessage.equals("doc")){
                 convertView = vi.inflate(R.layout.item_qiscus_chat_file_me, parent, false);
            }else if(typeOfMessage.equals("audio")){
                 convertView = vi.inflate(R.layout.item_qiscus_chat_audio_me, parent, false);
            } else if(typeOfMessage.equals("file")){
                 convertView = vi.inflate(R.layout.item_qiscus_chat_file_me, parent, false);
            }
        }else{
            if(typeOfMessage.equals("text")){
                 convertView = vi.inflate(R.layout.item_qiscus_chat_text, parent, false);
            }else if(typeOfMessage.equals("image")){
                 convertView = vi.inflate(R.layout.item_chat_img, parent, false);
            }else if(typeOfMessage.equals("doc")){
                 convertView = vi.inflate(R.layout.item_qiscus_chat_file, parent, false);
            }else if(typeOfMessage.equals("audio")){
                 convertView = vi.inflate(R.layout.item_qiscus_chat_audio, parent, false);
            } else if(typeOfMessage.equals("file")){
                 convertView = vi.inflate(R.layout.item_qiscus_chat_file, parent, false);
            }
        }
        return convertView;
    }

    private ViewHolder createViewHolder(View v, String typeOfMessage, boolean isOutgoing) {
        ViewHolder holder = new ViewHolder();




        holder.txtTime = (TextView) v.findViewById(R.id.date);
        if(isOutgoing){
            holder.msgSentTick = (ImageView) v.findViewById(R.id.icon_read);
        }


        if(typeOfMessage.equals("text")){
            holder.txtMessage = (TextView)v.findViewById(R.id.contents);
        }else if(typeOfMessage.equals("image")){
            holder.attachedImage = (ImageView) v.findViewById(R.id.frame);
            holder.imageLoadHolder = (RelativeLayout) v.findViewById(R.id.holder);
            holder.txtCaption = (TextView) v.findViewById(R.id.caption);
        }



       /* holder.txtMessageId = (TextView) v.findViewById(R.id.messageId);
        holder.msgServerTick = (ImageView) v.findViewById(R.id.msgServer);
        holder.leftImageView = (CircleImageView) v.findViewById(R.id.leftImageView);
        holder.rightImageView = (CircleImageView) v.findViewById(R.id.rightImageView);*/

        return holder;
    }

    private String getTimeText(String time) {
        if(time.length()> 15) {
            String formattedTime = time.substring(11, 16);
            int hour = Integer.parseInt(time.substring(11, 13))+3;
            String ext = null;
            if (hour >= 12) {
                hour = hour - 12;
                formattedTime = String.valueOf(hour) + time.substring(13, 16);
                ext = "PM";
            } else {
                ext = "AM";
            }
            return formattedTime + " " + ext;
        }else{
            return time;

        }
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtCaption;
        public TextView txtTime;
        private ImageView msgServerTick;
        private ImageView msgSentTick;
        private TextView txtMessageId;
        private ImageView attachedImage;
        private CircleImageView leftImageView;
        private CircleImageView rightImageView;
        private RelativeLayout imageLoadHolder;


    }
}
