package com.example.mess;

public class GroupEntity {
    private String displayName;
    private String name;
    private String message;
    private String language;
    private Long time;
    private String from;

    public GroupEntity(){

    }

    public GroupEntity (String name, String message, String language, Long time, String from){
        this.name = name;
        this.message = message;
        this.language = language;
        this.time = time;
        this.from = from;
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
