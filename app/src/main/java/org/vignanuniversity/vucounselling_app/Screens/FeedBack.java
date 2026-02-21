package org.vignanuniversity.vucounselling_app.Screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import org.vignanuniversity.vucounselling_app.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FeedBack extends Fragment {
    private View root;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private String regno = "", cyear = "", sem = "", currentWeekName = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.feedback_fragment, container, false);
        tabLayout = root.findViewById(R.id.tabLayout);
        viewPager = root.findViewById(R.id.viewPager);

        if (getArguments() != null) {
            regno = getArguments().getString("student_regno", "");
            cyear = getArguments().getString("cyear", "");
            sem = getArguments().getString("semester", "");
            currentWeekName = getArguments().getString("current_week", "");
        }
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewPagerAdapter adapter = new ViewPagerAdapter(requireActivity(), regno, cyear, sem, currentWeekName);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Counselling");
                            tab.setIcon(R.drawable.counselling);
                            break;
                        case 1:
                            tab.setText("Attitude");
                            tab.setIcon(R.drawable.attitude);
                            break;
                        case 2:
                            tab.setText("Traits");
                            tab.setIcon(R.drawable.traits);
                            break;
                    }
                }).attach();
        }
    private class ViewPagerAdapter extends FragmentStateAdapter {

        private final String regno, cyear, sem, currentWeekName;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String regno, String cyear, String sem,String currentWeekName) {
            super(fragmentActivity);
            this.regno = regno;
            this.cyear = cyear;
            this.sem = sem;
            this.currentWeekName = currentWeekName;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;

            switch (position) {
                case 1:
                    fragment = new Attitude();
                    break;
                case 2:
                    fragment = new Traits();
                    break;
                default:
                    fragment = new Counselling();
                    break;
            }

            Bundle bundle = new Bundle();
            bundle.putString("student_regno", regno);
            bundle.putString("cyear", cyear);
            bundle.putString("semester", sem);
            bundle.putString("current_week", currentWeekName);
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

}