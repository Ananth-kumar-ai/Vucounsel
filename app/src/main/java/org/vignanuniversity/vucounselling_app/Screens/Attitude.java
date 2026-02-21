package org.vignanuniversity.vucounselling_app.Screens;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class Attitude extends Fragment {
    private View root;
    private MaterialButtonToggleGroup moduleType;
    private String send1 = "", send2 = "", send3 = "", send4 = "", send5 = "", send6 = "", send7 = "", send8 = "", send9 = "";
    private String regno = "", cyear = "", sem = "", usercode = "", currentWeekName = "";

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.attitude_questions, container, false);

        sharedPreferences = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        usercode = sharedPreferences.getString("regno", "");
        if (getArguments() != null) {
            regno = getArguments().getString("student_regno");
            cyear = getArguments().getString("cyear");
            sem = getArguments().getString("semester");
            currentWeekName = getArguments().getString("current_week", "");
//            Log.d("CounsellingTAB", "onCreateView: " + regno + " " + cyear + " " + sem+ " " + usercode+ " " + currentWeekName);
        }

        moduleType = root.findViewById(R.id.toggleButtonQuestion1);

        setupSpinner(R.id.options_spinner1, selected -> send1 = selected);
        setupSpinner(R.id.options_spinner2, selected -> send2 = selected);
        setupSpinner(R.id.options_spinner3, selected -> send3 = selected);
        setupSpinner(R.id.options_spinner4, selected -> send4 = selected);
        setupSpinner(R.id.options_spinner5, selected -> send5 = selected);
        setupSpinner(R.id.options_spinner6, selected -> send6 = selected);
        setupSpinner(R.id.options_spinner7, selected -> send7 = selected);
        setupSpinner(R.id.options_spinner8, selected -> send8 = selected);
        setupSpinner(R.id.options_spinner9, selected -> send9 = selected);

        MaterialButton submitButton = root.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(v -> {
            if (send1.isEmpty() || send2.isEmpty() || send3.isEmpty() || send4.isEmpty()
                    || send5.isEmpty() || send6.isEmpty() || send7.isEmpty()
                    || send8.isEmpty() || send9.isEmpty()) {
                Toast.makeText(getContext(), "Please answer all questions", Toast.LENGTH_SHORT).show();
                return;
            }

            String finalMidno = getSelectedText(moduleType);
            String empcode = usercode;
            String registerno = regno;
            String finalDatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            String url = "http://160.187.169.14/jspapi/cattitudereport.jsp";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> Toast.makeText(getContext(), "Submitted Successfully", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> param = new HashMap<>();
                    param.put("send1", send1);
                    param.put("send2", send2);
                    param.put("send3", send3);
                    param.put("send4", send4);
                    param.put("send5", send5);
                    param.put("send6", send6);
                    param.put("send7", send7);
                    param.put("send8", send8);
                    param.put("send9", send9);
                    param.put("empcode", empcode);
                    param.put("finalMid", finalMidno);
                    param.put("registerno", registerno);
                    param.put("datetime", finalDatetime);
                    return param;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(requireContext());
            queue.add(request);
        });

        return root;
    }

    private void setupSpinner(int spinnerId, final OnSpinnerSelected callback) {
        Spinner spinner = root.findViewById(spinnerId);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString().trim();
                if (!selected.toLowerCase().contains("select")) {
                    callback.onSelected(selected);
                } else {
                    callback.onSelected("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                callback.onSelected("");
            }
        });
    }
    private String getSelectedText(MaterialButtonToggleGroup group) {
        int selectedId = group.getCheckedButtonId();
        if (selectedId != -1) {
            MaterialButton selectedButton = root.findViewById(selectedId);
            return selectedButton.getText().toString();
        }
        return "";
    }
    private interface OnSpinnerSelected {
        void onSelected(String selected);
    }
}