package com.nsa.labelimages.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.nsa.labelimages.Fragments.AppliedFragment;
import com.nsa.labelimages.Fragments.ExploreTaskFragment;
import com.nsa.labelimages.Fragments.MyTasksFragment;
import com.nsa.labelimages.Fragments.VerifiedFragment;

import org.jetbrains.annotations.NotNull;

public class HomeFragmentsAdapter extends FragmentStateAdapter {


    public HomeFragmentsAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        if (position == 0)
            fragment = new ExploreTaskFragment();
        else if (position == 1)
            fragment = new MyTasksFragment();


        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}