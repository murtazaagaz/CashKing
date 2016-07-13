package com.hackerkernel.cashking.fragments;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.constants.Constants;
import com.hackerkernel.cashking.network.FetchOfferList;
import com.hackerkernel.cashking.service.PendingOffersVerificationService;
import com.hackerkernel.cashking.storage.MySharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Fragment to show new offers
 */
public class NewOfferFragment extends Fragment {
    private String mobile;

    @Bind(R.id.recycleView) RecyclerView recyclerView;
    @Bind(R.id.frameLayout) View layoutForSnackbar;
    @Bind(R.id.placeholder) TextView mPlaceHolder;
    @Bind(R.id.swipe_to_refresh) SwipeRefreshLayout mSwipeToRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer, container, false);
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
        fetchOfferList.FetchNewOfferList();

        //Start pending offer verification service
        startPendingOfferVerificationService();
    }

    //Method to start service to check pending offer is success or not
    public void startPendingOfferVerificationService(){
        //start service to verify pending intent
        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), PendingOffersVerificationService.class);
        intent.putExtra(Constants.COM_MOBILE,mobile);
        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,60000*5,AlarmManager.INTERVAL_HOUR,pendingIntent);
    }

}
