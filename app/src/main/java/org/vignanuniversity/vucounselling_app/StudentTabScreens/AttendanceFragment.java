package org.vignanuniversity.vucounselling_app.StudentTabScreens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.vignanuniversity.vucounselling_app.Adapter.CourseAdapter;
import org.vignanuniversity.vucounselling_app.Adapter.OnScrollListener;
import org.vignanuniversity.vucounselling_app.DataFetcher.All_DataFetcher;
import org.vignanuniversity.vucounselling_app.MainScreens.AttendanceMain;
import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.Screens.StudentReport;
import org.vignanuniversity.vucounselling_app.classFiles.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendanceFragment extends Fragment {

    private static final String TAG = "ATT_FRAGMENT";

    View root;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private ArrayList<Course> arrayList;
    SharedPreferences preferences;
    RequestQueue requestQueue;

    String regno   = "";
    String empcode = "";

    TextView total, present, absent, percentage;
    private OnScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_attendance, container, false);

        preferences = root.getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

        // 1. Read from bundle
        Bundle args = getArguments();
        if (args != null) {
            regno   = args.getString("student_regno", "");
            empcode = args.getString("empcode", "");
        }

        // 2. Fall back to AttendanceMain static field
        if (empcode == null || empcode.isEmpty()) {
            empcode = AttendanceMain.empcode;
            Log.d(TAG, "empcode from AttendanceMain static: [" + empcode + "]");
        }

        // 3. Fall back to SharedPreferences
        if (empcode == null || empcode.isEmpty()) {
            empcode = preferences.getString("empcode", "");
            Log.d(TAG, "empcode from SharedPrefs: [" + empcode + "]");
        }

        Log.d(TAG, "onCreateView: regno=[" + regno + "] empcode=[" + empcode + "]");

        initializeViews();
        setupRecyclerViewScrollListener();
        setupStudentReportCard();

        requestQueue = Volley.newRequestQueue(getContext());
        All_DataFetcher.attendanceDataFetcher(getContext(), regno, false,
                this::parseAttendanceData);
        All_DataFetcher.attendanceDataFetcher(getContext(), regno, false,
                this::parseCourseData);

        return root;
    }

    private void setupStudentReportCard() {
        CardView studentReportCard = root.findViewById(R.id.student_report_card);
        studentReportCard.setOnClickListener(v -> {

            // Re-resolve empcode at click time — by now personalDetails API has finished
            String resolvedEmpcode = empcode;

            if (resolvedEmpcode == null || resolvedEmpcode.isEmpty()) {
                resolvedEmpcode = AttendanceMain.empcode;
            }
            if (resolvedEmpcode == null || resolvedEmpcode.isEmpty()) {
                resolvedEmpcode = preferences.getString("empcode", "");
            }

            Log.d(TAG, "Opening StudentReport: regno=[" + regno
                    + "] empcode=[" + resolvedEmpcode + "]");

            Intent intent = new Intent(getActivity(), StudentReport.class);
            intent.putExtra("student_regno", regno);
            intent.putExtra("empcode", resolvedEmpcode);
            startActivity(intent);
        });
    }

    private void setupRecyclerViewScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            throw new RuntimeException(context + " must implement OnScrollListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        scrollListener = null;
    }

    private void initializeViews() {
        total        = root.findViewById(R.id.total);
        present      = root.findViewById(R.id.present);
        absent       = root.findViewById(R.id.absent);
        percentage   = root.findViewById(R.id.percentage);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        arrayList = new ArrayList<>();
    }

    private void parseAttendanceData(JSONObject response) {
        try {
            total.setText(response.getString("TotalHours"));
            present.setText(response.getString("TotalAttendedHours"));
            absent.setText(response.getString("TotalAbsentHours"));
            percentage.setText(response.optString("TotalAttendedPercentage", "0.00"));
        } catch (JSONException e) {
            Log.e(TAG, "parseAttendanceData error: " + e.getMessage());
        }
    }

    private void parseCourseData(JSONObject response) {
        try {
            arrayList.clear();
            JSONArray subjectsArray = response.getJSONArray("Subjects");
            Map<String, Course> theoryMap = new HashMap<>();
            Map<String, Course> labMap    = new HashMap<>();

            for (int i = 0; i < subjectsArray.length(); i++) {
                JSONObject obj = subjectsArray.getJSONObject(i);
                String codeKey   = "subjectcode" + i;
                String nameKey   = "subjectname" + i;
                String totalKey  = "total" + i;
                String absentKey = "absent" + i;
                if (!obj.has(codeKey) || !obj.has(totalKey)
                        || !obj.has(absentKey)) continue;

                String code = obj.getString(codeKey);
                String name = obj.optString(nameKey, "");
                int    tot  = obj.getInt(totalKey);
                int    abs  = obj.getInt(absentKey);
                int    pres = tot - abs;
                float  pct  = tot > 0 ? (pres * 100f) / tot : 0f;

                Course course = new Course(code, name, "", tot, pres, pct);
                if (code.endsWith("A")) labMap.put(code.replace("A", ""), course);
                else theoryMap.put(code, course);
            }

            for (String code : theoryMap.keySet()) {
                Course theory = theoryMap.get(code);
                if (labMap.containsKey(code)) {
                    Course lab          = labMap.get(code);
                    int    finalTotal   = theory.getTotalClasses()
                            + lab.getTotalClasses();
                    int    finalPresent = theory.getAttendedClasses()
                            + lab.getAttendedClasses();
                    float  finalPct     = finalTotal > 0
                            ? (finalPresent * 100f) / finalTotal : 0f;
                    arrayList.add(new Course(code, theory.getName(), "",
                            finalTotal, finalPresent, finalPct));
                } else {
                    arrayList.add(theory);
                }
            }

            adapter = new CourseAdapter(arrayList);
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            Log.e(TAG, "parseCourseData error: " + e.toString());
        }
    }
}