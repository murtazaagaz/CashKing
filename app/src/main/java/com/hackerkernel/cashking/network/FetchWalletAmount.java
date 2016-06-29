package com.hackerkernel.cashking.network;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.activity.WalletActivity;
import com.hackerkernel.cashking.constants.Constants;
import com.hackerkernel.cashking.constants.EndPoints;
import com.hackerkernel.cashking.storage.MySharedPreferences;
import com.hackerkernel.cashking.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by QUT on 6/20/2016.
 */
public class FetchWalletAmount {
    private static final String TAG = FetchWalletAmount.class.getSimpleName();
    private RequestQueue mRequestQue;
    private MySharedPreferences sp;
    private Activity activity;
    private Button mWalletBtn;

    public FetchWalletAmount(final Activity activity){
        this.activity = activity;
        sp = MySharedPreferences.getInstance(activity);
        mRequestQue = MyVolley.getInstance().getRequestQueue();

        //init wallet button
        mWalletBtn = (Button) activity.findViewById(R.id.toolbar_wallet_btn);

        //set old wallet amount to wallet btn
        mWalletBtn.setText(activity.getString(R.string.rupee_sign)+sp.getWalletAmount());

        //open wallet activity when someone click wallet btn
        mWalletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, WalletActivity.class));
            }
        });
    }

    public void fetchNewWalletAmountInBackground(){
        //get new wallet amount from api (if internet is avaialble)
        if (Util.isNetworkAvailable()){
            fetchDataInBackground();
        }
    }

    private void fetchDataInBackground() {
        StringRequest req = new StringRequest(Request.Method.POST, EndPoints.GET_WALLET_AMOUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                sp.setWalletAmount(response);
                //set new wallet amount to btn
                mWalletBtn.setText(activity.getString(R.string.rupee_sign)+sp.getWalletAmount());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e(TAG,"HUS: "+error.getMessage());
                Toast.makeText(activity, R.string.unable_to_get_wallet_amnt,Toast.LENGTH_LONG).show();
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
