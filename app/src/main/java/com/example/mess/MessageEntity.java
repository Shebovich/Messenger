package com.example.mess;

public class MessageEntity implements Comparable<MessageEntity> {
    private String displayName;
    private String name;
    private String message;
    private String language;
    private Long time;
    private String userId;
    private String messageID;
    private String to;
    public MessageEntity(){

    }

    public MessageEntity(String name, String message, String language, Long time, String userId, String messageID){
        this.name = name;
        this.message = message;
        this.language = language;
        this.time = time;
        this.userId = userId;
        this.messageID = messageID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int compareTo(MessageEntity o) {
        return this.getTime().compareTo(o.getTime());
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
