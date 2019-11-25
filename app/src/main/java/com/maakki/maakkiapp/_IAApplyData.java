package com.maakki.maakkiapp;

/**
 * Created by ryan on 2016/8/16.
 */
public class _IAApplyData {
    private String apply_date,sublocality,locality,area,territory,remittance_time,remittance_name,remittance_account,cashier_account_name,cashier_account_no,cashier_branch_name,cashier_bank_name,refund_date,applyRefund_date,cancel_date,grant_date,trade_date,create_date,nickname, connid, createdate, picfile;
    private int apply_status,applicant_id,cashier_id,leader1_id,leader2_id,leader3_id,referral_id,apply_id,maakki_id;

    public _IAApplyData() {
    }
    public int getApply_id(){ return apply_id; }
    public void setApply_id(int apply_id){ this.apply_id=apply_id; }

    public int getApplicant_id(){ return applicant_id; }
    public void setApplicant_id(int applicant_id){ this.applicant_id=applicant_id; }

    public int getApply_status() { return apply_status; }
    public void setApply_status(int apply_status){ this.apply_status=apply_status; }

    public String getApply_date() { return apply_date; }
    public void setApply_date(String apply_date){ this.apply_date=apply_date; }

    public String getCancel_date() { return cancel_date; }
    public void setCancel_date(String cancel_date){ this.cancel_date=cancel_date; }

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
}
