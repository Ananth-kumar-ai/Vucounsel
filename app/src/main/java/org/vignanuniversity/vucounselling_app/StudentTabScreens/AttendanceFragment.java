package org.vignanuniversity.vucounselling_app.StudentTabScreens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.vignanuniversity.vucounselling_app.Adapter.CourseAdapter;
import org.vignanuniversity.vucounselling_app.Adapter.OnScrollListener;
import org.vignanuniversity.vucounselling_app.DataFetcher.All_DataFetcher;
import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendanceFragment extends Fragment {

    View root;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private ArrayList<Course> arrayList;
    SharedPreferences preferences;
    RequestQueue requestQueue;
    String regno = "";
    TextView total, present, absent, percentage;
    private OnScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_attendance, container, false);

        preferences = root.getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        regno = getArguments().getString("student_regno", "");


        initializeViews();
        setupRecyclerViewScrollListener();
        requestQueue = Volley.newRequestQueue(getContext());

        // Fetch Attendance Data
        All_DataFetcher.attendanceDataFetcher(getContext(), regno, false, this::parseAttendanceData);
        All_DataFetcher.attendanceDataFetcher(getContext(), regno, false, this::parseCourseData);


        return root;
    }

    // setup a scroll listener to the recycler view
    private void setupRecyclerViewScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private void initializeViews() {
        total = root.findViewById(R.id.total);
        present = root.findViewById(R.id.present);
        absent = root.findViewById(R.id.absent);
        percentage = root.findViewById(R.id.percentage);

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
            Log.e("AttendanceParsing", "Error parsing attendance data: " + e.getMessage());
        }
    }

    private void parseCourseData(JSONObject response) {

        try {
            arrayList.clear();

            JSONArray subjectsArray = response.getJSONArray("Subjects");

            Map<String, Course> theoryMap = new HashMap<>();
            Map<String, Course> labMap = new HashMap<>();

            for (int i = 0; i < subjectsArray.length(); i++) {

                JSONObject obj = subjectsArray.getJSONObject(i);

                String codeKey = "subjectcode" + i;
                String nameKey = "subjectname" + i;
                String totalKey = "total" + i;
                String absentKey = "absent" + i;

                if (!obj.has(codeKey) || !obj.has(totalKey) || !obj.has(absentKey)) {
                    continue;
                }

                String code = obj.getString(codeKey);
                String name = obj.optString(nameKey, "");
                int total = obj.getInt(totalKey);
                int absent = obj.getInt(absentKey);
                int present = total - absent;

                float percentage = total > 0 ? (present * 100f) / total : 0f;

                Course course = new Course(
                        code,
                        name,
                        "",
                        total,
                        present,
                        percentage
                );

                if (code.endsWith("A")) {
                    labMap.put(code.replace("A", ""), course);
                } else {
                    theoryMap.put(code, course);
                }
            }

            for (String code : theoryMap.keySet()) {

                Course theory = theoryMap.get(code);

                if (labMap.containsKey(code)) {

                    Course lab = labMap.get(code);

                    int finalTotal = theory.getTotalClasses() + lab.getTotalClasses();
                    int finalPresent = theory.getAttendedClasses() + lab.getAttendedClasses();
                    float finalPercentage = finalTotal > 0
                            ? (finalPresent * 100f) / finalTotal
                            : 0f;

                    arrayList.add(new Course(
                            code,
                            theory.getName(),
                            "",
                            finalTotal,
                            finalPresent,
                            finalPercentage
                    ));
                } else {
                    arrayList.add(theory);
                }
            }

            adapter = new CourseAdapter(arrayList);
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            Log.e("ATT_PARSE_ERROR", e.toString());
        }
    }

}
