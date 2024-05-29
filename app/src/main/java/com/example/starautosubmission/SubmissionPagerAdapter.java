package com.example.starautosubmission;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SubmissionPagerAdapter extends FragmentStateAdapter {

    public SubmissionPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new StepOneFragment();
            case 1:
                return new StepTwoFragment();
            case 2:
                return new StepThreeFragment();
            default:
                return new StepOneFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
