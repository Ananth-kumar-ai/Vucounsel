package org.vignanuniversity.vucounselling_app.semester;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import org.vignanuniversity.vucounselling_app.Adapter.InternalMyViewPager;
import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.semData;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class sem_marks extends Fragment {
    TextView ps,bs,cr,s_gpa;
    static ArrayList<semData> studentData;
    int select = 0;
    SharedPreferences preferences;
    String cyear="",sem="",regno="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.semester_fragment, container, false);
        LinearLayout layout = root.findViewById(R.id.linearLayout);

        if (getArguments() != null) {
            regno = getArguments().getString("student_regno");
            cyear = getArguments().getString("cyear");
            sem = getArguments().getString("semester");
            Log.d("sem_marks", "onCreateView: " + regno + " " + cyear + " " + sem);
        }
        ps = root.findViewById(R.id.ps);
        bs = root.findViewById(R.id.bs);
        cr = root.findViewById(R.id.cr);
        s_gpa = root.findViewById(R.id.sgpa);

        ViewPager2 viewPager2 = root.findViewById(R.id.OpenViewPager2);
        InternalMyViewPager adapter = new InternalMyViewPager(getActivity());

        if(cyear.equals("1") && sem.equals("1")){
            adapter.addFragment(new I_I(regno),"I-I");
        }else if(cyear.equals("1") && sem.equals("2")){
            adapter.addFragment(new I_I(regno),"I-I");
            adapter.addFragment(new I_II(regno),"I-II");
        }else if(cyear.equals("2") && sem.equals("1")){
            adapter.addFragment(new I_I(regno),"I-I");
            adapter.addFragment(new I_II(regno),"I-II");
            adapter.addFragment(new II_I(regno),"II-I");
        }else if(cyear.equals("2") && sem.equals("2")){
            adapter.addFragment(new I_I(regno),"I-I");
            adapter.addFragment(new I_II(regno),"I-II");
            adapter.addFragment(new II_I(regno),"II-I");
            adapter.addFragment(new II_II(regno),"II-II");
        }else if(cyear.equals("3") && sem.equals("1")){
            adapter.addFragment(new I_I(regno),"I-I");
            adapter.addFragment(new I_II(regno),"I-II");
            adapter.addFragment(new II_I(regno),"II-I");
            adapter.addFragment(new II_II(regno),"II-II");
            adapter.addFragment(new III_I(regno),"III-I");
        }else if(cyear.equals("3") && sem.equals("2")){
            adapter.addFragment(new I_I(regno),"I-I");
            adapter.addFragment(new I_II(regno),"I-II");
            adapter.addFragment(new II_I(regno),"II-I");
            adapter.addFragment(new II_II(regno),"II-II");
            adapter.addFragment(new III_I(regno),"III-I");
            adapter.addFragment(new III_II(regno),"III-II");
        }else if(cyear.equals("4") && sem.equals("1")){
            adapter.addFragment(new I_I(regno),"I-I");
            adapter.addFragment(new I_II(regno),"I-II");
            adapter.addFragment(new II_I(regno),"II-I");
            adapter.addFragment(new II_II(regno),"II-II");
            adapter.addFragment(new III_I(regno),"III-I");
            adapter.addFragment(new III_II(regno),"III-II");
            adapter.addFragment(new IV_I(regno),"IV-I");
        }else if(cyear.equals("4") && sem.equals("2")){
            adapter.addFragment(new I_I(regno),"I-I");
            adapter.addFragment(new I_II(regno),"I-II");
            adapter.addFragment(new II_I(regno),"II-I");
            adapter.addFragment(new II_II(regno),"II-II");
            adapter.addFragment(new III_I(regno),"III-I");
            adapter.addFragment(new III_II(regno),"III-II");
            adapter.addFragment(new IV_I(regno),"IV-I");
            adapter.addFragment(new IV_II(regno),"IV-II");
        }

        viewPager2.setAdapter(adapter);


        TabLayout tabLayout = root.findViewById(R.id.OpenTabLayout2);
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    String Title = adapter.getPageTitle(position).toString();
                    tab.setText(Title);
                }
        ).attach();

        tabLayout.getTabAt(0).select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                select = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return root;
    }
}