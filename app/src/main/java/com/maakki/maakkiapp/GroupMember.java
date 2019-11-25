package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/8/16.
 */
public class GroupMember {
    private int maakki_id,member_status;
    private long id,uniquegroupId;
    public GroupMember(){
    }
    //serial id
    public long getId(){ return id; }
    public void setId(long id){ this.id=id;}
    //联盟独特id
    public long getUniqueGroupId(){ return uniquegroupId;}
    public void setUniqueGroupId(long uniquegroupId){ this.uniquegroupId=uniquegroupId;}
    //盟主id
    public int getMaakki_id(){ return maakki_id; }
    public void setMaakki_id(int maakki_id){ this.maakki_id=maakki_id; }
    //0:群主 1:资料管理员 2.一般会员 3.邀请中 4.申请中 5.已拒绝（可接受再申请）6.申请退出 7.已经退出 99.黑名单（拒绝再接受申请）
    //除了盟主之外，前两名都是 Data_keeper，负责保管资料
    public int getMember_status() { return member_status; }
    public void setMember_status(int member_status){ this.member_status=member_status; }    
}
