package com.hyd.dao.models;

public class WxUserPreorder {

    private int id;

    private String openid;

    private String examNo;

    private String studentName;

    private String areaCode;

    private String townCode;

    private java.sql.Timestamp preorderTime;

    private String mobile;

    private String smsStatus;

    private java.sql.Timestamp smsTime;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOpenid() {
        return this.openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getExamNo() {
        return this.examNo;
    }

    public void setExamNo(String examNo) {
        this.examNo = examNo;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAreaCode() {
        return this.areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getTownCode() {
        return this.townCode;
    }

    public void setTownCode(String townCode) {
        this.townCode = townCode;
    }

    public java.sql.Timestamp getPreorderTime() {
        return this.preorderTime;
    }

    public void setPreorderTime(java.sql.Timestamp preorderTime) {
        this.preorderTime = preorderTime;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSmsStatus() {
        return this.smsStatus;
    }

    public void setSmsStatus(String smsStatus) {
        this.smsStatus = smsStatus;
    }

    public java.sql.Timestamp getSmsTime() {
        return this.smsTime;
    }

    public void setSmsTime(java.sql.Timestamp smsTime) {
        this.smsTime = smsTime;
    }

}