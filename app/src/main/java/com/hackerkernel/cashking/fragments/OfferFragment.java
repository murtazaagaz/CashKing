package com.hackerkernel.cashking.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
 * A simple {@link Fragment} subclass.
 */
public class OfferFragment extends Fragment {
    private static final String TAG = OfferFragment.class.getSimpleName();
    private String mobile;
    private RequestQueue mRequestQue;
    @Bind(R.id.recycleView)
    RecyclerView recyclerView;
    @Bind(R.id.frameLayout) View frame;
    private List<DealsListPojo> list = new ArrayList<>();

    public OfferFragment() {
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
        checkIfNetworkAvailable();
        return view;
    }

    private void checkIfNetworkAvailable() {
        if (Util.isNetworkAvailable()) {
            getDataInBackground();
        }
    }

    private void getDataInBackground() {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.OFFERLIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean retuurned = obj.getBoolean(Constants.COM_RETURN);
                    String messag = obj.getString(Constants.COM_MESSAGE);
                    if (retuurned) {
                       JSONArray data = obj.getJSONArray(Constants.COM_DATA);
                       list = JsonParsor.parseDealsList(data);
                        DealsAdapter adapter = new DealsAdapter(getActivity());
                        adapter.setDealList(list);
                        recyclerView.setAdapter(adapter);
                    }
                    else {
                      Toast.makeText(getActivity(),messag,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG,"Mur"+e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errror = MyVolley.handleVolleyError(error);
                Util.showRedSnackbar(frame,errror);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(mobile));
                params.put(Constants.COM_MOBILE,mobile);
                params.put("type","new");
                return params;
            }
        };
        mRequestQue.add(request);
    }

}
