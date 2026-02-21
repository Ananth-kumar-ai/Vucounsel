package org.vignanuniversity.vucounselling_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.Week;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.List;

public class WeekAdapter_dynamic extends RecyclerView.Adapter<WeekAdapter_dynamic.WeekViewHolder> {

    private List<Week> weeks;
    private Context context;

    public WeekAdapter_dynamic(Context context, List<Week> weeks) {
        this.context = context;
        this.weeks = weeks;
    }

    @NonNull
    @Override
    public WeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.week_report_card, parent, false);
        return new WeekViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekViewHolder holder, int position) {
        Week week = weeks.get(position);
        holder.weekName.setText(week.toString());

        if (week.getStatus() != null) {
            switch (week.getStatus()) {
                case "Present":
                    holder.toggleGroup.check(R.id.button1);
                    break;
                case "Absent":
                    holder.toggleGroup.check(R.id.button2);
                    break;
                case "Not Needed":
                    holder.toggleGroup.check(R.id.button3);
                    break;
                default:
                    break;
            }
        } else {
             holder.toggleGroup.clearChecked(); // or leave as is
        }
        holder.toggleGroup.clearOnButtonCheckedListeners();

        holder.toggleGroup.setEnabled(false);

//        holder.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
//            if (isChecked) {
//                String selectedStatus = "";
//                if (checkedId == R.id.button1) selectedStatus = "Present";
//                else if (checkedId == R.id.button2) selectedStatus = "Absent";
//                else if (checkedId == R.id.button3) selectedStatus = "Not Needed";
//
//                if (!selectedStatus.isEmpty()) {
//                    weeks.get(holder.getAdapterPosition()).setStatus(selectedStatus);
//                }
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return weeks.size();
    }

    public static class WeekViewHolder extends RecyclerView.ViewHolder {

        TextView weekName;
        MaterialButtonToggleGroup toggleGroup;

        public WeekViewHolder(@NonNull View itemView) {
            super(itemView);
            weekName = itemView.findViewById(R.id.week_name);
            toggleGroup = itemView.findViewById(R.id.toggleButton);
        }
    }
}
