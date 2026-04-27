package org.vignanuniversity.vucounselling_app.MainScreens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import org.vignanuniversity.vucounselling_app.Adapter.OnScrollListener;
import org.vignanuniversity.vucounselling_app.Adapter.StudentMyViewPager;
import org.vignanuniversity.vucounselling_app.Adapter.URLs;
import org.vignanuniversity.vucounselling_app.DataFetcher.All_DataFetcher;
import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.Screens.dynamicweeks;
import org.vignanuniversity.vucounselling_app.StudentTabScreens.AttendanceFragment;
import org.vignanuniversity.vucounselling_app.StudentTabScreens.Internal.Module1;
import org.vignanuniversity.vucounselling_app.StudentTabScreens.Internal.Module2;
import org.vignanuniversity.vucounselling_app.semester.sem_marks;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AttendanceMain extends AppCompatActivity implements OnScrollListener {
    public static String Title = "";
    int select = 0;
    LinearLayout linearLayout;

    static String regno = "", name = "", sphone = "", semail = "",
            pphone = "", pemail = "", pname = "", cyear = "", sem = "";

    // empcode is public static so AttendanceFragment can always read it
    public static String empcode = "";

    TextView nameText, regnoText;
    ImageView backButton, contactInfo, profile;
    LinearLayout linearLayout1;
    private LinearLayout marksContainer, homeContainer, feedbackContainer;
    private ImageView marksIcon, homeIcon, feedbackIcon;
    private TextView marksText, homeText, feedbackText;
    private Animation fadeIn, fadeOut, slideLeft, slideReset;
    private int currentSelectedIndex = 1;
    private View bottomNav;
    private boolean isBottomNavVisible = true;
    final Fragment fragment22 = new sem_marks();
    final Fragment fragment33 = new dynamicweeks();
    private FragmentManager fm;
    Fragment active = fragment22;
    Bundle bundle = new Bundle();

    private static final String TAG = "ATT_MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_tabs_main);
        bottomNav    = findViewById(R.id.bottom_menubar);
        linearLayout1 = findViewById(R.id.OpenTabLayout);

        regno = getIntent().getStringExtra("student_regno");
        name  = getIntent().getStringExtra("student_name");

        // 1. Try to get empcode from Intent
        String incomingEmpcode = getIntent().getStringExtra("empcode");
        if (incomingEmpcode != null && !incomingEmpcode.isEmpty()) {
            empcode = incomingEmpcode;
            Log.d(TAG, "empcode from Intent: [" + empcode + "]");
        }

        // 2. Fall back to SharedPreferences if still empty
        if (empcode == null || empcode.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("pref", Context.MODE_PRIVATE);
            empcode = prefs.getString("empcode", "");
            Log.d(TAG, "empcode from SharedPrefs: [" + empcode + "]");
        }

        Log.d(TAG, "onCreate: regno=[" + regno + "] empcode=[" + empcode + "]");

        All_DataFetcher.personalDetailsFetcher(this, regno, false, this::handlingPersonalDetails);
        setupNavigation();

        nameText    = findViewById(R.id.user_name);
        regnoText   = findViewById(R.id.user_code);
        backButton  = findViewById(R.id.back_press);
        contactInfo = findViewById(R.id.contact);
        profile     = findViewById(R.id.chari_profile);

        String imageUrl = URLs.grtStudentImageUrl(regno);
        Glide.with(this).load(imageUrl).into(profile);

        profile.setOnClickListener(v -> {
            View popupView = LayoutInflater.from(AttendanceMain.this)
                    .inflate(R.layout.profile_popup, null);
            PopupWindow popupWindow = new PopupWindow(popupView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, true);
            popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
            popupWindow.setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#80000000")));
            popupWindow.setOutsideTouchable(true);

            ImageView profileImageView = popupView.findViewById(R.id.profile_image_large);
            Button cancelButton = popupView.findViewById(R.id.btn_cancel);
            Glide.with(this).load(imageUrl).into(profileImageView);
            cancelButton.setOnClickListener(cv -> popupWindow.dismiss());
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            popupView.setAlpha(0f);
            popupView.animate().alpha(1f).setDuration(300)
                    .setInterpolator(new DecelerateInterpolator()).start();
        });

        nameText.setText(name);
        regnoText.setText(regno);
        backButton.setOnClickListener(v -> onBackPressed());
        contactInfo.setOnClickListener(v -> {
            Snackbar.make(v, "Contact Info", Snackbar.LENGTH_SHORT).show();
            DisplaycontactInfo();
        });

        linearLayout = findViewById(R.id.linearLayout);

        ViewPager2 viewPager2 = findViewById(R.id.OpenViewPager2);
        StudentMyViewPager adapter = new StudentMyViewPager(this, regno);
        adapter.addFragment(new AttendanceFragment(), "Attendance");
        adapter.addFragment(new Module1(), "Module - I Marks");
        adapter.addFragment(new Module2(), "Module - II Marks");
        viewPager2.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.OpenTabLayout2);
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText(adapter.getPageTitle(position))
        ).attach();
        tabLayout.getTabAt(0).select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                select = tab.getPosition();
                switch (select) {
                    case 0: Title = "Attendance"; break;
                    case 1: Title = "Module - I Marks"; break;
                    case 2: Title = "Module - II Marks"; break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {
                Title = tab.getText().toString();
            }
            @Override public void onTabReselected(TabLayout.Tab tab) {
                Title = tab.getText().toString();
            }
        });
    }

    @Override
    public void onScrollUp() {
        if (!isBottomNavVisible) {
            isBottomNavVisible = true;
            bottomNav.animate().translationY(0f).alpha(1f).setDuration(300)
                    .setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    @Override
    public void onScrollDown() {
        if (isBottomNavVisible) {
            isBottomNavVisible = false;
            bottomNav.animate()
                    .translationY(bottomNav.getHeight()).alpha(0f).setDuration(300)
                    .setInterpolator(new AccelerateInterpolator()).start();
        }
    }

    private void setupNavigation() {
        fm = getSupportFragmentManager();
        marksContainer    = findViewById(R.id.marks_container);
        homeContainer     = findViewById(R.id.home_container);
        feedbackContainer = findViewById(R.id.feedback_container);
        marksIcon    = findViewById(R.id.marks_icon);
        homeIcon     = findViewById(R.id.home_icon);
        feedbackIcon = findViewById(R.id.feedback_icon);
        marksText    = findViewById(R.id.marks_text);
        homeText     = findViewById(R.id.home_text);
        feedbackText = findViewById(R.id.feedback_text);

        fadeIn     = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut    = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        slideLeft  = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        slideReset = AnimationUtils.loadAnimation(this, R.anim.slide_reset);

        final LinearLayout[] containers = {marksContainer, homeContainer, feedbackContainer};
        final ImageView[]    icons      = {marksIcon, homeIcon, feedbackIcon};
        final TextView[]     texts      = {marksText, homeText, feedbackText};

        for (int i = 0; i < containers.length; i++) {
            final int index = i;
            containers[i].setOnClickListener(v -> {
                if (currentSelectedIndex == index) return;
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

                FragmentTransaction transaction =
                        getSupportFragmentManager().beginTransaction();
                if (index == 0) {
                    feedbackText.setVisibility(View.GONE);
                    fragment22.setArguments(bundle);
                    transaction.replace(R.id.fragment_container, fragment22);
                    active = fragment22;
                    linearLayout1.setVisibility(View.GONE);
                } else if (index == 1) {
                    transaction.remove(active);
                    feedbackText.setVisibility(View.GONE);
                    linearLayout1.setVisibility(View.VISIBLE);
                } else if (index == 2) {
                    linearLayout1.setVisibility(View.GONE);
                    feedbackText.setVisibility(View.VISIBLE);
                    fragment33.setArguments(bundle);
                    transaction.replace(R.id.fragment_container, fragment33);
                    active = fragment33;
                } else {
                    feedbackText.setVisibility(View.GONE);
                    linearLayout1.setVisibility(View.VISIBLE);
                }
                transaction.addToBackStack(null);
                transaction.commit();
            });
        }
        homeContainer.setSelected(true);
        homeIcon.startAnimation(slideLeft);
        homeText.setAlpha(1f);
    }

    public void handlingPersonalDetails(JSONObject response) {
        try {
            JSONArray jsonArray = response.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject hit = jsonArray.getJSONObject(i);
                sphone = hit.getString("studentmobile");
                semail = hit.getString("studentemailid").toLowerCase();
                pphone = hit.getString("fathermobile");
                pemail = hit.getString("parentemailid").toLowerCase();
                pname  = hit.getString("fathername");
                cyear  = hit.getString("cyear");
                sem    = hit.getString("semester");

                // empcode from personal details API
                if (hit.has("empcode") && !hit.optString("empcode").isEmpty()) {
                    empcode = hit.getString("empcode");
                    Log.d(TAG, "empcode from personalDetails API: [" + empcode + "]");
                }

                bundle.putString("student_regno", regno);
                bundle.putString("student_name",  name);
                bundle.putString("cyear",         cyear);
                bundle.putString("semester",      sem);
                bundle.putString("empcode",       empcode);

                // ── Save empcode to SharedPreferences so it's always available ──
                if (empcode != null && !empcode.isEmpty()) {
                    getSharedPreferences("pref", Context.MODE_PRIVATE)
                            .edit()
                            .putString("empcode", empcode)
                            .apply();
                    Log.d(TAG, "empcode saved to prefs: [" + empcode + "]");
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "handlingPersonalDetails error: " + e.toString());
        }
    }

    private void DisplaycontactInfo() {
        View popupView = LayoutInflater.from(AttendanceMain.this)
                .inflate(R.layout.contact_info, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupWindow.setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#80000000")));
        popupWindow.setOutsideTouchable(true);

        TextView studentPhone = popupView.findViewById(R.id.sphone);
        TextView studentEmail = popupView.findViewById(R.id.semail);
        TextView parentPhone  = popupView.findViewById(R.id.pphone);
        TextView parentEmail  = popupView.findViewById(R.id.pemail);
        TextView parentName   = popupView.findViewById(R.id.pname);
        TextView studentName  = popupView.findViewById(R.id.sname);

        studentName.setText(name);
        studentPhone.setText(sphone);
        studentEmail.setText(semail);
        parentPhone.setText(pphone);
        parentEmail.setText(pemail);
        parentName.setText(pname);

        studentPhone.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + sphone))));
        parentPhone.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + pphone))));

        ImageView cancelButton = popupView.findViewById(R.id.close);
        cancelButton.setOnClickListener(cv -> popupWindow.dismiss());
        popupWindow.showAtLocation(linearLayout, Gravity.CENTER, 0, 0);
        popupView.setAlpha(0f);
        popupView.animate().alpha(1f).setDuration(300)
                .setInterpolator(new DecelerateInterpolator()).start();
    }

    @Override
    public void onBackPressed() {
        if (linearLayout1.getVisibility() == View.VISIBLE) {
            linearLayout1.setVisibility(View.GONE);
            super.onBackPressed();
        } else {
            finish();
        }
    }
}