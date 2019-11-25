package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/8/16.
 */
public class CBonusData {
    private String bonus_date,bank_name,branch_name,account_no,account_name,remit_date;
    private int bonus,iCredit,bonus_from;
    private boolean isGrantCash;

    public CBonusData() {   }

    public int getBonus(){ return bonus; }
    public void setBonus(int bonus){ this.bonus=bonus; }

    public int getiCredit(){ return iCredit; }
    public void setiCredit(int iCredit){ this.iCredit=iCredit; }

    public int getBonus_from(){ return bonus_from; }
    public void setBonus_from(int bonus_from){ this.bonus_from=bonus_from; }

    public boolean getisGrantCash(){ return isGrantCash; }
    public void setisGrantCash(boolean isGrantCash){ this.isGrantCash=isGrantCash; }

    public String getBonus_date() { return bonus_date; }
    public void setBonus_date(String bonus_date){ this.bonus_date=bonus_date; }

    public String getBank_name() { return bank_name; }
    public void setBank_name(String bank_name){ this.bank_name=bank_name; }

    public String getBranch_name() { return branch_name; }
    public void setBranch_name(String branch_name){ this.branch_name=branch_name; }

    public String getAccount_no() { return account_no; }
    public void setAccount_no(String account_no){ this.account_no=account_no; }

    public String getAccount_name() { return account_name; }
    public void setAccount_name(String account_name){ this.account_name=account_name; }

    public String getRemit_date() { return remit_date; }
    public void setRemit_date(String remit_date){ this.remit_date=remit_date; }
}
