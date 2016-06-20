package com.hackerkernel.cashking.activity;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.cashking.constants.Constants;
import com.hackerkernel.cashking.constants.EndPoints;
import com.hackerkernel.cashking.network.MyVolley;
import com.hackerkernel.cashking.storage.MySharedPreferences;
import com.hackerkernel.cashking.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by QUT on 6/20/2016.
 */
public class FetchWallet {
    private RequestQueue mRequestQue;
    private MySharedPreferences sp;
    private Context mContext;
    public FetchWallet(Context context){
        this.mContext = context;
        sp = MySharedPreferences.getInstance(mContext);
        mRequestQue = MyVolley.getInstance().getRequestQueue();

        if (Util.isNetworkAvailable()){
            fetchDataInBackground();
        }
    }

    private void fetchDataInBackground() {
        StringRequest req = new StringRequest(Request.Method.POST, EndPoints.WALLET_AMOUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            sp.setWalletAmount(response);
            String amnt = sp.getWalletAmount();
                Log.d("TAG","MUR: fetchDataInBackground"+amnt);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String mobile = sp.getUserMobile();
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(mobile));
                params.put(Constants.COM_MOBILE,mobile);


                return params;
            }
        };
        mRequestQue.add(req);
    }
}
