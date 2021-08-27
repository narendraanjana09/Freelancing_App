package com.nsa.labelimages.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.google.android.material.tabs.TabLayout;
import com.nsa.labelimages.Adapters.AppliedAdaper;
import com.nsa.labelimages.Adapters.MyTasksFragmentsAdapter;
import com.nsa.labelimages.Adapters.VerifiedAdaper;
import com.nsa.labelimages.Interfaces.OnSyncUserData;
import com.nsa.labelimages.Model.ApplicationModel;
import com.nsa.labelimages.Model.UserModel;
import com.nsa.labelimages.R;

import static com.nsa.labelimages.activities.TaskActivity.appliedModelList;
import static com.nsa.labelimages.activities.TaskActivity.userModel;
import static com.nsa.labelimages.activities.TaskActivity.verifiedModelList;

public class MyTasksActivity extends AppCompatActivity {
    public static AppliedAdaper appliedAdaper;
    public static VerifiedAdaper verifiedAdaper;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    MyTasksFragmentsAdapter fragmentsAdapter;
    final int VERIFIED_ID=0;
    final int APPLIED_ID=1;
    RecyclerView appliedRV;
    AppliedAdaper adapter;
    TextView infoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tasks);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Verified").setIcon(MyTasksActivity.this.getDrawable(R.drawable.verify)).setId(VERIFIED_ID));
        tabLayout.addTab(tabLayout.newTab().setText("Applied").setId(APPLIED_ID));
        fragmentsAdapter=new MyTasksFragmentsAdapter(getSupportFragmentManager(),getLifecycle());
        viewPager.setAdapter(fragmentsAdapter);
        appliedRV=findViewById(R.id.appliedRV);
        infoView=findViewById(R.id.infoView);
        adapter=new AppliedAdaper(userModel.getApplicaModelList(),this);
        appliedRV.setAdapter(adapter);

        OnSyncUserData onSyncUserData =new OnSyncUserData() {
            @Override
            public void newUserData(UserModel userModel) {

                checkAppliedAndVerified();
            }

            @Override
            public void newRequestdata() {

            }
        };
        TaskActivity.onSyncUserDataList.add(onSyncUserData);

       // checkAppliedAndVerified();



        mixViewPagerAndTab();

    }

    private void mixViewPagerAndTab() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getId()==VERIFIED_ID){
                    tab.setIcon(MyTasksActivity.this.getDrawable(R.drawable.verify));
                }else{
                    tab.setIcon(MyTasksActivity.this.getDrawable(R.drawable.ic_baseline_assignment_24));
                }

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(null);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }


    private void checkAppliedAndVerified() {
        appliedModelList.clear();
        verifiedModelList.clear();
        if(userModel.getApplicaModelList()!=null){
            for (ApplicationModel applicationModel : userModel.getApplicaModelList()) {

                if (applicationModel.getTask_status().equals("0")) {
                    appliedModelList.add(0,applicationModel);
                } else if(applicationModel.getTask_status().equals("1")){
                    verifiedModelList.add(0,applicationModel);
                }

            }
            if (verifiedAdaper != null) {
                verifiedAdaper.notifyDataSetChanged();
            }
            if (appliedAdaper != null) {
                appliedAdaper.notifyDataSetChanged();
            }
        }
    }
    public void backToTask(View view) {
        finish();
    }
}