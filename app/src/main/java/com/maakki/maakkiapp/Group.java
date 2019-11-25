package com.maakki.maakkiapp;

public class Group {
    private String iconpath,group_name,uniquegroupId;
    private int group_status;
    private long id;
    public Group(){
    }
    //serial id
    public long getId(){return id; }
    public void setId(long id){this.id=id;}

    //Integer.toHexString(int_value);Founder's id_timeStamp(GMT+8:00)
    public String getUniqueGroupId(){ return uniquegroupId;}
    public void setUniqueGroupId(String uniquegroupId){ this.uniquegroupId=uniquegroupId;}

    //Group名称
    public String getGroup_name(){return group_name;}
    public void setGroup_name(String group_name){this.group_name=group_name;}

    //Group 图案
    public String getIconpath(){return iconpath;}
    public void setIconpath(String iconpath){this.iconpath=iconpath;}

    //1：接受申请 2.不接受申请，只能由团长邀请 3.已经解散
    public  int getGroupStatus() { return group_status; }
    public void setGroupStatus(int group_status){ this.group_status=group_status; }
}
