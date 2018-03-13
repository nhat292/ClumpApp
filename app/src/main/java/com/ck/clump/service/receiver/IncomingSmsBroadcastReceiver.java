package com.ck.clump.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.ck.clump.model.event.VerificationCodeReceivedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IncomingSmsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        Object[] pdusObj = (Object[]) bundle.get("pdus");
        for (int i = 0; i < pdusObj.length; i++) {
            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
            String message = currentMessage.getDisplayMessageBody();
            if (message.indexOf("Clump") != -1) {
                Matcher matcher = Pattern.compile("[0-9]{4}").matcher(message);
                if (matcher.find()) {
                    String code = matcher.group(0);
                    VerificationCodeReceivedEvent event = new VerificationCodeReceivedEvent(code);
                    EventBus.getDefault().post(event);
                }
            }
        }
    }
}
