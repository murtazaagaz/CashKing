package com.hackerkernel.cashking.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.activity.WalletActivity;
import com.hackerkernel.cashking.constants.Constants;
import com.hackerkernel.cashking.constants.EndPoints;
import com.hackerkernel.cashking.network.MyVolley;
import com.hackerkernel.cashking.parser.JsonParsor;
import com.hackerkernel.cashking.pojo.DealsListPojo;
import com.hackerkernel.cashking.pojo.SimplePojo;
import com.hackerkernel.cashking.storage.MySharedPreferences;
import com.hackerkernel.cashking.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PendingOffersVerificationService extends IntentService {

    private static final String TAG = PendingOffersVerificationService.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private String mMobile;
    private MySharedPreferences sp;

    private List<DealsListPojo> mPendingOfferList;

    public PendingOffersVerificationService() {
        super("PendingOffersVerificationService");
        //create Volley singleton
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        //init sp
        sp = MySharedPreferences.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "HUS: onHandleIntent");

        if (intent != null) {
            //check intent has Mobile number of logged in user
            if (intent.hasExtra(Constants.COM_MOBILE)) {
                mMobile = intent.getExtras().getString(Constants.COM_MOBILE);

                /*
                * Check internet & fetch offer list
                * */
                if (Util.isNetworkAvailable()){
                    fetchPendingOfferListInBackground();
                }
            }
        }else{
            Log.d(TAG,"HUS: onHandleIntent was null");
        }
    }

    /*
    * Method to fetch pending offer list in background
    * */
    private void fetchPendingOfferListInBackground() {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.OFFERLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"HUS: fetchPendingOfferListInBackground: "+response);
                parsePendingOfferListResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG,"HUS: fetchPendingOfferListInBackground: "+error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(mMobile));
                params.put(Constants.COM_MOBILE,mMobile);
                params.put(Constants.COM_TYPE,Constants.OFFER_TYPE_PENDING);
                return params;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        mRequestQueue.add(request);
    }

    /*
    * METHOD to parse pending offer list response
    *
    * */
    private void parsePendingOfferListResponse(String response) {
        try {
            JSONObject jo = new JSONObject(response);
            boolean returned = jo.getBoolean(Constants.COM_RETURN);
            int count = jo.getInt(Constants.COM_COUNT);

            //check returned is true means success
            if (returned && count > 0){
                JSONArray ja = jo.getJSONArray(Constants.COM_DATA);
                mPendingOfferList = JsonParsor.parseDealsList(ja, Constants.OFFER_TYPE_PENDING);
                /*
                * Method to verify that did we get conversion on pending offer
                * */
                verifyPendingOffers();
            }else{
                Log.d(TAG,"HUS: parsePendingOfferListResponse: returned & count are not > zero");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "HUS: parsePendingOfferListResponse: " + e.getMessage() + " response: " + response);
        }
    }

    /*
    * Method to verify that pending offers get conversion or not
    *
    * */
    private void verifyPendingOffers(){

        //Check we have some pending offers (Just for safety)
        if (mPendingOfferList.size() > 0){

            //Loop through all the list
            for (int i = 0; i < mPendingOfferList.size(); i++) {
                DealsListPojo current = mPendingOfferList.get(i);
                final String currentOfferId = current.getId();
                final String currentAffOfferId = current.getAffOfferId();
                final String currentNetworkId =  current.getNetworkId();
                final int finalI = i;

                StringRequest request = new StringRequest(Request.Method.POST, EndPoints.VERIFY_PENDING_OFFER, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG,"HUS: verifyPendingOffers: "+response);
                        parseVerifyPendingResponse(response, finalI);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG,"HUS: verifyPendingOffers: "+error.getMessage());
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> param = new HashMap<>();
                        param.put(Constants.COM_APIKEY,Util.generateApiKey(mMobile));
                        param.put(Constants.COM_MOBILE,mMobile);
                        param.put(Constants.COM_AFF_OFFER_ID,currentAffOfferId);
                        param.put(Constants.COM_OFFER_ID,currentOfferId);
                        param.put(Constants.COM_NETWORK_ID,currentNetworkId);
                        return param;
                    }
                };

                int socketTimeout = 30000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                request.setRetryPolicy(policy);
                //add to request queue
                mRequestQueue.add(request);
            }

        }else{
            Log.e(TAG,"HUS: verifyPendingOffers: mPendingOfferList was smaller then 1 "+mPendingOfferList.size());
        }
    }


    private void parseVerifyPendingResponse(String response,int listIndex) {
        try {

            SimplePojo resultList = JsonParsor.SimpleParser(response);

            //true success (credit the money)
            if (resultList.isReturned()){
                creditTheOfferAmount(listIndex);
            }else{
                Log.d(TAG,"HUS: parseResponse: response "+resultList.getMessage());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"HUS: parseResponse: "+e.getMessage());
        }
    }

    /*
    * Method to credit amount and notify user is conversion was successfull
    *
    * */
    private void creditTheOfferAmount(int listIndex) {
        //Store amount in SP
        DealsListPojo current = mPendingOfferList.get(listIndex);
        //amount from offer
        String amount = current.getDealAmount();
        //old amount wallet amount of user
        String oldWalletAmount = sp.getWalletAmount();
        //new wallet amount of user(Add offer amount + old user wallet amount)
        int newWalletAmount = Integer.parseInt(oldWalletAmount) + Integer.parseInt(amount);
        //store amount to sp
        sp.setWalletAmount(newWalletAmount+"");

        /*
        * notify user that conversion is done
        * */
        notifyUserAboutTheConversion(listIndex);
    }

    /*
    * Method to create a notification to notify user about the conversion
    *
    * */
    private void notifyUserAboutTheConversion(int listIndex) {
        //amount
        DealsListPojo current = mPendingOfferList.get(listIndex);
        String amount = current.getDealAmount();
        String offerName = current.getDealName();

        //notification id
        int notificationId = (int) System.currentTimeMillis();

        //add sound to notification
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager manager = (NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(getApplicationContext(), WalletActivity.class);

        //intent to open wallet when conversion is done
        Intent intent = new Intent(getApplicationContext(), WalletActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, listIndex, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Congratulations!")
                .setSound(notificationSound)
                .setContentIntent(pendingIntent)
                .setContentText("You just earn "+amount+"\u20B9 for "+offerName);

        manager.notify(notificationId,builder.build());
    }
}
