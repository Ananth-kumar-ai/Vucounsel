package org.vignanuniversity.vucounselling_app.Screens;

import static android.content.Context.MODE_PRIVATE;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.vignanuniversity.vucounselling_app.Adapter.OnScrollListener;
import org.vignanuniversity.vucounselling_app.Adapter.StudentAdapter;
import org.vignanuniversity.vucounselling_app.Adapter.StudentClick;
import org.vignanuniversity.vucounselling_app.Adapter.URLs;
import org.vignanuniversity.vucounselling_app.DataFetcher.All_DataFetcher;
import org.vignanuniversity.vucounselling_app.MainScreens.AttendanceMain;
import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.studentClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Home extends Fragment  implements StudentClick {

    TextView textView;
    View root;
    private RecyclerView recyclerView;
    SearchView searchView;
    private StudentAdapter studentAdapter;
    private List<studentClass> studentClassList;
    SharedPreferences preferences;
    private OnScrollListener scrollListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.home_activity, container, false);
        initIDS();
        setupSearchView();
        setupRecyclerViewScrollListener();
        return root;
    }

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

    private void initIDS() {
        preferences = root.getContext().getSharedPreferences("pref", MODE_PRIVATE);
        recyclerView = root.findViewById(R.id.recyclerView);
        searchView = root.findViewById(R.id.searchView_Bookslist);

        studentClassList = new ArrayList<>();
        String url = URLs.getStudentReportsUrl(preferences.getString("regno",""));
        Log.d("URL", url);
        RequestQueue rq = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    parseStudentData(jsonArray);
                } catch (JSONException e) {
                    Log.d("Error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.getMessage());
            }
        });
        rq.add(request);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentAdapter = new StudentAdapter(studentClassList, Home.this);
        recyclerView.setAdapter(studentAdapter);

    }
    private void parseStudentData(JSONArray jsonArray) {
        try {
            List<studentClass> books = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject bookObj = jsonArray.getJSONObject(i);
                String name = bookObj.optString("studentName");
                String regno = bookObj.optString("regno");
                String att = bookObj.optString("Atten!@nce");

                All_DataFetcher.cgpaFetcher(getContext(), regno, false,jsonObject -> {
                    String cgpaValue = "";
                    try {
                        if (jsonObject == null) {
                            Log.d("CGPAParser", "JSON Object is null");
                        }
                        String cgpaString = jsonObject.optString("cgpa_Excluding", "?");
                        if (cgpaString.equals("?") || !cgpaString.contains(".") || cgpaString.contains("0.00")) {
                            cgpaString = jsonObject.optString("Aggregate", "?");
                            Log.d("CGPAParser", "CGPA not available or malformed: " + cgpaString);
                        }
                        String[] parts = cgpaString.split(Pattern.quote("."));
                        if (parts.length != 2) {
                            Log.d("CGPAParser", "Unexpected CGPA format: " + cgpaString);
                        }
                        String integerPart = parts[0];
                        String decimalPart = parts[1];
                        cgpaValue = integerPart + "." + decimalPart;
                    } catch (Exception e) {
                        Log.d("CGPAParser", "Error parsing CGPA: " + e.getMessage());
                    }
                    studentClass studentClass = new studentClass(regno, name, "", att, cgpaValue, "", "", "");
                    books.add(studentClass);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            studentClassList.clear();
                            books.sort((s1, s2) -> s1.getRegno().compareToIgnoreCase(s2.getRegno()));
                            studentClassList.addAll(books);
                            studentAdapter = new StudentAdapter(studentClassList, Home.this);
                            recyclerView.setAdapter(studentAdapter);

                        }
                    });
                });
            }
        } catch (JSONException e) {
            Log.d("Error", e.getMessage());
        }
    }

    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                studentAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint("Search Here");
    }

    @Override
    public void onStudentClick(studentClass student) {
        Intent intent = new Intent(getContext(), AttendanceMain.class);
        intent.putExtra("student_regno", student.getRegno());
        intent.putExtra("student_name", student.getName());

        ActivityOptions options = ActivityOptions.makeCustomAnimation(
                getContext(),
                R.anim.screen_in,
                R.anim.screen_out);

        startActivity(intent, options.toBundle());
    }
}