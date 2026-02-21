package org.vignanuniversity.vucounselling_app.DataFetcher;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.vignanuniversity.vucounselling_app.Adapter.URLs;

import org.json.JSONException;
import org.json.JSONObject;

public class All_DataFetcher {
    public interface DataCallback {
        void onDataLoaded(JSONObject data) throws JSONException;
    }
    public static void personalDetailsFetcher(Context context, String regno, boolean forceRefresh, DataCallback callback) {
        fetchData(context, URLs.getStudentInfo(regno), "", "fetchPersonalDetails", forceRefresh, callback);
    }

    public static void attendanceDataFetcher(Context context, String regno, boolean forceRefresh, DataCallback callback) {
        fetchData(context, URLs.getMainAttendanceUrl(regno), "", "fetchAttendanceData", forceRefresh, callback);
    }

    public static void internalMarksFetcher(Context context, String regno, boolean forceRefresh, DataCallback callback) {
        fetchData(context, URLs.getInternalMarksUrl(regno), "", "fetchInternalMarks", forceRefresh, callback);
    }
    public static void cgpaFetcher(Context context, String regno, boolean forceRefresh, DataCallback callback) {
        fetchData(context, URLs.getCGPA(regno), "", "fetchCGPA", forceRefresh, callback);
    }
    public static void semesterMarksFetcher(Context context, String regno, int year, int sem, boolean forceRefresh, DataCallback callback) {
        fetchData(context, URLs.getSemesterMarks(regno, year, sem), "", "fetchSemesterMarks", forceRefresh, callback);
    }
    public static void dynamicWeeksFetcher(Context context, String regno, boolean forceRefresh, DataCallback callback) {
        fetchData(context, URLs.getDynamicWeeks(regno), "", "fetchDynamicWeeks", forceRefresh, callback);
    }
    private static void fetchData(Context context, String url, String cacheKey, String functionName, boolean forceRefresh, DataCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        callback.onDataLoaded(response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> handleErrorResponse(error, functionName, context));
        Volley.newRequestQueue(context).add(request);
    }

    private static void handleErrorResponse(VolleyError error, String functionName, Context context) {
        Log.e("Network Error", error.toString());
        Toast.makeText(context, "Network error in " + functionName + ". Please try again.", Toast.LENGTH_SHORT).show();
    }
}
