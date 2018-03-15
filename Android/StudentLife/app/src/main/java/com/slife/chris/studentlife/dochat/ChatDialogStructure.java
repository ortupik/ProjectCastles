package com.slife.chris.studentlife.dochat;

/**
 * Created by CHRIS on 3/31/2016.
 */
public class ChatDialogStructure {

    private String sender ="";
    private String message = "";
    private String time ="";
    private String messageId ="";
    private String filepath ="";
    private String tempMessageId = "";
    private String delivered ="";

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getTempMessageId() {
        return tempMessageId;
    }

    public void setTempMessageId(String tempMessageId) {
        this.tempMessageId = tempMessageId;
    }

    public String getDelivered() {
        return delivered;
    }

    public void setDelivered(String delivered) {
        this.delivered = delivered;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type ="";

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }




    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }






}
