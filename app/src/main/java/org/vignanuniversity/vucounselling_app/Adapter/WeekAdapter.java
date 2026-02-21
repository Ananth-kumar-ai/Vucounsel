package org.vignanuniversity.vucounselling_app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.weekClass;

import java.util.ArrayList;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.WeekViewHolder> {

    ArrayList<weekClass> weekClassArrayList;

    public WeekAdapter(ArrayList<weekClass> weekClassArrayList) {
        this.weekClassArrayList = weekClassArrayList;
    }

    @NonNull
    @Override
    public WeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.week_report_card, parent, false);
        return new WeekViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekViewHolder holder, int position) {
        weekClass weekClass = weekClassArrayList.get(position);
        holder.textView.setText(weekClass.getName());

    }

    @Override
    public int getItemCount() {
        return weekClassArrayList.size();
    }

    public class WeekViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public WeekViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.week_name);

        }
    }
}
