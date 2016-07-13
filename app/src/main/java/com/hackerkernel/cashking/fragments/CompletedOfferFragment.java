package com.hackerkernel.cashking.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.network.FetchOfferList;
import com.hackerkernel.cashking.network.MyVolley;
import com.hackerkernel.cashking.storage.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompletedOfferFragment extends Fragment {

    private String mobile;

    @Bind(R.id.recycleView) RecyclerView recyclerView;
    @Bind(R.id.frameLayout) View layoutForSnackbar;
    @Bind(R.id.placeholder) TextView mPlaceHolder;
    @Bind(R.id.swipe_to_refresh) SwipeRefreshLayout mSwipeToRefresh;


    public CompletedOfferFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_completed_offer, container, false);

        ButterKnife.bind(this,view);

        mobile = MySharedPreferences.getInstance(getActivity()).getUserMobile();

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Construct FetchOfferList class & fetch pending offers
        FetchOfferList fetchOfferList = new FetchOfferList(getActivity(),layoutForSnackbar,mobile,recyclerView,mPlaceHolder,mSwipeToRefresh);
        fetchOfferList.FetchCompletedOfferList();
    }
}
