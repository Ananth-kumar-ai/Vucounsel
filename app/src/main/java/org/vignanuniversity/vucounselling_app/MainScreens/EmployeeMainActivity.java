package org.vignanuniversity.vucounselling_app.MainScreens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import org.vignanuniversity.vucounselling_app.Adapter.OnScrollListener;
import org.vignanuniversity.vucounselling_app.Adapter.URLs;
import org.vignanuniversity.vucounselling_app.LoginActivity;
import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.Screens.FeedBack;
import org.vignanuniversity.vucounselling_app.Screens.Home;
import org.vignanuniversity.vucounselling_app.Screens.Marks;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EmployeeMainActivity extends AppCompatActivity implements OnScrollListener {

    ImageView profile;
    TextView name, empcode;
    SharedPreferences sharedPreferences;
    LinearProgressIndicator loadingIndicator;
    View rootView;
    final Fragment fragment11 = new Home();
    final Fragment fragment22 = new Marks();
    final Fragment fragment33 = new FeedBack();
    private FragmentManager fm;
    Fragment active = fragment11;
    private LinearLayout marksContainer, homeContainer, feedbackContainer;
    private ImageView marksIcon, homeIcon, feedbackIcon;
    private TextView marksText, homeText, feedbackText;
    private Animation fadeIn, fadeOut, slideLeft, slideReset;
    private int currentSelectedIndex = 1;

    ImageView back_button,logout;
    private View bottomNav;
    private boolean isBottomNavVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        initFunction();
        initFragments();
        setupNavigation();
    }
    @Override
    public void onScrollUp() {
        if (!isBottomNavVisible) {
            isBottomNavVisible = true;
            bottomNav.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    @Override
    public void onScrollDown() {
        if (isBottomNavVisible) {
            isBottomNavVisible = false;
            bottomNav.animate()
                    .translationY(bottomNav.getHeight())
                    .alpha(0f)
                    .setDuration(300)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
        }
    }

    private void setupNavigation() {
        marksContainer = findViewById(R.id.marks_container);
        homeContainer = findViewById(R.id.home_container);
        feedbackContainer = findViewById(R.id.feedback_container);

        marksIcon = findViewById(R.id.marks_icon);
        homeIcon = findViewById(R.id.home_icon);
        feedbackIcon = findViewById(R.id.feedback_icon);

        marksText = findViewById(R.id.marks_text);
        homeText = findViewById(R.id.home_text);
        feedbackText = findViewById(R.id.feedback_text);


        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        slideReset = AnimationUtils.loadAnimation(this, R.anim.slide_reset);


        final LinearLayout[] containers = {marksContainer, homeContainer, feedbackContainer};
        final ImageView[] icons = {marksIcon, homeIcon, feedbackIcon};
        final TextView[] texts = {marksText, homeText, feedbackText};


        for (int i = 0; i < containers.length; i++) {
            final int index = i;
            containers[i].setOnClickListener(v -> {
                if (currentSelectedIndex == index) {
                    return;
                }
                icons[currentSelectedIndex].clearAnimation();
                icons[currentSelectedIndex].startAnimation(slideReset);
                texts[currentSelectedIndex].startAnimation(fadeOut);
                texts[currentSelectedIndex].setAlpha(0f);
                containers[currentSelectedIndex].setSelected(false);

                currentSelectedIndex = index;
                containers[index].setSelected(true);

                icons[index].clearAnimation();
                icons[index].startAnimation(slideLeft);
                texts[index].setAlpha(0f);
                texts[index].startAnimation(fadeIn);
                texts[index].setAlpha(1f);

                loadingIndicator.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                if (index == 0) {
                    findViewById(R.id.back_press).setVisibility(View.VISIBLE);
                    findViewById(R.id.logout).setVisibility(View.GONE);
                    feedbackText.setVisibility(View.GONE);
                    transaction.replace(R.id.fragment_container, fragment22);
                    active = fragment22;
                } else if (index == 1) {
                    findViewById(R.id.logout).setVisibility(View.VISIBLE);
                    findViewById(R.id.back_press).setVisibility(View.GONE);
                    feedbackText.setVisibility(View.GONE);
                    transaction.replace(R.id.fragment_container, fragment11);
                    active = fragment11;
                } else if (index == 2) {
                    findViewById(R.id.logout).setVisibility(View.VISIBLE);
                    findViewById(R.id.back_press).setVisibility(View.GONE);
                    feedbackText.setVisibility(View.VISIBLE);
                    transaction.replace(R.id.fragment_container, fragment33);
                    active = fragment33;
                }else{
                    findViewById(R.id.logout).setVisibility(View.VISIBLE);
                    findViewById(R.id.back_press).setVisibility(View.GONE);
                    feedbackText.setVisibility(View.GONE);
                    transaction.replace(R.id.fragment_container, fragment11);
                    active = fragment11;
                }

                transaction.addToBackStack(null);
                transaction.commit();
                loadingIndicator.postDelayed(() -> loadingIndicator.setVisibility(View.GONE), 300);  // Set the delay according to the animation duration or transition time


            });
        }
        homeContainer.setSelected(true);
        homeIcon.startAnimation(slideLeft);
        homeText.setAlpha(1f);
    }


    private void initFragments() {
        fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_container, fragment11, "1").commit();
    }

    private void LogoutFunction() {
        findViewById(R.id.logout).setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(EmployeeMainActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void initFunction() {
        bottomNav = findViewById(R.id.bottom_menubar);
        loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);
        rootView = findViewById(android.R.id.content);
        sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        String usercode = sharedPreferences.getString("regno", "");
        profile = findViewById(R.id.chari_profile);
        name = findViewById(R.id.user_name);
        empcode = findViewById(R.id.user_code);

        LogoutFunction();
        employeeDetails(usercode);

        String imageUrl = URLs.getImageUrl(usercode);
        Log.d("imageurl", imageUrl);
        Glide.with(this).load(imageUrl).into(profile);
        loadingIndicator.setVisibility(View.GONE);
        empcode.setText("Emp Code : "+usercode);
    }

    private void employeeDetails(String usercode) {
        loadingIndicator.setVisibility(View.VISIBLE);
        String url = URLs.getEmployeeDetailsUrl(usercode);
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    try {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject bookObj = jsonArray.getJSONObject(i);

                            String cname = bookObj.optString("name");
                            String cdept = bookObj.optString("Department");
                            String cempcode = bookObj.optString("empcode");
                            String cdesignation = bookObj.optString("designation");
                            name.setText(cname);
                            loadingIndicator.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        loadingIndicator.setVisibility(View.GONE);
                        Log.d("Error", e.getMessage());
                    }
                } catch (JSONException e) {
                    loadingIndicator.setVisibility(View.GONE);
                    Log.d("Error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingIndicator.setVisibility(View.GONE);
                if (error == null || error.getMessage() == null) {
                    Log.d("Error", "No Internet Connection");
                } else {
                    Log.d("Error", "Error: " + error.getMessage());
                }
            }
        });
        rq.add(request);
    }
}