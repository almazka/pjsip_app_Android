package com.telefon.ufanet.MVP.VOIP;

public interface ITelephony {
    boolean endCall();
    void answerRingingCall();
    void silenceRinger();
}