package com.telefon.ufanet.MVP.VOIP;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnInstantMessageParam;
import org.pjsip.pjsua2.OnRegStateParam;

import java.util.ArrayList;

public class PJSIPAccount extends Account
{
    public ArrayList<PJSIPBuddy> buddyList = new ArrayList<PJSIPBuddy>();
    public AccountConfig cfg;

    PJSIPAccount(AccountConfig config)
    {
	super();
	cfg = config;
    }

    public PJSIPBuddy addBuddy(BuddyConfig bud_cfg)
    {
	/* Create Buddy */
	PJSIPBuddy bud = new PJSIPBuddy(bud_cfg);
	try {
	    bud.create(this, bud_cfg);
	} catch (Exception e) {
	    bud.delete();
	    bud = null;
	}

	if (bud != null) {
	    buddyList.add(bud);
	    if (bud_cfg.getSubscribe())
		try {
		    bud.subscribePresence(true);
	    } catch (Exception e) {}
	}

	return bud;
    }

    public void delBuddy(PJSIPBuddy buddy)
    {
	buddyList.remove(buddy);
	buddy.delete();
    }

    public void delBuddy(int index)
    {
	PJSIPBuddy bud = buddyList.get(index);
	buddyList.remove(index);
	bud.delete();
    }

    @Override
    public void onRegState(OnRegStateParam prm)
    {
        PJSIPApp.observer.notifyRegState(prm.getCode(), prm.getReason(),
				      prm.getExpiration());
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm)
    {
	System.out.println("======== Incoming call ======== ");
	PJSIPCall call = new PJSIPCall(this, prm.getCallId());
	PJSIPApp.observer.notifyIncomingCall(call);
    }

    @Override
    public void onInstantMessage(OnInstantMessageParam prm)
    {
	System.out.println("======== Incoming pager ======== ");
	System.out.println("From     : " + prm.getFromUri());
	System.out.println("To       : " + prm.getToUri());
	System.out.println("Contact  : " + prm.getContactUri());
	System.out.println("Mimetype : " + prm.getContentType());
	System.out.println("Body     : " + prm.getMsgBody());
    }
}
