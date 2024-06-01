package com.example.starautosubmission;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SettingsPagerAdapter extends FragmentStateAdapter {

    public SettingsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PersonalInfoFragment();
            case 1:
                return new EmailSettingsFragment();
            case 2:
                return new EmailBodyFragment();
            case 3:
                return new ChangePasswordFragment();
            default:
                return new PersonalInfoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // 总共4个Fragment
    }
}
