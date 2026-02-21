package org.vignanuniversity.vucounselling_app.StudentTabScreens.Internal;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import org.vignanuniversity.vucounselling_app.R;

import java.util.ArrayList;

public class internal_item extends AppCompatActivity {
    static RequestQueue rq;
    String type;
    TextView need_to_hide_if_applied;
    LinearLayout showStatusIfAttempting;
    String key,name;
    TextView sub,module,t1,t21,t22,t31,t32,t4,t51,t52,t53,t54,t55;
    ArrayList<String> marksList;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_item);

        back = findViewById(R.id.on_back_press);
        back.setOnClickListener(v->{
            onBackPressed();
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            marksList = extras.getStringArrayList("marksList");
            name = extras.getString("internal Details");
            type = extras.getString("module Details");
        }
        init();

        module.setText(type);
        sub.setText(name);
//        if(type.equals("Module - I")){
        need_to_hide_if_applied.setVisibility(View.GONE);
        showStatusIfAttempting.setVisibility(View.VISIBLE);
//        }

    }

    public void init() {
        sub = findViewById(R.id.subjectName);
        module = findViewById(R.id.moduleType);
        t1 = findViewById(R.id.T1Data);
        t21 = findViewById(R.id.T2Data1);
        t22 = findViewById(R.id.T2Data2);
        t31 = findViewById(R.id.T3Data1);
        t32 = findViewById(R.id.T3Data2);
        t4 = findViewById(R.id.T4Data);
        t51 = findViewById(R.id.T5Data1);
        t52 = findViewById(R.id.T5Data2);
        t53 = findViewById(R.id.T5Data3);
        t54 = findViewById(R.id.T5Data4);
        showStatusIfAttempting = findViewById(R.id.hide_this_if_student_is_not_attempting);
        need_to_hide_if_applied = findViewById(R.id.not_eligible_text_view);

        if (marksList != null && marksList.size() >= 10) {
            t1.setText(marksList.get(0));
            t21.setText(marksList.get(1));
            t22.setText(marksList.get(2));
            t31.setText(marksList.get(3));
            t32.setText(marksList.get(4));
            t4.setText(marksList.get(5));
            t51.setText(marksList.get(6));
            t52.setText(marksList.get(7));
            t53.setText(marksList.get(8));
            t54.setText(marksList.get(9));
        }
    }

}