package com.hackerkernel.cashking.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.constants.Constants;
import com.hackerkernel.cashking.constants.EndPoints;
import com.hackerkernel.cashking.infrastructure.BaseAuthActivity;
import com.hackerkernel.cashking.network.FetchWalletAmount;
import com.hackerkernel.cashking.network.MyVolley;
import com.hackerkernel.cashking.parser.JsonParsor;
import com.hackerkernel.cashking.pojo.SimplePojo;
import com.hackerkernel.cashking.storage.MySharedPreferences;
import com.hackerkernel.cashking.util.Util;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WalletActivity extends BaseAuthActivity {

    private static final String TAG = WalletActivity.class.getSimpleName();
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.layoutForSnackbar) View mLayoutForSnackbar;
    @Bind(R.id.operator_spinner) Spinner mOperatorSpinner;
    @Bind(R.id.recharge_now) Button mRechargeNowButton;
    @Bind(R.id.mobileNumber) EditText mMobileNumberEditText;
    @Bind(R.id.amount) EditText mAmountEditText;

    private MySharedPreferences sp;
    private String mWalletAmount;
    private String mLoggedInUserMobile;

    private String[] mMobileOperatorCodes;

    private RequestQueue mRequestQueue;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Wallet");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = MySharedPreferences.getInstance(getApplicationContext());

        //instanciate progressbar
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.processing));
        pd.setCancelable(true);

        //insalize volley
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        //get logged in user mobile
        mLoggedInUserMobile = sp.getUserMobile();

        //get wallet amount from API set and store it in SP
        FetchWalletAmount fetchWalletAmount = new FetchWalletAmount(this);
        fetchWalletAmount.fetchNewWalletAmountInBackground();

        //get user wallet amount
        mWalletAmount = sp.getWalletAmount();

        //Set operator & code array
        String[] mMobileOperatorNames = getResources().getStringArray(R.array.mobile_operator_name);
        mMobileOperatorCodes = getResources().getStringArray(R.array.mobile_operator_code);


        //set Spinner with mobile operatores as default
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mMobileOperatorNames);
        mOperatorSpinner.setAdapter(adapter);
        mOperatorSpinner.setTag("mobile");

        //when recharge now button is clicked
        mRechargeNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetAndDoRecharge();
            }
        });
    }

    /*
    * Method to check internet and validate recharge values
    * this method is called when recharge now button is clicked
    * */
    private void checkInternetAndDoRecharge() {
        if (Util.isNetworkAvailable()){

            //do validation
            String mobileNumber = mMobileNumberEditText.getText().toString().trim(),
                    amount = mAmountEditText.getText().toString().trim(),
                    rechargeType = mOperatorSpinner.getTag().toString();

            //check mobile number and amount are not empty
            if (mobileNumber.isEmpty() || amount.isEmpty()){
                Util.showRedSnackbar(mLayoutForSnackbar,"Fill in all the fields");
                return;
            }

            //check mobile number is of 10 char
            if (mobileNumber.length() != 10){
                Util.showRedSnackbar(mLayoutForSnackbar,"Invalid mobile number");
                return;
            }

            //check amount is more the 10
            if (Integer.parseInt(amount) < 10){
                Util.showRedSnackbar(mLayoutForSnackbar,"Recharge for less then 20 not allowed");
                return;
            }

            //check user has enough wallet amount
            if (Integer.parseInt(amount) > Float.parseFloat(mWalletAmount)){
                Util.showRedSnackbar(mLayoutForSnackbar,"Unable to do recharge of "+amount+" rs wallet balance is only of "+mWalletAmount+" rs");
                return;
            }

            //get the operator code
            String operatorCode = getOperatorCodeFromSpinner();

            //make a requestInternetAndGetHallOfFame to the api to do a recharge
            doRechargeInBackground(mobileNumber, amount, rechargeType, operatorCode);
        }else {
            Util.noInternetSnackBar(this,mLayoutForSnackbar);
        }
    }


    /*
    * Method to do recharge in background
    * */
    private void doRechargeInBackground(final String mobileNumber, final String amount, final String rechargeType, final String operatorCode) {
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.DO_RECHARGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseRechargeResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                error.printStackTrace();
                Log.e(TAG,"HUS: doRechargeInBackground: "+error.getMessage());
                //error message
                String errorMessage = MyVolley.handleVolleyError(error);
                if (errorMessage!=null){
                    Util.showRedSnackbar(mLayoutForSnackbar,errorMessage);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put(Constants.COM_APIKEY,Util.generateApiKey(mLoggedInUserMobile));
                param.put(Constants.COM_MOBILE,mLoggedInUserMobile);
                param.put(Constants.COM_MOBILE_TO_RECHARGE,mobileNumber);
                param.put(Constants.COM_AMOUNT,amount);
                param.put(Constants.COM_OPERATOR_CODE,operatorCode);
                param.put(Constants.COM_RECHARGE_TYPE,rechargeType);
                return param;
            }
        };

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        mRequestQueue.add(request);
    }

    /*
    * Method to parse recharge response
    * */
    private void parseRechargeResponse(String response) {
        try {
            SimplePojo current = JsonParsor.SimpleParser(response);
            if (current.isReturned()){
                //success

                //update wallet amount
                FetchWalletAmount fetchWalletAmount = new FetchWalletAmount(this);
                fetchWalletAmount.fetchNewWalletAmountInBackground();

                //show success amount
                showSuccessBox(current.getMessage());
            }else {
                //false
                //show error response
                showErrorBox(current.getMessage());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"HUS: parseRechargeResponse "+e.getMessage());
            Util.showParsingErrorAlert(this);
        }

    }

    /*
    * method to get the operator code from the selected spinner
    * */
    private String getOperatorCodeFromSpinner() {
        //get spinner pos
        int pos = mOperatorSpinner.getSelectedItemPosition();
        String code = null;
        if (mOperatorSpinner.getTag().equals("mobile")){
            code =  mMobileOperatorCodes[pos];
        }
        return code;
    }

    /*
    * show error alert box error
    * */
    public void showErrorBox(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.oops))
                .setMessage(message)
                .setPositiveButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
    * Show success error box
    * */
    public void showSuccessBox(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.hurray))
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
