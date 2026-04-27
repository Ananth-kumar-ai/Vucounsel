package org.vignanuniversity.vucounselling_app.Screens;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.vignanuniversity.vucounselling_app.Adapter.URLs;
import org.vignanuniversity.vucounselling_app.R;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StudentReport extends AppCompatActivity {

    private static final String TAG = "StudentReport";
    // Target test endpoint mapped explicitly for the core API responses
    private static final String API_BASE = "http://160.187.169.24/counselling_jspapi/";

    private String regno = "";
    private String empcode = "";
    private String currentMod = "1";
    
    // Core Layout Elements
    private TextView headerRegno, chipM1, chipM2;
    private LinearLayout loadingLayout, contentLayout, emptyLayout;
    
    // Module Containers
    private LinearLayout counsellingContainer, attitudeContainer, traitsContainer;
    private TextView counsellingDateBadge, weekLabel, btnPrevWeek, btnNextWeek;
    
    private RequestQueue requestQueue;
    
    // Trackers for asynchronous API loading
    private int apisToLoad = 0;
    private int apisLoaded = 0;
    
    // Navigation mechanics for active counseling weeks
    private List<String> weeksList = new ArrayList<>();
    private int currentWeekIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_report);
        
        // 1. Recover Intent Data
        regno = getIntent().getStringExtra("student_regno");
        empcode = getIntent().getStringExtra("empcode");
        
        if (regno == null) regno = "";
        
        // Fallback: LoginActivity stores the employee code under the "regno" key in SharedPreferences
        if (empcode == null || empcode.isEmpty()) {
            android.content.SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE);
            empcode = prefs.getString("regno", ""); 
        }
        
        Log.d(TAG, "Initialized Student Report -> Reg: " + regno + " Emp: " + empcode);

        requestQueue = Volley.newRequestQueue(this);
        initViews();
        
        // Start full initialization
        fetchDynamicWeeks();
    }

    private void initViews() {
        headerRegno = findViewById(R.id.header_regno);
        chipM1 = findViewById(R.id.chip_m1);
        chipM2 = findViewById(R.id.chip_m2);
        
        loadingLayout = findViewById(R.id.loading_layout);
        contentLayout = findViewById(R.id.content_layout);
        emptyLayout = findViewById(R.id.empty_layout);
        
        counsellingContainer = findViewById(R.id.counselling_container);
        attitudeContainer = findViewById(R.id.attitude_container);
        traitsContainer = findViewById(R.id.traits_container);
        
        counsellingDateBadge = findViewById(R.id.counselling_date_badge);
        weekLabel = findViewById(R.id.week_label);
        btnPrevWeek = findViewById(R.id.btn_prev_week);
        btnNextWeek = findViewById(R.id.btn_next_week);
        
        findViewById(R.id.back_btn).setOnClickListener(v -> finish());
        headerRegno.setText("Reg No: " + regno);
        
        // Action Handlers
        chipM1.setOnClickListener(v -> switchModule("1"));
        chipM2.setOnClickListener(v -> switchModule("2"));
        btnPrevWeek.setOnClickListener(v -> navigateWeek(-1));
        btnNextWeek.setOnClickListener(v -> navigateWeek(1));   
    }
    
    private void setInitialLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
        apisToLoad = 3;
        apisLoaded = 0;
    }
    
    private void checkLoadingComplete() {
        apisLoaded++;
        if (apisLoaded >= apisToLoad) {
            loadingLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        }
    }
    
    // ----------- 1. FETCH WEEKS -----------
    private void fetchDynamicWeeks() {
        setInitialLoading();
        // Uses the standard server config out of URLs.java for the week structure fetcher
        String url = URLs.getDynamicWeeks(regno);
        Log.d(TAG, "Fetching dynamic weeks: " + url);
        
        StringRequest req = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject res = new JSONObject(response.trim());
                JSONArray dyWeeksArr = res.optJSONArray("DynamicWeeks");
                JSONArray cwArr = res.optJSONArray("CurrentWeek");
                
                weeksList.clear();
                currentWeekIndex = -1;
                String currentWeekStr = "";

                if (cwArr != null && cwArr.length() > 0) {
                    currentWeekStr = cwArr.getJSONObject(0).optString("CurrentWeek", "");
                }

                if (dyWeeksArr != null && dyWeeksArr.length() > 0) {
                    JSONObject weeksObj = dyWeeksArr.getJSONObject(0);
                    // The keys are numeric strings: "0", "1", "2", ...
                    for (int i = 0; i < weeksObj.length(); i++) {
                        String weekName = weeksObj.optString(String.valueOf(i), "");
                        if (!weekName.isEmpty()) {
                            weeksList.add(weekName);
                            if (weekName.equals(currentWeekStr)) {
                                currentWeekIndex = weeksList.size() - 1;
                            }
                        }
                    }
                }
                
                if (currentWeekIndex == -1 && !weeksList.isEmpty()) {
                    currentWeekIndex = weeksList.size() - 1;
                } else if (currentWeekIndex == -1 && !currentWeekStr.isEmpty()) {
                    weeksList.add(currentWeekStr);
                    currentWeekIndex = 0;
                }
                
                // Switch module automatically trigger API calls 1, 2, 3
                switchModule("1");
                
            } catch (Exception e) {
                Log.e(TAG, "Weeks parse error: " + e.getMessage());
                switchModule("1"); // load remaining layout anyways
            }
        }, error -> {
            Log.e(TAG, "Weeks error: " + error.toString());
            switchModule("1"); // load remaining layout anyways
        });
        
        requestQueue.add(req);
    }
    
    private void navigateWeek(int direction) {
        if (weeksList.isEmpty()) return;
        
        int newIndex = currentWeekIndex + direction;
        if (newIndex >= 0 && newIndex < weeksList.size()) {
            currentWeekIndex = newIndex;
            String targetWeek = weeksList.get(currentWeekIndex);
            
            // Re-fetch only the counselling segment smoothly
            setInitialLoading();
            apisToLoad = 1; 
            apisLoaded = 0;
            fetchCounselling(targetWeek);
        }
    }

    private void switchModule(String mod) {
        currentMod = mod;
        
        // Chip Aesthetic Toggles
        if (mod.equals("1")) {
            chipM1.setBackgroundResource(R.drawable.chip_selected);
            chipM1.setTextColor(Color.WHITE);
            chipM2.setBackgroundResource(R.drawable.chip_unselected);
            chipM2.setTextColor(Color.parseColor("#B3B9F2"));
        } else {
            chipM2.setBackgroundResource(R.drawable.chip_selected);
            chipM2.setTextColor(Color.WHITE);
            chipM1.setBackgroundResource(R.drawable.chip_unselected);
            chipM1.setTextColor(Color.parseColor("#B3B9F2"));
        }
        
        counsellingContainer.removeAllViews();
        attitudeContainer.removeAllViews();
        traitsContainer.removeAllViews();
        
        setInitialLoading();
        apisToLoad = 3;
        apisLoaded = 0;
        
        String weekToLoad = "";
        if (currentWeekIndex >= 0 && currentWeekIndex < weeksList.size()) {
            weekToLoad = weeksList.get(currentWeekIndex);
        }
        
        fetchCounselling(weekToLoad);
        fetchAttitude(mod);
        fetchTraits(mod);
    }
    
    // ----------- 2. FETCH COUNSELLING -----------
    private void fetchCounselling(String weekStr) {
        weekLabel.setText(weekStr.isEmpty() ? "No Week Selected" : weekStr);
        counsellingDateBadge.setText("—");
        counsellingContainer.removeAllViews();
        
        if (weekStr.isEmpty()) {
            addEmptyMessage(counsellingContainer, "No active week to load info.");
            checkLoadingComplete();
            return;
        }
        
        // Ensure bracket alignment against JSP backend format natively
        String safeWeek = weekStr;
        if (safeWeek.contains("_")) {
            StringBuilder sb = new StringBuilder(safeWeek);
            int stInd   = safeWeek.indexOf("_");
            int lastInd = safeWeek.lastIndexOf("_");
            if (stInd != lastInd && stInd != -1 && lastInd != -1) {
                sb.setCharAt(stInd, '[');
                sb.setCharAt(lastInd, ']');
                safeWeek = sb.toString();
            }
        }
        
        String url = API_BASE + "fetchcounselling.jsp?registerno=" + encode(regno) + 
                     "&empcode=" + encode(empcode) + "&weekname=" + encode(safeWeek);
        
        Log.d(TAG, "Fetching Counselling: " + url);
        
        StringRequest req = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject res = new JSONObject(response.trim());
                if (!res.optString("status").equals("success")) {
                    addEmptyMessage(counsellingContainer, "No counseling data available for this week.");
                    checkLoadingComplete();
                    return;
                }
                
                JSONObject data = res.getJSONObject("data");
                
                String date = data.optString("counselling_date", "");
                if (!date.isEmpty()) counsellingDateBadge.setText(date);
                
                int count = 0;
                Iterator<String> keys = data.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = data.optString(key, "");
                    
                    // Filters fields logically so display remains clean
                    if (isMetaKey(key) || value.isEmpty() || value.equals("null")) continue;
                    
                    addKeyValueRow(counsellingContainer, formatKey(key), value);
                    count++;
                }
                
                if (count == 0) addEmptyMessage(counsellingContainer, "Data is empty or not formatted correctly.");
                
            } catch (Exception e) {
                Log.e(TAG, "Counselling Exception: " + e.getMessage());
                addEmptyMessage(counsellingContainer, "Error tracking DB logic parameters.");
            }
            checkLoadingComplete();
        }, error -> {
            Log.e(TAG, "Counselling Net Error: " + error.toString());
            addEmptyMessage(counsellingContainer, "Connection to Counseling Server lost.");
            checkLoadingComplete();
        });
        
        requestQueue.add(req);
    }
    
    // ----------- 3. FETCH ATTITUDE -----------
    private void fetchAttitude(String mod) {
        attitudeContainer.removeAllViews();
        String mId = mod.equals("1") ? "Module 1" : "Module 2";
        
        String url = API_BASE + "checkattitude.jsp?registerno=" + encode(regno) + 
                     "&empcode=" + encode(empcode) + "&mid=" + encode(mId);
                     
        Log.d(TAG, "Fetching Attitude: " + url);
        
        StringRequest req = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject res = new JSONObject(response.trim());
                if (!res.optString("status").equals("found")) {
                    addEmptyMessage(attitudeContainer, "No attitude tracking parameters located for " + mId + ".");
                    checkLoadingComplete();
                    return;
                }
                
                int count = 0;
                int index = 1;
                for (int i = 1; i <= 9; i++) {
                    String val = res.optString("attitude" + i, "");
                    if (!val.isEmpty() && !val.equals("null")) {
                        addAttitudeRow(attitudeContainer, index++, val);
                        count++;
                    }
                }
                
                if (count == 0) addEmptyMessage(attitudeContainer, "All attitude parameters returned null.");
                
            } catch (Exception e) {
                Log.e(TAG, "Attitude Parser Ex: " + e.getMessage());
                addEmptyMessage(attitudeContainer, "Error syncing attitude DB.");
            }
            checkLoadingComplete();
        }, error -> {
            Log.e(TAG, "Attitude Volley Ex: " + error.toString());
            addEmptyMessage(attitudeContainer, "Network timeout accessing attitude module.");
            checkLoadingComplete();
        });
        
        requestQueue.add(req);
    }
    
    // ----------- 4. FETCH TRAITS -----------
    private void fetchTraits(String mod) {
        traitsContainer.removeAllViews();
        String mId = mod.equals("1") ? "m1" : "m2";
        
        String url = API_BASE + "fetchtraits.jsp?registerno=" + encode(regno) + 
                     "&empcode=" + encode(empcode) + "&mid=" + encode(mId);
                     
        Log.d(TAG, "Fetching Traits: " + url);
        
        StringRequest req = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject res = new JSONObject(response.trim());
                if (!res.optString("status").equals("success")) {
                    addEmptyMessage(traitsContainer, "Traits module returned empty status for " + mId + ".");
                    checkLoadingComplete();
                    return;
                }
                
                JSONObject data = res.getJSONObject("data");
                int count = 0;
                
                for (int i = 1; i <= 29; i++) {
                    if (data.has("trait" + i)) {
                        int score = data.optInt("trait" + i, 0);
                        String comment = data.optString("trait" + i + "_comment", "");
                        
                        // Omit completely empty rows 
                        if (score != 0 || (!comment.isEmpty() && !comment.equals("null"))) {
                            addTraitRow(traitsContainer, i, score, comment);
                            count++;
                        }
                    }
                }
                
                if (count == 0) addEmptyMessage(traitsContainer, "No scored trait data present.");
                
            } catch (Exception e) {
                Log.e(TAG, "Traits Exception: " + e.getMessage());
                addEmptyMessage(traitsContainer, "Exception evaluating Traits format.");
            }
            checkLoadingComplete();
        }, error -> {
            Log.e(TAG, "Traits Net Error: " + error.toString());
            addEmptyMessage(traitsContainer, "Disconnected while accessing Trait framework.");
            checkLoadingComplete();
        });
        
        requestQueue.add(req);
    }
    
    // ----------- COMPONENT BUILDERS FOR DYNAMIC VIEWS -----------
    
    private void addKeyValueRow(LinearLayout parent, String key, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.bottomMargin = dp(10);
        row.setLayoutParams(param);
        
        TextView tKey = new TextView(this);
        tKey.setText(key);
        tKey.setTextColor(Color.parseColor("#455A64"));
        tKey.setTextSize(13f);
        tKey.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.3f));
        
        TextView tVal = new TextView(this);
        tVal.setText(value);
        tVal.setTextColor(getTextColorForValue(value));
        tVal.setTextSize(14f);
        // tVal.setTypeface(null, android.graphics.Typeface.BOLD);
        tVal.setGravity(Gravity.END);
        tVal.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        
        row.addView(tKey);
        row.addView(tVal);
        parent.addView(row);
        
        addDivider(parent);
    }
    
    private void addAttitudeRow(LinearLayout parent, int index, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.TOP);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.bottomMargin = dp(12);
        row.setLayoutParams(param);
        
        TextView idx = new TextView(this);
        idx.setText(String.valueOf(index));
        idx.setTextColor(Color.WHITE);
        idx.setTextSize(12f);
        idx.setGravity(Gravity.CENTER);
        
        // Circular index styling 
        android.graphics.drawable.GradientDrawable bgCircle = new android.graphics.drawable.GradientDrawable();
        bgCircle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        bgCircle.setColor(Color.parseColor("#D84315"));
        idx.setBackground(bgCircle);
        
        LinearLayout.LayoutParams idxP = new LinearLayout.LayoutParams(dp(22), dp(22));
        idxP.setMargins(0, dp(4), dp(12), 0);
        idx.setLayoutParams(idxP);
        
        TextView tVal = new TextView(this);
        tVal.setText(value);
        tVal.setTextColor(Color.parseColor("#37474F"));
        tVal.setTextSize(14.5f);
        tVal.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        
        row.addView(idx);
        row.addView(tVal);
        parent.addView(row);
        addDivider(parent);
    }
    
    private void addTraitRow(LinearLayout parent, int index, int score, String comment) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.bottomMargin = dp(12);
        row.setLayoutParams(param);
        
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView lbl = new TextView(this);
        lbl.setText("Behavioral Trait " + index);
        lbl.setTextColor(Color.parseColor("#455A64"));
        lbl.setTextSize(14f);
        lbl.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        
        TextView scr = new TextView(this);
        scr.setText(score + " / 5");
        scr.setTextColor(Color.WHITE);
        scr.setTextSize(12f);
        scr.setTypeface(null, android.graphics.Typeface.BOLD);
        scr.setPadding(dp(12), dp(4), dp(12), dp(4));
        
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        bg.setCornerRadius(dp(12));
        bg.setColor(getScoreColor(score));
        scr.setBackground(bg);
        
        top.addView(lbl);
        top.addView(scr);
        row.addView(top);
        
        if (comment != null && !comment.trim().isEmpty() && !comment.equals("null")) {
            TextView cmt = new TextView(this);
            cmt.setText("Note: " + comment);
            cmt.setTextColor(Color.parseColor("#00838F"));
            cmt.setTextSize(13f);
            cmt.setPadding(0, dp(6), 0, 0);
            row.addView(cmt);
        }
        
        parent.addView(row);
        addDivider(parent);
    }
    
    private void addEmptyMessage(LinearLayout parent, String msg) {
        TextView tv = new TextView(this);
        tv.setText(msg);
        tv.setTextColor(Color.parseColor("#90A4AE"));
        tv.setTextSize(14f);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(0, dp(16), 0, dp(16));
        parent.addView(tv);
    }
    
    private void addDivider(LinearLayout parent) {
        View v = new View(this);
        v.setBackgroundColor(Color.parseColor("#ECEFF1"));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        p.bottomMargin = dp(8);
        v.setLayoutParams(p);
        parent.addView(v);
    }

    // ----------- UTILITIES -----------
    private String encode(String s) {
        if (s == null) return "";
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s.replace(" ", "%20");
        }
    }
    
    private int dp(int px) {
        return Math.round(px * getResources().getDisplayMetrics().density);
    }
    
    private String formatKey(String raw) {
        String s = raw.replace("_", " ").replace("-", " ");
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (w.isEmpty()) continue;
            sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase()).append(" ");
        }
        return sb.toString().trim();
    }
    
    private int getTextColorForValue(String value) {
        String lower = value.toLowerCase();
        if (lower.equals("yes") || lower.equals("present") || lower.contains("high")) {
            return Color.parseColor("#2E7D32");  // Dark Green
        } else if (lower.equals("no") || lower.equals("absent") || lower.contains("poor")) {
            return Color.parseColor("#C62828");  // Red
        } else if (lower.contains("personal") || lower.contains("counselling")) {
            return Color.parseColor("#1565C0"); // Deep blue
        }
        return Color.parseColor("#000000");     // Default Neutral
    }
    
    private int getScoreColor(int score) {
        if (score >= 4) return Color.parseColor("#1B5E20"); // Excellent (Green)
        if (score == 3) return Color.parseColor("#F57F17"); // Fair (Orange)
        if (score == 0) return Color.parseColor("#B0BEC5"); // Neutral (Grey)
        return Color.parseColor("#B71C1C");                 // Poor (Red)
    }
    
    private boolean isMetaKey(String key) {
        String l = key.toLowerCase();
        return l.equals("status") || l.equals("message") || l.equals("updated_on") || 
               l.equals("empcode") || l.equals("registerno") || l.equals("week_details") ||
               l.equals("given_from") || l.equals("mid") || l.startsWith("debug_") || 
               l.equals("counselling_date"); // date handled separately
    }
}