package com.hackerkernel.cashking.infrastructure;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.hackerkernel.cashking.network.FetchWalletAmount;
import com.hackerkernel.cashking.activity.LoginActivity;
import com.hackerkernel.cashking.storage.MySharedPreferences;

/**
 * Class to check login status
 * if user is not logged in send him to login activity
 */
public abstract class BaseAuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if user not login send him to Main activity
        if (!MySharedPreferences.getInstance(this).getLoginStatus()){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
