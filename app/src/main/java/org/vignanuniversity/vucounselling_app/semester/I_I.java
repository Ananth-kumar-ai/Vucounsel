package org.vignanuniversity.vucounselling_app.semester;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.vignanuniversity.vucounselling_app.Adapter.SemRecyclerAdapter;
import org.vignanuniversity.vucounselling_app.DataFetcher.All_DataFetcher;
import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.semData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class I_I extends Fragment {
    SharedPreferences preferences;
    static RequestQueue rq;
    static int cr,bs,ps;

    LinearLayout linearLayout;
    View root;
    RecyclerView recyclerView;
    static ArrayList<semData> studentData;

    TextView need_to_hide_if_applied;
    static String sgpa="-",reg_no="";
    // create a parameterless constructor
    public I_I(String reg_no) {
        this.reg_no = reg_no;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_i__i, container, false);
        studentData = new ArrayList<>();
        need_to_hide_if_applied = root.findViewById(R.id.not_eligible_text_view);
        linearLayout = root.findViewById(R.id.I_I);
        recyclerView = root.findViewById(R.id.I_I_recycler_view);


        getData(reg_no);

        return root;
    }

    public void getData(String reg_no){
        rq = Volley.newRequestQueue(getContext());
        cr = 0;
        bs = 0;
        ps = 0;
        All_DataFetcher.semesterMarksFetcher(getContext(),reg_no,1,1,false,this::parseData);
    }

    public void parseData(JSONObject response) {
        try {
            JSONArray jsonArray = response.optJSONArray("1year1semester");
            if(jsonArray == null){
                need_to_hide_if_applied.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                return;
            }else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    need_to_hide_if_applied.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    JSONObject hit = jsonArray.getJSONObject(i);

                    String code = hit.getString("subjectcode");
                    String subjectName = hit.getString("subjectname");


                    String gradePoints = hit.getString("gradepoints");
                    String letterGrade = hit.getString("grade");

                    if (letterGrade.equals("I") || letterGrade.equals("R")) {
                        bs++;
                    } else {
                        ps++;
                    }



                    semData s = new semData();
                    s.setCode(code);
                    s.setName(subjectName);
                    s.setPoints(gradePoints);
                    s.setGrade(letterGrade);
                    studentData.add(s);
                }

                Collections.sort(studentData, new Comparator<semData>() {
                    @Override
                    public int compare(semData o1, semData o2) {
                        float gradePoints1 = Float.parseFloat(o1.getPoints());
                        float gradePoints2 = Float.parseFloat(o2.getPoints());
                        return Float.compare(gradePoints2, gradePoints1); // Descending order
                    }
                });


                SemRecyclerAdapter studentRecyclerAdapter = new SemRecyclerAdapter(getContext(), studentData);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(studentRecyclerAdapter);
            }

        } catch (JSONException e) {
            Log.d("error_in", e.toString());
            Toast.makeText(getContext(), "Data parsing error. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }


    public static int getCr() {
        return cr;
    }

    public static int getBs() {
        return bs;
    }

    public static int getPs() {
        return ps;
    }

    public static String getSgpa() {
        return sgpa;
    }

}