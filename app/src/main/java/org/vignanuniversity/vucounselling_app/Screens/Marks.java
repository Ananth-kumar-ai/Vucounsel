package org.vignanuniversity.vucounselling_app.Screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.vignanuniversity.vucounselling_app.Adapter.WeekAdapter;
import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.weekClass;

import java.util.ArrayList;

public class Marks extends Fragment {

    TextView textView;
    View root;
    ArrayList<weekClass> weekClassArrayList;
    RecyclerView recyclerView;
    Spinner spinner;
    String selected_text="";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.counselling_attendance, container, false);
        initFunctions();
        return root;
    }

    private void initFunctions() {
        recyclerView = root.findViewById(R.id.week_recycler_view);
        spinner = root.findViewById(R.id.week_select);

        weekClassArrayList = new ArrayList<weekClass>();
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        WeekAdapter weekAdapter = new WeekAdapter(weekClassArrayList);
        recyclerView.setAdapter(weekAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 1:
                        weekClassArrayList.clear();
                        weekClassArrayList.add(new weekClass("list 1"));
                        weekClassArrayList.add(new weekClass("list 1"));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                WeekAdapter weekAdapter = new WeekAdapter(weekClassArrayList);
                                recyclerView.setAdapter(weekAdapter);
                            }
                        });
                        return;
                    case 2:
                        weekClassArrayList.clear();
                        weekClassArrayList.add(new weekClass("list 2"));
                        weekClassArrayList.add(new weekClass("list 2"));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                WeekAdapter weekAdapter = new WeekAdapter(weekClassArrayList);
                                recyclerView.setAdapter(weekAdapter);
                            }
                        });
                        return;
                    case 3:
                        weekClassArrayList.clear();
                        weekClassArrayList.add(new weekClass("list 3"));
                        weekClassArrayList.add(new weekClass("list 3"));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                WeekAdapter weekAdapter = new WeekAdapter(weekClassArrayList);
                                recyclerView.setAdapter(weekAdapter);
                            }
                        });
                        return;
                    case 4:
                        weekClassArrayList.clear();
                        weekClassArrayList.add(new weekClass("list 4"));
                        weekClassArrayList.add(new weekClass("list 4"));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                WeekAdapter weekAdapter = new WeekAdapter(weekClassArrayList);
                                recyclerView.setAdapter(weekAdapter);
                            }
                        });
                        return;
                    case 5:
                        weekClassArrayList.clear();
                        weekClassArrayList.add(new weekClass("list 5"));
                        weekClassArrayList.add(new weekClass("list 5"));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                WeekAdapter weekAdapter = new WeekAdapter(weekClassArrayList);
                                recyclerView.setAdapter(weekAdapter);
                            }
                        });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

}
