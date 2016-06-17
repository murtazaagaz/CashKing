package com.hackerkernel.cashking.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.adapter.DealsAdapter;
import com.hackerkernel.cashking.constants.Constants;
import com.hackerkernel.cashking.constants.EndPoints;
import com.hackerkernel.cashking.network.MyVolley;
import com.hackerkernel.cashking.parser.JsonParsor;
import com.hackerkernel.cashking.pojo.DealsListPojo;
import com.hackerkernel.cashking.storage.MySharedPreferences;
import com.hackerkernel.cashking.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment to show new offers
 */
public class NewOfferFragment extends Fragment {
    private static final String TAG = NewOfferFragment.class.getSimpleName();
    private String mobile;
    private RequestQueue mRequestQue;

    @Bind(R.id.recycleView) RecyclerView recyclerView;
    @Bind(R.id.frameLayout) View layoutForSnackbar;

    private List<DealsListPojo> list = new ArrayList<>();

    public NewOfferFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer, container, false);
        ButterKnife.bind(this,view);

        mobile = MySharedPreferences.getInstance(getActivity()).getUserMobile();
        mRequestQue = MyVolley.getInstance().getRequestQueue();

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkInternetFetchOffer();
    }

    private void checkInternetFetchOffer() {
        if (Util.isNetworkAvailable()) {
            fetchOfferInBackground();
        }else {
            Util.noInternetSnackBar(getActivity(),layoutForSnackbar);
        }
    }

    private void fetchOfferInBackground() {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.OFFERLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean returned = obj.getBoolean(Constants.COM_RETURN);
                    String message = obj.getString(Constants.COM_MESSAGE);
                    if (returned) {
                        JSONArray data = obj.getJSONArray(Constants.COM_DATA);
                        parseOfferResponse(data);
                    } else {
                        Util.showRedSnackbar(layoutForSnackbar,message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG,"MUR: fetchOfferInBackground"+e);
                    Util.showParsingErrorAlert(getActivity());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e(TAG,"MUR: fetchOfferInBackground: "+error.getMessage());
                String errror = MyVolley.handleVolleyError(error);
                if (error != null){
                    Util.showRedSnackbar(layoutForSnackbar,errror);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(mobile));
                params.put(Constants.COM_MOBILE,mobile);
                params.put(Constants.COM_TYPE,"new");
                return params;
            }
        };
        mRequestQue.add(request);
    }

    private void parseOfferResponse(JSONArray data) throws JSONException {
        list = JsonParsor.parseDealsList(data);
        DealsAdapter adapter = new DealsAdapter(getActivity());
        adapter.setDealList(list);
        recyclerView.setAdapter(adapter);
    }

}
