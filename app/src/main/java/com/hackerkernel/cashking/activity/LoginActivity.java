package com.hackerkernel.cashking.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.constants.Constants;
import com.hackerkernel.cashking.constants.EndPoints;
import com.hackerkernel.cashking.infrastructure.BaseActivity;
import com.hackerkernel.cashking.network.MyVolley;
import com.hackerkernel.cashking.storage.MySharedPreferences;
import com.hackerkernel.cashking.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    @Bind(R.id.mobile_EditText)
    EditText mobileEditText;
    @Bind(R.id.password_EditText) EditText passwordEditText;
    @Bind(R.id.relative)
    RelativeLayout relative;

    @Bind(R.id.login_Btn)
    Button loginBtn;
    private RequestQueue mRequestQue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mRequestQue = MyVolley.getInstance().getRequestQueue();
    loginBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String mobile = mobileEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            checkIfNetworkAvaillble(mobile, password);
        }
    });
    }

    private void checkIfNetworkAvaillble(String mobile, String password) {
        if (Util.isNetworkAvailable()){
            getDataInBackground(mobile,password);
        }
    }

    private void getDataInBackground(final String mobile, final String password) {
        StringRequest req = new StringRequest(Request.Method.POST, EndPoints.REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseDataInBackground(response, mobile);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            String errorString = MyVolley.handleVolleyError(error);
                Util.showRedSnackbar(relative,errorString);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Key",mobile);
                params.put("Key",password);
                return params;
            }
        };
        mRequestQue.add(req);

    }

    private void parseDataInBackground(String response, String mobile) {
        try {
            JSONObject obj = new JSONObject(response);
            boolean returned = obj.getBoolean(Constants.COM_RETURN);
            String message = obj.getString(Constants.COM_MESSAGE);
            if (returned){
                MySharedPreferences.getInstance(this).setUserMobile(mobile);
                Util.goToHomeActivity(this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"MUR:"+e);
            Util.showParsingErrorAlert(this);
        }


    }

}
