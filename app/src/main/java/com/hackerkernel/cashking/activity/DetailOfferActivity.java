package com.hackerkernel.cashking.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.constants.Constants;
import com.hackerkernel.cashking.constants.EndPoints;
import com.hackerkernel.cashking.network.MyVolley;
import com.hackerkernel.cashking.parser.JsonParsor;
import com.hackerkernel.cashking.pojo.DetailOfferPojo;
import com.hackerkernel.cashking.pojo.OfferInstallementPojo;
import com.hackerkernel.cashking.pojo.SimplePojo;
import com.hackerkernel.cashking.storage.MySharedPreferences;
import com.hackerkernel.cashking.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailOfferActivity extends AppCompatActivity {
    private RequestQueue mRequestQue;
    private String mOfferId;
    private MySharedPreferences sp;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.layout_for_snackbar) View mLayoutForSnackbar;
    @Bind(R.id.offer_image) ImageView mImage;
    @Bind(R.id.offer_name) TextView mName;
    @Bind(R.id.offer_short_description) TextView mShortDescription;
    @Bind(R.id.offer_amount) TextView mAmount;
    @Bind(R.id.offer_detail_description) TextView mDetailDescription;
    @Bind(R.id.offer_note) TextView mNote;
    @Bind(R.id.offer_installment_container) TableLayout mInstallmentContainer;
    @Bind(R.id.offer_detail_instruction) TextView mDetailInstruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_offer);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Offer detail");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init sp
        sp = MySharedPreferences.getInstance(this);

        //init volley
        mRequestQue = MyVolley.getInstance().getRequestQueue();

        //check offers id
        if (getIntent().hasExtra(Constants.COM_ID)){

            mOfferId = getIntent().getExtras().getString(Constants.COM_ID);

            //check internet and get offer details
            checkInternetAndFetchDetails();
        } else{
            Toast.makeText(getApplication(),"Unable to open offer detail page. Something weird is going on. Try again later",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void checkInternetAndFetchDetails() {
        if (Util.isNetworkAvailable()){
            fetchDataInBackground();
        }else {
            Util.noInternetSnackBar(this,mLayoutForSnackbar);
        }
    }

    private void fetchDataInBackground() {
        StringRequest req = new StringRequest(Request.Method.POST, EndPoints.OFFER_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    SimplePojo pojo = JsonParsor.simpleParser(response);
                    if (pojo.isReturned()){
                        JSONObject obj = new JSONObject(response);
                        JSONArray data = obj.getJSONArray(Constants.COM_DATA);
                        //parse offer detail
                        DetailOfferPojo detailOfferPojo = JsonParsor.parseDetailOffer(data);
                        setupViews(detailOfferPojo);
                    }else{
                        //TODO:: handle when response is false
                        Toast.makeText(getApplicationContext(),pojo.getMessage(),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Util.showParsingErrorAlert(DetailOfferActivity.this);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String handleError = MyVolley.handleVolleyError(error);
                if (handleError != null){
                    Util.showRedSnackbar(mLayoutForSnackbar,handleError);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new Hashtable<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE,sp.getUserMobile());
                params.put(Constants.COM_ID,mOfferId);
                return params;
            }
        };
        mRequestQue.add(req);
    }

    private void setupViews(DetailOfferPojo d) {
        mName.setText(d.getName());
        mAmount.setText(d.getAmount());
        mShortDescription.setText(d.getShortDescription());
        mDetailDescription.setText(d.getDetailDescription());
        mDetailInstruction.setText(d.getDetailInstruction().replace("<br>","\n"));

        Log.d("HUS","HUS: "+d.getName());
        //set offer name to title bar
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(d.getName());
        }

        //setup Image
        if (d.getImageUrl() != null){
            String imageUrl = EndPoints.IMAGE_BASE_URL + d.getImageUrl();
            Glide.with(this)
                    .load(imageUrl)
                    .thumbnail(0.5f)
                    .into(mImage);
        }

        //parse Offer installement amount
        List<OfferInstallementPojo> list = d.getInstallmentList();
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < list.size(); i++) {
            OfferInstallementPojo c = list.get(i);
            View view = inflater.inflate(R.layout.include_offer_installment_table_layout,mInstallmentContainer,false);
            TextView descTextview = (TextView) view.findViewById(R.id.description);
            TextView amountTextview = (TextView) view.findViewById(R.id.amount);
            descTextview.setText(c.getDescription());
            amountTextview.setText(c.getAmount());
            mInstallmentContainer.addView(view);
        }

    }


}

