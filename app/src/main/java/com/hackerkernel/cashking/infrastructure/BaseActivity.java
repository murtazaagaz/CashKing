package com.hackerkernel.cashking.infrastructure;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.hackerkernel.cashking.storage.MySharedPreferences;
import com.hackerkernel.cashking.util.Util;

/**
 * Class to check if user logged in send him to home activity
 * no need to display login or MainActivity
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MySharedPreferences.getInstance(this).getLoginStatus()){
            Util.goToHomeActivity(this);
        }
    }
}
