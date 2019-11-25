package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/8/16.
 */
public class _920TradeData {
    private String sublocality,locality,area,territory,remittance_time,remittance_name,remittance_account,cashier_account_name,cashier_account_no,cashier_branch_name,cashier_bank_name,refund_date,applyRefund_date,cancel_date,grant_date,trade_date,create_date,nickname, connid, createdate, picfile;
    private int cashier_id,leader1_id,leader2_id,leader3_id,referral_id,trade_id,maakki_id,trade_status,amount;
    private double rRate,iCredit ;
    private boolean isImaimaiGrant;
    public _920TradeData() {
    }
    public int getTrade_id(){ return trade_id; }
    public void setTrade_id(int trade_id){ this.trade_id=trade_id; }

    public int getMaakki_id(){ return maakki_id; }
    public void setMaakki_id(int maakki_id){ this.maakki_id=maakki_id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname){ this.nickname=nickname; }

    public int getTrade_status() { return trade_status; }
    public void setTrade_status(int trade_status){ this.trade_status=trade_status; }

    public int getAmount() { return amount; }
    public void setAmount(int amount){ this.amount=amount; }

    public double getrRate() { return rRate; }
    public void setrRate(double rRate){ this.rRate=rRate; }

    public double getiCredit() { return iCredit; }
    public void setiCredit(double iCredit){ this.iCredit=iCredit; }

    public String getCreate_date() { return create_date; }
    public void setCreate_date(String create_date){ this.create_date=create_date; }

    public String getTrade_date() { return trade_date; }
    public void setTrade_date(String trade_date){ this.trade_date=trade_date; }

    public String getGrant_date() { return grant_date; }
    public void setGrant_date(String grant_date){ this.grant_date=grant_date; }

    public String getCancel_date() { return cancel_date; }
    public void setCancel_date(String cancel_date){ this.cancel_date=cancel_date; }

    public String getApplyRefund_date() { return applyRefund_date; }
    public void setApplyRefund_date(String applyRefund_date){ this.applyRefund_date=applyRefund_date; }

    public String getRefund_date() { return refund_date; }
    public void setRefund_date(String refund_date){ this.refund_date=refund_date; }

    public int getReferral_id() { return referral_id; }
    public void setReferral_id(int referral_id){ this.referral_id=referral_id; }

    public int getLeader3_id() { return leader3_id; }
    public void setLeader3_id(int leader3_id){ this.leader3_id=leader3_id; }

    public int getLeader2_id() { return leader2_id; }
    public void setLeader2_id(int leader2_id){ this.leader2_id=leader2_id; }

    public int getLeader1_id() { return leader1_id; }
    public void setLeader1_id(int leader1_id){ this.leader1_id=leader1_id; }

    public int getCashier_id() { return cashier_id; }
    public void setCashier_id(int cashier_id){ this.cashier_id=cashier_id; }

    public String getCashier_bank_name() { return cashier_bank_name; }
    public void setCashier_bank_name(String cashier_bank_name){ this.cashier_bank_name=cashier_bank_name; }

    public String getCashier_branch_name() { return cashier_branch_name; }
    public void setCashier_branch_name(String cashier_branch_name){ this.cashier_branch_name=cashier_branch_name; }

    public String getCashier_account_no() { return cashier_account_no; }
    public void setCashier_account_no(String cashier_account_no){ this.cashier_account_no=cashier_account_no; }

    public String getCashier_account_name() { return cashier_account_name; }
    public void setCashier_account_name(String cashier_account_name){ this.cashier_account_name=cashier_account_name; }

    public String getRemittance_account() { return remittance_account; }
    public void setRemittance_account(String remittance_account){ this.remittance_account=remittance_account; }

    public String getRemittance_name() { return remittance_name; }
    public void setRemittance_name(String remittance_name){ this.remittance_name=remittance_name; }

    public String getRemittance_time() { return remittance_time; }
    public void setRemittance_time(String remittance_time){ this.remittance_time=remittance_time; }

    public String getTerritory() { return territory; }
    public void setTerritory(String territory){ this.territory=territory; }

    public String getArea() { return area; }
    public void setArea(String area){ this.area=area; }

    public String getLocality() { return locality; }
    public void setLocality(String locality){ this.locality=locality; }

    public String getSublocality() { return sublocality; }
    public void setSublocality(String sublocality){ this.sublocality=sublocality; }

    public boolean getIsImaimaiGrant(){return isImaimaiGrant;}
    public void setIsImaimaiGrant(boolean isImaimaiGrant){this.isImaimaiGrant=isImaimaiGrant;}
}
