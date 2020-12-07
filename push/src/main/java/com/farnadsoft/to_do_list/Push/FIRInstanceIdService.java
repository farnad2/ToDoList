package com.farnadsoft.to_do_list.Push;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.farnadsoft.to_do_list.Server.Server;
import com.farnadsoft.to_do_list.Server.ServerConfig;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by BLiveInHack on 11-06-2016.
 */
public class FIRInstanceIdService extends FirebaseInstanceIdService {
    private RequestParams params;


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("Token", refreshedToken);
        FirebaseMessaging.getInstance().subscribeToTopic("global");
        FirebaseMessaging.getInstance().subscribeToTopic(ServerConfig.APP_TYPE);
        sendRegistrationIdToBackend(refreshedToken);
    }

    private void sendRegistrationIdToBackend(String refreshedToken) {
        params = new RequestParams();
        params.put(PushUser.EMAIL, FCMActivity.getRegistrationEmail(this));
        params.put(PushUser.APP_TYPE, ServerConfig.APP_TYPE);
        params.put(PushUser.TOKEN, refreshedToken);
        params.put(PushUser.DEVICE_MODEL, DeviceUtils.getDeviceModel());
        params.put(PushUser.DEVICE_API, DeviceUtils.getDeviceAPILevel());
        params.put(PushUser.DEVICE_OS, DeviceUtils.getDeviceOS());
        params.put(PushUser.DEVICE_NAME, DeviceUtils.getDeviceName());
        params.put(PushUser.TIMEZONE, DeviceUtils.getDeviceTimeZone());
        params.put(PushUser.LAST_LAT, DeviceUtils.getLastlat(this) + "");
        params.put(PushUser.LAST_LONG, DeviceUtils.getLastLng(this) + "");
        params.put(PushUser.MEMORY, DeviceUtils.getDeviceMemory(this) + "");
        params.put(PushUser.DEVICE_ID, DeviceUtils.getDeviceId(this) + "");
        params.put(PushUser.PIN_CODE, DeviceUtils.getPinCode(this) + "");

        //Log.e("param", params.toString());
        Server.postSync(ServerConfig.REGISTRATION_URL, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.e("Advance Push 61",response.toString());
                    }
                });
    }
}
