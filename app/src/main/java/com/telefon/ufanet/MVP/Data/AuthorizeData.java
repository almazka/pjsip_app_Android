package com.telefon.ufanet.MVP.Data;

public class AuthorizeData {

    public static final AuthorizeData INSTANCE = new AuthorizeData();

    public AuthorizeData(){}

    public static AuthorizeData getInstance() {
        return INSTANCE;
    }

    private String name;
    private String password;
    private boolean isChecked;
    private String sip_user;
    private String sip_password;
    private String user_token;
    private String api_token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked (Boolean checked) {
        this.isChecked = checked;
    }

    public void setSip_user (String sip_user) {
        this.sip_user = sip_user;
    }

    public String getSip_user() {
        return sip_user;
    }

    public void setSip_password (String sip_password) {
        this.sip_password = sip_password;
    }

    public String getSip_password() {
        return sip_password;
    }

    public void setUser_token (String user_token) {
        this.user_token = user_token;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setApi_token (String api_token) {
        this.api_token = api_token;
    }

    public String getApi_token(){
        return api_token;
    }

}
