package org.vignanuniversity.vucounselling_app.Screens;

import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class Traits extends Fragment {
    View root;
    String url = "http://160.187.169.14/jspapi/ctraitsreport.jsp";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.traits_questions, container, false);

        root.findViewById(R.id.submit_button).setOnClickListener(v -> submitTraitsData());
        return root;
    }

    private void submitTraitsData() {
        String[] rflag = new String[30];
        for (int i = 1; i < 30; i++) {
            int editTextId = getResources().getIdentifier("editText" + i, "id", getActivity().getPackageName());
            EditText editText = root.findViewById(editTextId);
            rflag[i] = editText != null ? editText.getText().toString() : "";
        }

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Toast.makeText(getActivity(), "Traits submitted successfully!", Toast.LENGTH_SHORT).show();
        }, error -> {
            Toast.makeText(getActivity(), "Error submitting traits: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                for (int i = 1; i < 30; i++) {
                    param.put("remarks" + i, rflag[i]);
                }

                String registerno = "211FA04389";
                String empcode = "01918";
                String finalDatetime = "2023-10-01 12:00:00";
                String midno = "M1";

                param.put("registerno", registerno);
                param.put("empcode", empcode);
                param.put("datetime", finalDatetime);
                param.put("finalMid", midno);

                return param;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(request);
    }
}