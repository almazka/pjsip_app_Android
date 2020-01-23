package com.telefon.ufanet.MVP.Data;

public class Token {
    //@SerializedName("access_token")
    String access_token = "";

    //@SerializedName("token_type")
   String token_type = "";

   public String getAccess_token () {
       return  access_token;
   }

   public String getToken_type() {
       return token_type;
   }

}
