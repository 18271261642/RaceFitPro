package com.example.bozhilun.android.bzlmaps.sos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import java.util.Date;

public abstract class PhonecallReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //因为传递的传入仅在振铃时有效


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action == null)
            return;
        Bundle bundle = intent.getExtras();
        if(bundle == null)
            return;
        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (action.equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = bundle.getString("android.intent.extra.PHONE_NUMBER");
            Log.e("TAG", "===savedNumber===" + savedNumber);
        } else {
            String stateStr = bundle.getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(WatchUtils.isEmpty(stateStr))
                return;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, state, number);
        }
    }

    //Derived classes should override these to respond to specific events of interest
    protected abstract void onIncomingCallReceived(Context ctx, String number, Date start);

    protected abstract void onIncomingCallAnswered(Context ctx, String number, Date start);

    protected abstract void onIncomingCallEnded(Context ctx, String number, Date start, Date end);

    protected abstract void onOutgoingCallStarted(Context ctx, String number, Date start);

    protected abstract void onOutgoingCallEnded(Context ctx, String number, Date start, Date end);

    protected abstract void onMissedCall(Context ctx, String number, Date start);
    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING://响铃状态
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallReceived(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK://呼叫状态
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {//呼叫状态
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, !WatchUtils.isEmpty(savedNumber) ? savedNumber : number, callStartTime);
                } else {
                    isIncoming = true;
                    callStartTime = new Date();
                    onIncomingCallAnswered(context, !WatchUtils.isEmpty(savedNumber) ? savedNumber : number, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE://挂断
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, !WatchUtils.isEmpty(savedNumber) ? savedNumber : number, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, !WatchUtils.isEmpty(savedNumber) ? savedNumber : number, callStartTime, new Date());
                } else {
                    onOutgoingCallEnded(context, !WatchUtils.isEmpty(savedNumber) ? savedNumber : number, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }

}