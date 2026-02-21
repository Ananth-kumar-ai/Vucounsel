package org.vignanuniversity.vucounselling_app.Screens;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.vignanuniversity.vucounselling_app.Adapter.OnScrollListener;
import org.vignanuniversity.vucounselling_app.Adapter.WeekAdapter_dynamic;
import org.vignanuniversity.vucounselling_app.DataFetcher.All_DataFetcher;
import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.Week;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class dynamicweeks extends Fragment {

    private View root;
    private RecyclerView weeksRecyclerView;
    private WeekAdapter_dynamic adapter;
    private List<Week> weekList;
    private OnScrollListener scrollListener;

    LinearLayout dynamic_weeks_layout;
    FrameLayout layout_fragment;
    String cyear="",sem="",regno="",CurrentWeekName="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.dynamicweeks, container, false);

        setUpCurrentWeek();

        weeksRecyclerView = root.findViewById(R.id.recyclerView);
        weeksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupRecyclerViewScrollListener();

        if (getArguments() != null) {
            regno = getArguments().getString("student_regno");
            cyear = getArguments().getString("cyear");
            sem = getArguments().getString("semester");
            Log.d("dynamicweeks", "onCreateView: " + regno + " " + cyear + " " + sem);
        }

        weekList = new ArrayList<>();
        adapter = new WeekAdapter_dynamic(getContext(), weekList);
        weeksRecyclerView.setAdapter(adapter);

        All_DataFetcher.dynamicWeeksFetcher(getContext(), regno, false, this::parseWeeksFromJson);

        return root;
    }

    private void setUpCurrentWeek() {

    }

    private void parseWeeksFromJson(JSONObject data) throws JSONException {

        weekList.clear();
        JSONArray dynamicWeeksArray = data.getJSONArray("DynamicWeeks");
        JSONArray CurrentWeek = data.getJSONArray("CurrentWeek");
        if (CurrentWeek.length() > 0) {
            JSONObject currentWeekObject = CurrentWeek.getJSONObject(0);
            CurrentWeekName = currentWeekObject.getString("CurrentWeek");
            TextView currentWeekTextView = root.findViewById(R.id.week_name);
            currentWeekTextView.setText(CurrentWeekName);
            MaterialButtonToggleGroup toggleGroup = root.findViewById(R.id.toggleButton);

            toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    String status = "";
                    if (checkedId == R.id.button1) {
                        status = "Present";
                    } else if (checkedId == R.id.button2) {
                        status = "Absent";
                    } else if (checkedId == R.id.button3) {
                        status = "Not Needed";
                    }

                    if (status.equals("Present")) {
                        scrollListener.onScrollDown();
                        layout_fragment = root.findViewById(R.id.dynamicContainer);
                        dynamic_weeks_layout = root.findViewById(R.id.dynamicWeeksLayout);
                        layout_fragment.setVisibility(View.VISIBLE);
                        dynamic_weeks_layout.setVisibility(View.GONE);
                        FeedBack feedbackFragment = new FeedBack();

                        Bundle bundle = new Bundle();
                        bundle.putString("student_regno", regno);
                        bundle.putString("cyear", cyear);
                        bundle.putString("semester", sem);
                        bundle.putString("current_week", CurrentWeekName);
                        feedbackFragment.setArguments(bundle);

                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.dynamicContainer, feedbackFragment)
                                .addToBackStack(null)
                                .commit();

                    }
                }
            });
        } else {
            CurrentWeekName = "No current week available";
        }


        if (dynamicWeeksArray.length() > 0) {
            JSONObject weeksObject = dynamicWeeksArray.getJSONObject(0);

            Iterator<String> keys = weeksObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String weekString = weeksObject.getString(key);
                if( weekString.equals(CurrentWeekName) ) {
                    continue;
                }

                int bracketStart = weekString.indexOf('[');
                int bracketEnd = weekString.indexOf(']');

                if (bracketStart != -1 && bracketEnd != -1) {
                    String weekName = weekString.substring(0, bracketStart).trim().replaceAll("Week(\\d)\\b", "Week0$1");
                    String dateRange = weekString.substring(bracketStart + 1, bracketEnd);
                    String[] dates = dateRange.split(" to ");
                    if (dates.length == 2) {
                        String startDate = dates[0].trim();
                        String endDate = dates[1].trim();
                        weekList.add(new Week(weekName, startDate, endDate));
                    }
                }
            }
        }

        if (weekList.isEmpty()) {
            weekList.add(new Week("No weeks available", "N/A", "N/A"));
        }

        weekList.sort((w1, w2) -> w2.getName().compareTo(w1.getName()));

        adapter.notifyDataSetChanged();
    }
    private void setupRecyclerViewScrollListener() {
        weeksRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (scrollListener != null) {
                    if (dy > 0) {
                        scrollListener.onScrollDown();
                    } else if (dy < 0) {
                        scrollListener.onScrollUp();
                    }
                }
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnScrollListener) {
            scrollListener = (OnScrollListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnScrollListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        scrollListener = null;
    }
}