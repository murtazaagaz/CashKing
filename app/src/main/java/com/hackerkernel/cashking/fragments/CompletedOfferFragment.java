package com.hackerkernel.cashking.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hackerkernel.cashking.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompletedOfferFragment extends Fragment {


    public CompletedOfferFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_offer, container, false);
    }

}
