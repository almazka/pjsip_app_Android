package com.telefon.ufanet.MVP.Data;

import java.util.List;

class Num {
    public String sip_login = "";
    public String sip_password = "";
    public String getSip_login() {return  sip_login;}
    public String getSip_password() {return  sip_password;}
}

public class Sip {
    List<Num> numbers;
    public String getSipLogin() {
        return  numbers.get(0).getSip_login();
    }
    public String getSipPass() {
        return  numbers.get(0).getSip_password();
    }
}