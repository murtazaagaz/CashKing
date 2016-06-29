package com.hackerkernel.cashking.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.hackerkernel.cashking.R;
import com.hackerkernel.cashking.adapter.ViewPagerAdapter;
import com.hackerkernel.cashking.fragments.CompletedOfferFragment;
import com.hackerkernel.cashking.fragments.NewOfferFragment;
import com.hackerkernel.cashking.network.FetchWalletAmount;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.home_tabs) TabLayout tabLayout;
    @Bind(R.id.home_viewPager) ViewPager viewPager;
    @Bind(R.id.navigation_view) NavigationView mNaviagtionView;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    //member varaible
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.app_name);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        //fetch user wallet amount
        FetchWalletAmount fetchWalletAmount = new FetchWalletAmount(this);
        fetchWalletAmount.fetchNewWalletAmountInBackground();

        //init side menu
        initSideMenu();

    }

    public void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NewOfferFragment(),"New");
        adapter.addFragment(new CompletedOfferFragment(),"Completed");
        viewPager.setAdapter(adapter);
    }

    /*
     * Method to instanciate Side Drawer
     * */
    private void initSideMenu() {
        //instanciate ActionbarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar, R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        //When drawer menu items are clicked
        mNaviagtionView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_home:
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(getApplication(),HomeActivity.class));
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
