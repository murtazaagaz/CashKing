package com.hackerkernel.cashking.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.adapter.ViewPagerAdapter;
import com.hackerkernel.cashking.fragments.CompletedOfferFragment;
import com.hackerkernel.cashking.fragments.NewOfferFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.home_tabs) TabLayout tabLayout;
    @Bind(R.id.home_viewPager) ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.cash_king);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }
    public void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NewOfferFragment(),"New");
        adapter.addFragment(new CompletedOfferFragment(),"Completed");
        viewPager.setAdapter(adapter);
    }


}
