package org.vignanuniversity.vucounselling_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import org.vignanuniversity.vucounselling_app.Adapter.URLs;
import org.vignanuniversity.vucounselling_app.MainScreens.EmployeeMainActivity;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText username, password;
    SharedPreferences preferences;
    LinearProgressIndicator loadingIndicator;
    View rootView;

    // 🔐 Play Store Test Credentials
    private static final String TEST_USERNAME = "test";
    private static final String TEST_PASSWORD = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initFunction();

        if (preferences.getBoolean("isLogin", false)) {
            startActivity(new Intent(this, EmployeeMainActivity.class));
            finish();
        }
    }

    private void initFunction() {
        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
        loadingIndicator = findViewById(R.id.loading_indicator);
        rootView = findViewById(android.R.id.content);
        preferences = getSharedPreferences("pref", MODE_PRIVATE);

        findViewById(R.id.submit_button).setOnClickListener(v -> {
            if (username.getText() == null || username.getText().toString().isEmpty()) {
                username.setError("Username is required");
                return;
            }
            if (password.getText() == null || password.getText().toString().isEmpty()) {
                password.setError("Password is required");
                return;
            }
            login();
        });
    }

    private void login() {
        String userInput = username.getText().toString().trim();
        String passInput = password.getText().toString().trim();

        // ✅ Play Store Test Login (NO API CALL)
        if (userInput.equals(TEST_USERNAME) && passInput.equals(TEST_PASSWORD)) {
            saveLoginAndProceed(userInput);
            return;
        }

        // 🔄 Normal Server Login
        loadingIndicator.setVisibility(View.VISIBLE);
        setScreenOpacity(0.5f);

        String url = URLs.getLoginUrl();

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    loadingIndicator.setVisibility(View.GONE);
                    setScreenOpacity(1.0f);

                    String flag = response.trim();
                    Log.d("LoginResponse", flag);

                    if (flag.equalsIgnoreCase("Success")) {
                        saveLoginAndProceed(userInput);
                    } else {
                        Toast.makeText(this,
                                "Incorrect RegisterId or Password",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingIndicator.setVisibility(View.GONE);
                    setScreenOpacity(1.0f);
                    Log.e("VolleyError", error.toString());
                    Toast.makeText(this,
                            "Network error. Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
        ) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("usercode", userInput);
                param.put("passcode", passInput);
                return param;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // ✅ Common login success handler
    private void saveLoginAndProceed(String regNo) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLogin", true);
        editor.putString("regno", regNo);
        editor.apply();

        startActivity(new Intent(this, EmployeeMainActivity.class));
        finish();
    }

    private void setScreenOpacity(float opacity) {
        rootView.setAlpha(opacity);
    }
}
