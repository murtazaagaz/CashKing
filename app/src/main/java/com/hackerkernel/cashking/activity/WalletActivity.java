package com.hackerkernel.cashking.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.infrastructure.BaseAuthActivity;
import com.hackerkernel.cashking.network.MyVolley;
import com.hackerkernel.cashking.storage.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;


public class WalletActivity extends BaseAuthActivity {

    private static final String TAG = WalletActivity.class.getSimpleName();
   // @Bind(R.id.toolbar_wallet) Button mToolbarWalletButton;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.layoutForSnackbar) View mLayoutForSnackbar;
    @Bind(R.id.radio_button_group) RadioGroup mRadioButtonGroup;
    @Bind(R.id.operator_spinner) Spinner mOperatorSpinner;
    @Bind(R.id.recharge_now) Button mRechargeNowButton;
    @Bind(R.id.mobileNumber) EditText mMobileNumberEditText;
    @Bind(R.id.amount) EditText mAmountEditText;

    private MySharedPreferences sp;
    private String mWalletAmount;
    private String mLoggedInUserMobile;

    //Arrays to store mobile operator & code from res/string.xml
    private String[] mMobileOperatorNames;
    private String[] mMobileOperatorCodes;

    private ArrayAdapter<String> adapter;
    private RequestQueue mRequestQueue;
    private ProgressDialog pd;
    private LayoutInflater inflate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.wallet);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = MySharedPreferences.getInstance(getApplicationContext());

        //init layout inflater
        inflate = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        //instanciate progressbar
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.processing));
        pd.setCancelable(true);

        //insalize volley
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        //get logged in user mobile
        mLoggedInUserMobile = sp.getUserMobile();

        //set wallet amount to toolbar
       // mWalletAmount = sp.getAmount();
        // mToolbarWalletButton.setText(mWalletAmount + getString(R.string.rupee_sign));

        //get wallet amount from API set and store it in SP
        /*FetchWalletAmount fetchWalletAmount = new FetchWalletAmount(this);
        fetchWalletAmount.checkInternetAndGetWalletAmount();

        //Set operator & code array
        mMobileOperatorNames = getResources().getStringArray(R.array.mobile_operator_name);
        mMobileOperatorCodes = getResources().getStringArray(R.array.mobile_operator_code);


        //set Spinner with mobile operatores as default

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,mMobileOperatorNames);
        mOperatorSpinner.setAdapter(adapter);
        mOperatorSpinner.setTag("mobile");

        //keep check when radio button are checked
        mRadioButtonGroup.setOnCheckedChangeListener(this);

        //when recharge now button is clicked
        mRechargeNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetAndDoRecharge();
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.mobile_radio_button:
                    adapter = new ArrayAdapter<>(getBaseContext(),android.R.layout.simple_dropdown_item_1line,mMobileOperatorNames);
                    mOperatorSpinner.setAdapter(adapter);
                    mOperatorSpinner.setTag("mobile");
                break;
        }
    }

    /*
    * Method to check internet and validate recharge values
    * this method is called when recharge now button is clicked
    * */
   /* private void checkInternetAndDoRecharge() {
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
            if (Integer.parseInt(amount) < 20){
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

            //make a request to the api to do a recharge
            doRechargeInBackground(mobileNumber, amount, rechargeType, operatorCode);
        }else {
            Util.noInternetSnackBar(this,mLayoutForSnackbar);
        }
    }


    /*
    * Method to do recharge in background
    * */
    /*private void doRechargeInBackground(final String mobileNumber, final String amount, final String rechargeType, final String operatorCode) {
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, ApiUrl.DO_RECHARGE, new Response.Listener<String>() {
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
                param.put(Keys.KEY_COM_KEY,Util.getEncryptedKey(mLoggedInUserMobile));
                param.put(Keys.KEY_COM_MOBILE,mLoggedInUserMobile);
                param.put(Keys.KEY_DO_RECHARGE_MOBILE_TO_RECHARGE,mobileNumber);
                param.put(Keys.KEY_DO_RECHARGE_AMOUNT,amount);
                param.put(Keys.KEY_DO_RECHARGE_OPERATOR_CODE,operatorCode);
                param.put(Keys.KEY_DO_RECHARGE_TYPE,rechargeType);
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
    /*private void parseRechargeResponse(String response) {
        try {
            SimplePojo current = JsonParsor.simpleParser(response);
            if (current.isReturned()){
                //success

                //update wallet amount
                FetchWalletAmount fetchWalletAmount = new FetchWalletAmount(this);
                fetchWalletAmount.checkInternetAndGetWalletAmount();

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
    /*private String getOperatorCodeFromSpinner() {
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
   /* public void showErrorBox(String message){
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
  /*  public void showSuccessBox(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.hurray))
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        //show Rate dialog if user has never rated the app before
                        if (!sp.getBooleanKey(MySharedPreferences.KEY_RATED_APP)){
                            showRateYourExperienceDialog();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
    * Method to show rate your experience with yoo dialog
    * */
    /*public void showRateYourExperienceDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = inflate.inflate(R.layout.layout_rate_experience,null,false);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingBar.setRating(5);
        ratingBar.setStepSize(1);
        builder.setMessage(getString(R.string.rate_your_experience))
                .setView(view)
                .setPositiveButton(getString(R.string.rate), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        float rating = ratingBar.getRating();
                        /*
                        * if star is smaller the 3
                        *   show how can we improve our service dialog
                        * else
                        *   open google play
                        * */
                       /* if (rating <= 3.0){
                            //show how to improve service dialog
                            showImproveDialog();
                      /*  }else{
                            //store to sp that you rated this app
                            sp.setBooleanKey(MySharedPreferences.KEY_RATED_APP);

                            //open google play to rate our application
                            openGooglePlayToRate();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.remind_me_later), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
    * Open google play to rate appliction
    * */
   /* private void openGooglePlayToRate() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.unable_to_open_google_play, Toast.LENGTH_LONG).show();
        }
    }

    /*
    * Show improve dialog
    * */
   /* private void showImproveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //inflate layout of improve activity
        View view = inflate.inflate(R.layout.layout_improve,null,false);
        //get edit text or name & suggestion
        final EditText nameView = (EditText) view.findViewById(R.id.improveName);
        final EditText suggestionView = (EditText) view.findViewById(R.id.improveSuggesting);

        builder.setMessage(getString(R.string.how_can_we_improve_our_service))
                .setView(view)
                .setPositiveButton(getString(R.string.send), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameView.getText().toString().trim();
                        String suggestion = suggestionView.getText().toString().trim();
                        sendImproveSuggestionToTeam(name,suggestion);
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    /*
    * Method to send improve suggestion to Yoo Team
    * */
   /* private void sendImproveSuggestionToTeam(String name, String suggestion) {
        if (name.isEmpty() || suggestion.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.fill_in_all_the_fields,Toast.LENGTH_LONG).show();
            return;
        }
        String text = suggestion+"\n"+sp.getUserMobile()+"\n"+name;

        String[] TO = {"contact@theyoo.co"};

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Yoo Improve Suggestion - "+sp.getUserMobile());
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            startActivity(Intent.createChooser(emailIntent, "Contact Yoo app"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "HUS: sendImproveSuggestionToTeam: unable to open activity: "+e.getMessage());
            Toast.makeText(getApplicationContext(), R.string.unable_to_email_app,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wallet_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_recharge_history:
                startActivity(new Intent(this,RechargeHistoryActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);

    **/
    }
}

