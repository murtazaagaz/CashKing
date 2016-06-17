package com.hackerkernel.cashking.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    @Bind(R.id.mobile_EditText) EditText mobileEditText;
    @Bind(R.id.password_EditText) EditText passwordEditText;
    @Bind(R.id.relative) View layoutForSnackbar;

    @Bind(R.id.login_Btn) Button loginBtn;
    private RequestQueue mRequestQue;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //init volley
        mRequestQue = MyVolley.getInstance().getRequestQueue();

        //init progressdialog
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.processing));
        pd.setCancelable(false);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = mobileEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                checkInternetAndDoLogin(mobile, password);
            }
        });
    }

    private void checkInternetAndDoLogin(String mobile, String password) {
        if (Util.isNetworkAvailable()){

            if (mobile.isEmpty() || password.isEmpty()){
                Util.showRedSnackbar(layoutForSnackbar,getString(R.string.fill_in_all_the_fields));
                return;
            }

            doLoginInBackground(mobile,password);
        }else {
            Util.noInternetSnackBar(this,layoutForSnackbar);
        }
    }

    private void doLoginInBackground(final String mobile, final String password) {
        pd.show();
        StringRequest req = new StringRequest(Request.Method.POST, EndPoints.REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseDataInBackground(response, mobile);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                error.printStackTrace();
                Log.e(TAG,"MUR: doLoginInBackground: "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                Util.showRedSnackbar(layoutForSnackbar,errorString);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(mobile));
                params.put(Constants.COM_MOBILE,mobile);
                params.put(Constants.COM_PASSWORD,password);
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
            }else {
                Util.showRedSnackbar(layoutForSnackbar,message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"MUR:"+e);
            Util.showParsingErrorAlert(this);
        }
    }

}
