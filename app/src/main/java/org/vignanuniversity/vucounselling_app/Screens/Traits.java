package org.vignanuniversity.vucounselling_app.Screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class Traits extends Fragment {

    private View root;
    private final String url = "http://192.168.10.25/jspapi/test/ctraitsreport.jsp";
    private String studentRegno = "";
    private String currentWeek  = "";
    private String empcode      = "";

    // Direct resource ID arrays — no getIdentifier() risk
    private final int[] spinnerIds = {
            0,
            R.id.spinner1,  R.id.spinner2,  R.id.spinner3,  R.id.spinner4,
            R.id.spinner5,  R.id.spinner6,  R.id.spinner7,  R.id.spinner8,
            R.id.spinner9,  R.id.spinner10, R.id.spinner11, R.id.spinner12,
            R.id.spinner13, R.id.spinner14, R.id.spinner15, R.id.spinner16,
            R.id.spinner17, R.id.spinner18, R.id.spinner19, R.id.spinner20,
            R.id.spinner21, R.id.spinner22, R.id.spinner23, R.id.spinner24,
            R.id.spinner25, R.id.spinner26, R.id.spinner27, R.id.spinner28,
            R.id.spinner29
    };

    private final int[] editTextIds = {
            0,
            R.id.editText1,  R.id.editText2,  R.id.editText3,  R.id.editText4,
            R.id.editText5,  R.id.editText6,  R.id.editText7,  R.id.editText8,
            R.id.editText9,  R.id.editText10, R.id.editText11, R.id.editText12,
            R.id.editText13, R.id.editText14, R.id.editText15, R.id.editText16,
            R.id.editText17, R.id.editText18, R.id.editText19, R.id.editText20,
            R.id.editText21, R.id.editText22, R.id.editText23, R.id.editText24,
            R.id.editText25, R.id.editText26, R.id.editText27, R.id.editText28,
            R.id.editText29
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.traits_questions, container, false);

        if (getArguments() != null) {
            studentRegno = getArguments().getString("student_regno", "");
            currentWeek  = getArguments().getString("current_week",  "");
        }

        SharedPreferences sp = getActivity()
                .getSharedPreferences("pref", Context.MODE_PRIVATE);
        empcode = sp.getString("regno", "");

        root.findViewById(R.id.submit_button)
                .setOnClickListener(v -> submitTraitsData());

        return root;
    }

    private void submitTraitsData() {

        // ── Module selection ──
        MaterialButtonToggleGroup moduleGroup =
                root.findViewById(R.id.toggleButtonQuestion1);
        int checkedId = moduleGroup.getCheckedButtonId();

        if (checkedId == -1) {
            Toast.makeText(getActivity(),
                    "Please select Module 1 or Module 2",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        MaterialButton selectedBtn = root.findViewById(checkedId);
        // "Module 1" → "M1",  "Module 2" → "M2"
        String finalMid = selectedBtn.getText().toString()
                .trim().replace("Module ", "M");

        // ── Collect all 29 rows ──
        // tv = numeric severity from spinner (0-5)
        // tc = text remarks from edittext
        final String[] tv = new String[30];
        final String[] tc = new String[30];

        for (int i = 1; i <= 29; i++) {

            // Spinner — selected item is numeric string "0","1","2","3","4","5"
            Spinner spinner = root.findViewById(spinnerIds[i]);
            if (spinner != null && spinner.getSelectedItem() != null) {
                tv[i] = spinner.getSelectedItem().toString().trim();
            } else {
                tv[i] = "0"; // default 0 if not selected
            }

            // EditText — remarks text
            EditText et = root.findViewById(editTextIds[i]);
            tc[i] = (et != null) ? et.getText().toString().trim() : "";
        }

        final String fMid   = finalMid;
        final String fRegno = studentRegno;
        final String fEmp   = empcode;
        final String fWeek  = currentWeek;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equalsIgnoreCase("Success")) {
                        Toast.makeText(getActivity(),
                                "Traits submitted successfully!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Shows exact server error for debugging
                        Toast.makeText(getActivity(),
                                "Server: " + response,
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String msg = "Network Error";
                    if (error.networkResponse != null) {
                        try {
                            msg = new String(error.networkResponse.data, "UTF-8");
                        } catch (Exception ex) {
                            msg = "HTTP " + error.networkResponse.statusCode;
                        }
                    } else if (error.getMessage() != null) {
                        msg = error.getMessage();
                    }
                    Toast.makeText(getActivity(),
                            "Error: " + msg, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();

                param.put("registerno", fRegno);
                param.put("empcode",    fEmp);
                param.put("finalMid",   fMid);
                param.put("weekname",   fWeek);

                for (int i = 1; i <= 29; i++) {
                    param.put("trait"   + i, tv[i]); // numeric: "0"-"5"
                    param.put("remarks" + i, tc[i]); // text remarks
                }

                return param;
            }
        };

        Volley.newRequestQueue(getActivity().getApplicationContext())
                .add(request);
    }
}