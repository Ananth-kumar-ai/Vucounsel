package org.vignanuniversity.vucounselling_app.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;
public class StudentMyViewPager extends FragmentStateAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitles = new ArrayList<>();
    private String studentRegno = "";

    public StudentMyViewPager(@NonNull FragmentActivity fragmentActivity, String studentRegno) {
        super(fragmentActivity);
        this.studentRegno = studentRegno;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = fragmentList.get(position);

        Bundle args = new Bundle();
        args.putString("student_regno", studentRegno);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    @Nullable
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitles.add(title);
    }
}
