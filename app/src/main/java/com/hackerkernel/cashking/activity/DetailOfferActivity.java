package com.hackerkernel.cashking.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.constants.Constants;
import com.hackerkernel.cashking.network.MyVolley;
import com.hackerkernel.cashking.parser.JsonParsor;
import com.hackerkernel.cashking.pojo.SimplePojo;
import com.hackerkernel.cashking.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import butterknife.ButterKnife;

public class DetailOfferActivity extends AppCompatActivity {
    //TODO ADD URL PARAMS AND COMPLETE JSON PARSOR

    private RequestQueue mRequestQue;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_offer);
        ButterKnife.bind(this);

        mRequestQue = MyVolley.getInstance().getRequestQueue();
        /*if (getIntent().hasCategory("id")){
            id = getIntent().getStringExtra(id);
            checkInternetAndFetchData();
        }
        else{
            finish();
        }*/


    }

    private void checkInternetAndFetchData() {
        if (Util.isNetworkAvailable()){
            fetchDataInBackground();
        }
    }

    private void fetchDataInBackground() {
        StringRequest req = new StringRequest(Request.Method.POST, "url", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    SimplePojo pojo = JsonParsor.simpleParser(response);
                    if (pojo.isReturned()){
                       //TODO parse Data here
                        JSONObject obj = new JSONObject(response);
                        JSONArray data = obj.getJSONArray(Constants.COM_DATA);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String handleError = MyVolley.handleVolleyError(error);
                if (handleError != null){
                    //Util.showRedSnackbar(linear,handleError);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new Hashtable<>();
                params.put(Constants.COM_ID,id);
                return params;
            }
        };
        mRequestQue.add(req);

    }
}
