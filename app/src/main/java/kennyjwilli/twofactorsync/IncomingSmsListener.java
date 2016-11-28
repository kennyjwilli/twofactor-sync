package kennyjwilli.twofactorsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by kenny on 10/17/16.
 */

public class IncomingSmsListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs;
            String format = bundle.getString("format");
            //String msgFrom;
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    } else {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    SmsMessage msg = msgs[i];
                    //msgFrom = msg.getOriginatingAddress();
                    String msgBody = msg.getMessageBody();
                    if (Util.hasVerificationCode(msgBody, Util.codeMatchers)) {
                        String maybeCode = Util.findBestCode(msgBody);
                        if (maybeCode != null) {
                            Log.i(Util.TAG, "received code: " + maybeCode);
                            Util.copyToClipboard(ctx, maybeCode);
                            Toast.makeText(ctx, "Copied code " + maybeCode, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }
}
