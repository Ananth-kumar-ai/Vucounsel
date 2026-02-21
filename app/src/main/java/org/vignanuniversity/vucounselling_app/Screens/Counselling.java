package org.vignanuniversity.vucounselling_app.Screens;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.vignanuniversity.vucounselling_app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Counselling extends Fragment {

    View root;

    private MaterialButtonToggleGroup q1Group, q2Group, q3Group, q4Group, q5Group;
    private EditText editTextRemarks;
    private MaterialButton submitButton;
    private String regno = "", cyear = "", sem = "", usercode = "",currentWeekName = "";
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.counselling_question_card, container, false);

        sharedPreferences = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        usercode = sharedPreferences.getString("regno", "");
        if (getArguments() != null) {
            regno = getArguments().getString("student_regno");
            cyear = getArguments().getString("cyear");
            sem = getArguments().getString("semester");
            currentWeekName = getArguments().getString("current_week", "");
//            Log.d("CounsellingTAB", "onCreateView: " + regno + " " + cyear + " " + sem+ " " + usercode+ " " + currentWeekName);
        }

        q1Group = root.findViewById(R.id.toggleButtonQuestion1);
        q2Group = root.findViewById(R.id.toggleButtonQuestion2);
        q3Group = root.findViewById(R.id.toggleButtonQuestion3);
        q4Group = root.findViewById(R.id.toggleButtonQuestion4);
        q5Group = root.findViewById(R.id.toggleButtonQuestion5);
        editTextRemarks = root.findViewById(R.id.edit_text);
        submitButton = root.findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> sendCounsellingData());

        return root;
    }

    private void sendCounsellingData() {
        String firstq = getSelectedText(q1Group);
        String secq = getSelectedText(q2Group);
        String thirdq = getSelectedText(q3Group);
        String fourthq = getSelectedText(q4Group);
        String fifthq = getSelectedText(q5Group);
        String sixq = editTextRemarks.getText().toString().trim();

        if (firstq.isEmpty() || secq.isEmpty() || thirdq.isEmpty()
                || fourthq.isEmpty() || fifthq.isEmpty()) {
            Toast.makeText(getContext(), "Please answer all 5 questions.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sixq.isEmpty()) {
            Toast.makeText(getContext(), "Please provide remarks in Question 6.", Toast.LENGTH_SHORT).show();
            return;
        }

        String empcode = usercode;
        String weekname = currentWeekName;
        String attended = "Yes";
        String datetime = getCurrentDateTime();
        String registerno = regno;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest request = new StringRequest(Request.Method.POST,
                "http://160.187.169.14/jspapi/givecounselling.jsp",
                response -> Toast.makeText(getContext(), "Submitted Successfully", Toast.LENGTH_SHORT).show(),
                error -> {
                    String errorMsg = "Submission Failed";
                    if (error.networkResponse != null) {
                        errorMsg += " - HTTP Code: " + error.networkResponse.statusCode;

                        try {
                            String body = new String(error.networkResponse.data, "UTF-8");
                            errorMsg += "\nResponse: " + body;
                        } catch (Exception e) {
                            errorMsg += "\nError parsing server response.";
                        }
                    } else if (error.getCause() != null) {
                        errorMsg += " - Cause: " + error.getCause().getMessage();
                    } else {
                        errorMsg += " - Unknown error occurred.";
                    }

                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("firstq", firstq);
                param.put("secq", secq);
                param.put("thirdq", thirdq);
                param.put("fourthq", fourthq);
                param.put("fifthq", fifthq);
                param.put("sixq", sixq);
                param.put("empcode", empcode);
                param.put("weekname", weekname);
                param.put("attended", attended);
                param.put("datetime", datetime);
                param.put("registerno", registerno);
                return param;
            }
        };
        Log.d("VOLLEY_DEBUG", "Submitting: " + firstq + ", " + secq + ", " + thirdq + ", " + fourthq + ", " + fifthq + ", " + sixq);
        requestQueue.add(request);
    }


    private String getSelectedText(MaterialButtonToggleGroup group) {
        int selectedId = group.getCheckedButtonId();
        if (selectedId != -1) {
            MaterialButton selectedButton = root.findViewById(selectedId);
            return selectedButton.getText().toString();
        }
        return "";
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
