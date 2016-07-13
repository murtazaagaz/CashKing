package com.hackerkernel.cashking.network;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.adapter.DealsListAdapter;
import com.hackerkernel.cashking.constants.Constants;
import com.hackerkernel.cashking.constants.EndPoints;
import com.hackerkernel.cashking.parser.JsonParsor;
import com.hackerkernel.cashking.pojo.DealsListPojo;
import com.hackerkernel.cashking.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to fetch Offer/Pending/completed list from api fetch it and display it in a recyclerview
 */
public class FetchOfferList {
    private static final String TAG = FetchOfferList.class.getSimpleName();
    //Member fields
    private RequestQueue mRequestQueue;
    private Context mContext;
    private View mLayoutForSnackbar;
    private String mMobile;
    private RecyclerView mRecyclerView;
    private TextView mPlaceholder;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //list to store offer list data
    private List<DealsListPojo> mList;


    //constructor
    public FetchOfferList(Context context, View layoutForSnackbar, String mobile,
                          RecyclerView recyclerView, TextView placeholder,
                          SwipeRefreshLayout swipeRefreshLayout){
        //instanciate Volley
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        //store refernce
        this.mContext = context;
        this.mLayoutForSnackbar = layoutForSnackbar;
        this.mMobile = mobile;
        this.mRecyclerView = recyclerView;
        this.mPlaceholder = placeholder;
        this.mSwipeRefreshLayout = swipeRefreshLayout;
    }

    /*
    * Method to check internet & fetch latest offer list
    * */
    public void FetchNewOfferList(){
        if (Util.isNetworkAvailable()){
            fetchOfferListInBackground(Constants.OFFER_TYPE_NEW);
        }else {
            Util.noInternetSnackBar(mContext,mLayoutForSnackbar);
        }
    }


    /*
    * Method to check internet & fetch latest offer list
    * */
    public void FetchCompletedOfferList(){
        if (Util.isNetworkAvailable()){
            fetchOfferListInBackground(Constants.OFFER_TYPE_COMPLETED);
        }else {
            Util.noInternetSnackBar(mContext,mLayoutForSnackbar);
        }
    }


    /*
    * Method to fetch latest offer list from API
    * */
    private void fetchOfferListInBackground(final String offerType){
        //show pb
        if (mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setRefreshing(true);
        }

        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.OFFERLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //hide pb
                if (mSwipeRefreshLayout != null){
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                //parse pending / latest / complete offer list
                parseOfferListResponse(response,offerType);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //hide pb
                //Util.setProgressbarVisiblilty(pb,false);
                if (mSwipeRefreshLayout != null){
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                error.printStackTrace();
                Log.d(TAG, "HUS: fetchOfferListInBackground: " + error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                Util.showRedSnackbar(mLayoutForSnackbar,errorString);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(mMobile));
                params.put(Constants.COM_MOBILE,mMobile);

                /*
                * send post param on basic of offerType
                *
                * */
                if (offerType.equals(Constants.OFFER_TYPE_NEW)){
                    params.put(Constants.COM_TYPE,Constants.OFFER_TYPE_NEW);
                }
                else if(offerType.equals(Constants.OFFER_TYPE_PENDING)){
                    params.put(Constants.COM_TYPE,Constants.OFFER_TYPE_PENDING);
                }
                else if (offerType.equals(Constants.OFFER_TYPE_COMPLETED)){
                    params.put(Constants.COM_TYPE,Constants.OFFER_TYPE_COMPLETED);
                }

                return params;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * Method to parse OfferListResponse
    * */
    private void parseOfferListResponse(String response,String offerType) {
        try {
            JSONObject obj = new JSONObject(response);
            boolean returned = obj.getBoolean(Constants.COM_RETURN);
            String message = obj.getString(Constants.COM_MESSAGE);
            if (returned) {
                //get count
                int count = obj.getInt(Constants.COM_COUNT);

                //offer found fetch data & setup Recyclerview
                if (count > 0){
                    JSONArray data = obj.getJSONArray(Constants.COM_DATA);
                    this.mList = JsonParsor.parseDealsList(data,offerType);
                    setupOfferRecyclerViewFromList(offerType);
                }
                //no offer found show error
                else {
                    mRecyclerView.setVisibility(View.GONE);
                    mPlaceholder.setVisibility(View.VISIBLE);


                    //Display appropriate message
                    switch (offerType) {
                        case Constants.OFFER_TYPE_COMPLETED:
                            //completed offer List
                            mPlaceholder.setText(R.string.no_completed_offers);
                            break;
                        default:
                            //latest offer List
                            mPlaceholder.setText(R.string.no_new_offer_found_try_again_later);
                            break;
                    }
                }

            } else {
                Util.showRedSnackbar(mLayoutForSnackbar,message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"MUR: fetchOfferInBackground"+e);
            Util.showParsingErrorAlert(mContext);
        }
    }

    /*
    * Method to setup recycler view from list
    * */
    private void setupOfferRecyclerViewFromList(String offerType) {
        DealsListAdapter adapter = new DealsListAdapter(mContext,offerType);
        adapter.setDealList(mList);
        mRecyclerView.setAdapter(adapter);
    }
}
