package org.vignanuniversity.vucounselling_app.Screens;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.vignanuniversity.vucounselling_app.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Counselling extends Fragment {

    private View root;

    private MaterialButtonToggleGroup q1Group, q2Group, q3Group, q4Group, q5Group, q7Group;
    private Spinner q2Spinner, q3Spinner;
    private EditText q5IssuesEdit, remarksEdit, q7ReasonEdit;
    private LinearLayout q2Layout, q3Layout, q5Layout, q7Layout;

    private String regno = "", usercode = "", currentWeekName = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflates the card (the actual form)
        root = inflater.inflate(R.layout.counselling_question_card, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        usercode = sp.getString("regno", "");

        if (getArguments() != null) {
            regno = getArguments().getString("student_regno", "");
            currentWeekName = getArguments().getString("current_week", "");
        }

        initViews();
        setupConditionalVisibility();

        root.findViewById(R.id.submit_button).setOnClickListener(v -> sendCounsellingData());

        return root;
    }

    private void initViews() {
        q1Group = root.findViewById(R.id.toggleButtonQuestion1);
        q2Group = root.findViewById(R.id.toggleButtonQuestion2);
        q3Group = root.findViewById(R.id.toggleButtonQuestion3);
        q4Group = root.findViewById(R.id.toggleButtonQuestion4);
        q5Group = root.findViewById(R.id.toggleButtonQuestion5);
        q7Group = root.findViewById(R.id.toggleButtonQuestion7);

        q2Spinner  = root.findViewById(R.id.q2_spinner);
        q3Spinner  = root.findViewById(R.id.q3_spinner);
        q5IssuesEdit = root.findViewById(R.id.q5_issues_edit_text);
        q7ReasonEdit = root.findViewById(R.id.q7_reason_edit_text);
        remarksEdit  = root.findViewById(R.id.edit_text);

        q2Layout = root.findViewById(R.id.q2_conditional_layout);
        q3Layout = root.findViewById(R.id.q3_conditional_layout);
        q5Layout = root.findViewById(R.id.q5_conditional_layout);
        q7Layout = root.findViewById(R.id.q7_conditional_layout);
    }

    private void setupConditionalVisibility() {

        q2Group.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                q2Layout.setVisibility(
                        getSelectedText(q2Group).equals("Yes") ? View.VISIBLE : View.GONE
                );
            }
        });

        q3Group.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                q3Layout.setVisibility(
                        getSelectedText(q3Group).equals("Yes") ? View.VISIBLE : View.GONE
                );
            }
        });

        q5Group.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                q5Layout.setVisibility(
                        getSelectedText(q5Group).equals("Yes") ? View.VISIBLE : View.GONE
                );
            }
        });

        q7Group.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                q7Layout.setVisibility(
                        getSelectedText(q7Group).equals("Yes") ? View.VISIBLE : View.GONE
                );
            }
        });
    }

    private void sendCounsellingData() {

        String q1 = getSelectedText(q1Group);
        String q2 = getSelectedText(q2Group);
        String q3 = getSelectedText(q3Group);
        String q4 = getSelectedText(q4Group);
        String q5 = getSelectedText(q5Group);

        // conditional answers (only collected if Yes was selected)
        String q2Yes = q2.equals("Yes") ? q2Spinner.getSelectedItem().toString().trim() : "";
        String q3Yes = q3.equals("Yes") ? q3Spinner.getSelectedItem().toString().trim() : "";
        String q5Yes = q5.equals("Yes") ? q5IssuesEdit.getText().toString().trim() : "";

        String remarks = remarksEdit.getText().toString().trim();

        // Q7 is UI only — no DB column, so we just collect but do NOT send
        // (kept for future use or display)

        // Validation — Q1 to Q5 required
        if (q1.isEmpty() || q2.isEmpty() || q3.isEmpty()
                || q4.isEmpty() || q5.isEmpty()) {
            Toast.makeText(getContext(),
                    "Please answer all required questions (1–5)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.10.25/jspapi/test/givecounselling.jsp";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equalsIgnoreCase("Success")) {
                        Toast.makeText(getContext(),
                                "Submitted Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(),
                                "Server: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String msg = "Submission Failed";
                    if (error.networkResponse != null) {
                        msg += " (HTTP " + error.networkResponse.statusCode + ")";
                        try {
                            msg += "\n" + new String(error.networkResponse.data, "UTF-8");
                        } catch (Exception ignored) {}
                    } else if (error.getCause() != null) {
                        msg += ": " + error.getCause().getMessage();
                    }
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();

                // Questions mapped to DB columns
                param.put("firstq",    q1);         // last_week_performance
                param.put("secq",      q2);         // improve_student_performance
                param.put("secq_yes",  q2Yes);      // improve_student_performance_yes
                param.put("thirdq",    q3);         // changes_student_performance
                param.put("thirdq_yes",q3Yes);      // changes_student_performance_yes
                param.put("fourthq",   q4);         // attendance_satisfactory
                param.put("fifthq",    q5);         // student_discipline
                param.put("fifthq_yes",q5Yes);      // student_discipline_yes
                param.put("sixq",      remarks);    // counselling_message

                // Meta
                param.put("empcode",   usercode);
                param.put("weekname",  currentWeekName);
                param.put("attended",  "Present");  // counselling_attendance
                param.put("datetime",  getCurrentDateTime());
                param.put("registerno",regno);

                return param;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private String getSelectedText(MaterialButtonToggleGroup group) {
        int id = group.getCheckedButtonId();
        if (id != -1) {
            MaterialButton btn = root.findViewById(id);
            return btn.getText().toString();
        }
        return "";
    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date());
    }
}