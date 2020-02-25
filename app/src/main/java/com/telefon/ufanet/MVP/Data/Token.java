package com.telefon.ufanet.MVP.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("access_token")
    @Expose
    String access_token;

    @SerializedName("token_type")
    @Expose
    String token_type;

    public String getAccess_token () {
       return  access_token;
   }

    public String getToken_type() {
       return token_type;
   }

    public Token(String access_token, String token_type) {
        this.access_token = access_token;
        this.token_type = token_type;
    }

    public Token() {
    }
}
