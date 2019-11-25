package com.maakki.maakkiapp;

public class Friend {
    private String friend_type,nickname, picfilepath;
    private Integer memid, maakkiid;
    private boolean isNotify;
    private long id,lastMessageTime,lastchattime;

    public Friend() {}

    public void setId( long id){this.id=id;}
    public long getId(){return id;}

    public void setMaakkiid(int maakkiid){this.maakkiid=maakkiid;}
    public int getMaakkiid(){return maakkiid;}

    public String getFriendType() {
        return friend_type;
    }
    public void setFriendType(String friend_type) { this.friend_type = friend_type;}

    public int getMemid() {
        return memid;
    }
    public void setMemid(int memid) { this.memid = memid;}

    public String getNickName() {
        return nickname;
    }
    public void setNickName(String nickname) {
        this.nickname = nickname;
    }

    public boolean getIsNotify() {
        return isNotify;
    }
    public void setIsNotify(boolean isNotify) {
        this.isNotify = isNotify;
    }


    public String getPicfilePath() {
        return picfilepath;
    }
    public void setPicfilePath(String picfilepath) {
        this.picfilepath = picfilepath;
    }

    public Long getLastMessageTime() {
        return lastMessageTime;
    }
    public void setLastMessageTime(Long lastMessageTime) {this.lastMessageTime = lastMessageTime;}

    public Long getLastChatTime() {
        return lastchattime;
    }
    public void setLastChatTime(Long lastchattime) {this.lastchattime = lastchattime;}


}
