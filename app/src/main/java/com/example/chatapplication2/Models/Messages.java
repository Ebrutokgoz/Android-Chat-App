package com.example.chatapplication2.Models;

public class Messages {
    private String sender, receiver, kind, messageText, messageId, time, date, name;

    public Messages(){
    }

    public Messages(String sender, String receiver, String kind, String messageText, String messageId, String time, String date, String name){
        this.sender = sender;
        this.receiver = receiver;
        this.kind = kind;
        this.messageText = messageText;
        this.messageId = messageId;
        this.date = date;
        this.time = time;
        this.name = name;
    }

    public String getSender(){ return sender; }
    public void setSender(String sender){this.sender = sender;}

    public String getReceiver(){ return receiver; }
    public void setReceiver(String receiver){this.receiver = receiver;}

    public String getKind(){ return kind; }
    public void setKind(String kind){this.kind = kind;}

    public String getMessageText(){ return messageText; }
    public void setMessageText(String messageText){this.messageText = messageText;}

    public String getMessageId(){ return messageId; }
    public void setMessageId(String messageId){this.messageId = messageId;}

    public String getDate(){ return date; }
    public void setDate(String date){this.date = date;}

    public String getTime(){ return time; }
    public void setTime(String time){this.time = time;}

    public String geName(){ return name; }
    public void setName(String name){this.name = name;}
}
