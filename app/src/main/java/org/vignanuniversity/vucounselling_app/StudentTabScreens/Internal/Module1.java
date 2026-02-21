package org.vignanuniversity.vucounselling_app.StudentTabScreens.Internal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import org.vignanuniversity.vucounselling_app.Adapter.internalRecyclerAdapter;
import org.vignanuniversity.vucounselling_app.DataFetcher.All_DataFetcher;
import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.internalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module1 extends Fragment implements internalRecyclerAdapter.SubjectClick{
    View root;
    LinearLayout linearLayout;
    static RecyclerView recyclerView;
    static ArrayList<internalData> studentData;
    ArrayList<String> subject_names1 = new ArrayList<>();
    ArrayList<ArrayList<String>> tableData = new ArrayList<>();
    ArrayList<String> subjectNames = new ArrayList<>();
    RequestQueue rq;
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.module_marks, container, false);
        studentData = new ArrayList<>();

        preferences = root.getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

        int spanCount = 2;
        linearLayout = root.findViewById(R.id.Internal);
        recyclerView = root.findViewById(R.id.im_recycler_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanCount,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        internalRecyclerAdapter studentRecyclerAdapter = new internalRecyclerAdapter(getContext(),studentData,this);
        recyclerView.setAdapter(studentRecyclerAdapter);
        All_DataFetcher.internalMarksFetcher(getContext(),getArguments().getString("student_regno", ""),false,this::handleinternalMarksData);
        return root;
    }

    private void handleinternalMarksData(JSONObject response) {
        try {
            int l1 = 0, l2 = 0;
            tableData.clear();
            // Clear previous data
            studentData.clear();
            subject_names1.clear();

            JSONArray jsonArray00 = response.getJSONArray("subject");
            l1 = jsonArray00.length();


            List<String> excludeSubjects = Arrays.asList("Lab", "Counseling", "library","NPTEL","-L");
            for (int i = 0; i < jsonArray00.length(); i++) {
                String subjectName = jsonArray00.getString(i);
                boolean shouldExclude = false;
                for (String keyword : excludeSubjects) {
                    if (subjectName.contains(keyword)) {
                        shouldExclude = true;
                        break;
                    }
                }

                if (!shouldExclude) {
                    subject_names1.add(subjectName);
                    internalData s = new internalData();
                    s.setName(subjectName);
                    studentData.add(s);
                }
            }


            // Populate subject names
            JSONArray subjectArray = response.getJSONArray("subject");
            if (subjectArray.length() > 0) {
                for (int y = 0; y < l1; y++) {
                    String subjectName = subjectArray.getString(y);
                    subjectNames.add(subjectName);
                }
            }
            ArrayList<String> headerRow = new ArrayList<>(subjectNames);
            tableData.add(headerRow);

            JSONObject jsonArray11 = response.getJSONObject("marks");

            // Loop through rows
            for (int x = 0; x < 31; x++) {
                ArrayList<String> rowData = new ArrayList<>();

                // Loop through columns
                for (int y = 0; y <= l1; y++) {
                    // Extract cell data
                    String cellData = jsonArray11.optString(x + "_" + y, "-");
                    rowData.add(cellData);
                }
                tableData.add(rowData);
            }

            for (ArrayList<String> row : tableData) {
                System.out.println(row);
            }

            // Notify the adapter of data changes
            if (recyclerView.getAdapter() != null) {
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showError("Error parsing internal marks data: " + e.getMessage());
            Log.d("int_error_in", e.toString());
        }
    }

    @Override
    public void onClickListener(internalData internalData) {
        String clickedSubject = internalData.getName();
        printSubjectData(clickedSubject);
    }


    private void printSubjectData(String subjectName) {
        System.out.println("Clicked Subject: " + subjectName);
        int subjectIndex = subjectNames.indexOf(subjectName);
        if (subjectIndex == -1) {
            System.out.println("Subject not found!");
            return;
        }
        System.out.println("Header: " + subjectNames);
        ArrayList<String> marks = new ArrayList<>();
        for (ArrayList<String> row : tableData) {
            if (row.get(0).startsWith("Module1")) {
                String data = row.get(subjectIndex + 1);
                marks.add((data));
            }
        }
        Intent intent  = new Intent(getContext(), internal_item.class);
        intent.putExtra("internal Details",subjectName);
        intent.putStringArrayListExtra("marksList", marks);
        intent.putExtra("module Details","Module - I");
        startActivity(intent);
    }

    private void showError(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
    }


}
