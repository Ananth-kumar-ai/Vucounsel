package org.vignanuniversity.vucounselling_app.Screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.vignanuniversity.vucounselling_app.Adapter.OnScrollListener;
import org.vignanuniversity.vucounselling_app.Adapter.WeekAdapter_dynamic;
import org.vignanuniversity.vucounselling_app.DataFetcher.All_DataFetcher;
import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.Week;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class dynamicweeks extends Fragment {

    private View root;
    private RecyclerView weeksRecyclerView;
    private WeekAdapter_dynamic adapter;
    private List<Week> weekList;
    private OnScrollListener scrollListener;

    LinearLayout dynamic_weeks_layout;
    FrameLayout layout_fragment;
    String cyear="", sem="", regno="", CurrentWeekName="";
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.dynamicweeks, container, false);
        sharedPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);

        setUpCurrentWeek();

        weeksRecyclerView = root.findViewById(R.id.recyclerView);
        weeksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupRecyclerViewScrollListener();

        if (getArguments() != null) {
            regno = getArguments().getString("student_regno");
            cyear = getArguments().getString("cyear");
            sem = getArguments().getString("semester");
        }

        weekList = new ArrayList<>();
        adapter = new WeekAdapter_dynamic(getContext(), weekList);
        weeksRecyclerView.setAdapter(adapter);

        // Fetch weekly data from server
        All_DataFetcher.dynamicWeeksFetcher(getContext(), regno, false, this::parseWeeksFromJson);

        return root;
    }

    private void setUpCurrentWeek() { }

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
            toggleGroup.setSingleSelection(true);
            toggleGroup.setSelectionRequired(true);

            MaterialButton btnPresent = root.findViewById(R.id.button1);
            MaterialButton btnAbsent = root.findViewById(R.id.button2);
            MaterialButton btnNotNeeded = root.findViewById(R.id.button3);

            String usercode = sharedPreferences.getString("regno", "");

            // 1. Manually hitting the "Present" button ALWAYS opens Feedback
            btnPresent.setOnClickListener(v -> {
                openFeedbackFragment();
            });

            // 2. Add listener to handle state changes
            toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    if (checkedId == R.id.button1) {
                        setButtonColors(btnPresent, btnAbsent, btnNotNeeded, "Present");
                        disableButtons(btnPresent, btnAbsent, btnNotNeeded, "Present");
                        // We do not save to DB here, the Feedback Form handles DB insert when submitted!
                    } else if (checkedId == R.id.button2) {
                        setButtonColors(btnPresent, btnAbsent, btnNotNeeded, "Absent");
                        disableButtons(btnPresent, btnAbsent, btnNotNeeded, "Absent");
                        markAttendanceStatus(regno, usercode, CurrentWeekName, "Absent");
                    } else if (checkedId == R.id.button3) {
                        setButtonColors(btnPresent, btnAbsent, btnNotNeeded, "Not Needed");
                        disableButtons(btnPresent, btnAbsent, btnNotNeeded, "Not Needed");
                        markAttendanceStatus(regno, usercode, CurrentWeekName, "Not Needed");
                    }
                }
            });

            // 3. Check what was already marked on load!
            checkAttendanceStatusOnLoad(regno, usercode, CurrentWeekName, toggleGroup, btnPresent, btnAbsent, btnNotNeeded);

        } else {
            CurrentWeekName = "No current week available";
        }

        // Add remaining weeks into the Recycler View List
        if (dynamicWeeksArray.length() > 0) {
            JSONObject weeksObject = dynamicWeeksArray.getJSONObject(0);

            Iterator<String> keys = weeksObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String weekString = weeksObject.getString(key);
                if(weekString.equals(CurrentWeekName)) {
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

    // Opens your feedback form securely
    private void openFeedbackFragment() {
        if(scrollListener != null) scrollListener.onScrollDown();

        layout_fragment = root.findViewById(R.id.dynamicContainer);
        dynamic_weeks_layout = root.findViewById(R.id.dynamicWeeksLayout);

        if (layout_fragment != null && dynamic_weeks_layout != null) {
            layout_fragment.setVisibility(View.VISIBLE);
            dynamic_weeks_layout.setVisibility(View.GONE);
        }

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

    // Custom Highlighting colors!
    private void setButtonColors(MaterialButton p, MaterialButton a, MaterialButton n, String status) {
        if(status.equals("Present")) {
            // Bright Green
            p.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")));
            p.setTextColor(android.graphics.Color.WHITE);
        } else if(status.equals("Absent")) {
            // Bright Red
            a.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F44336")));
            a.setTextColor(android.graphics.Color.WHITE);
        } else if(status.equals("Not Needed")) {
            // Bright Orange
            n.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF9800")));
            n.setTextColor(android.graphics.Color.WHITE);
        }
    }

    // Logic locking that completely freezes the app's UI state based on conditions!
    private void disableButtons(MaterialButton p, MaterialButton a, MaterialButton n, String status) {
        // We ALWAYS kill the ability to choose Absent/Not Needed once ANYTHING is clicked or loaded!
        a.setEnabled(false);
        n.setEnabled(false);

        if (status.equals("Present")) {
            // But we LEAVE Present ALIVE so they can click it and continue to the FeedBack form!
            p.setEnabled(true);
        } else {
            // If they clicked Absent/Not Needed, disable 'Present' forever too!
            p.setEnabled(false);
        }
    }

    // Hits our new check_attendance.jsp API to recall state silently
    private void checkAttendanceStatusOnLoad(String studentRegNo, String empcode, String weekname,
                                             MaterialButtonToggleGroup toggleGroup, MaterialButton p, MaterialButton a, MaterialButton n) {
        String url = "http://192.168.10.25/jspapi/test/check_attendance.jsp";

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        String dbStatus = json.getString("status");

                        if (!dbStatus.equals("Not Marked") && !dbStatus.startsWith("Error")) {
                            // If Database remembered a choice, we stop the Checkboxes from firing the network again!
                            toggleGroup.clearOnButtonCheckedListeners();

                            if (dbStatus.equals("Present")) {
                                toggleGroup.check(R.id.button1);
                            } else if (dbStatus.equals("Absent")) {
                                toggleGroup.check(R.id.button2);
                            } else if (dbStatus.equals("Not Needed")) {
                                toggleGroup.check(R.id.button3);
                            }

                            // Physically color code and freeze them!
                            setButtonColors(p, a, n, dbStatus);
                            disableButtons(p, a, n, dbStatus);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("registerno", studentRegNo);
                param.put("empcode", empcode);
                param.put("weekname", weekname);
                return param;
            }
        };
        requestQueue.add(request);
    }

    private void markAttendanceStatus(String studentRegNo, String empcode, String weekname, String status) {
        String url = "http://192.168.10.25/jspapi/test/mark_attendance.jsp";

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(getContext(), "Marked as " + status, Toast.LENGTH_SHORT).show(),
                error -> {
                    String errorMsg = "Failed to save: " + status;
                    if (error.networkResponse != null) {
                        errorMsg += " - HTTP Code: " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<>();
                param.put("registerno", studentRegNo);
                param.put("empcode", empcode);
                param.put("weekname", weekname);
                param.put("status", status);
                return param;
            }
        };

        requestQueue.add(request);
    }

    private void setupRecyclerViewScrollListener() {
        weeksRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (scrollListener != null) {
                    if (dy > 0) scrollListener.onScrollDown();
                    else if (dy < 0) scrollListener.onScrollUp();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
