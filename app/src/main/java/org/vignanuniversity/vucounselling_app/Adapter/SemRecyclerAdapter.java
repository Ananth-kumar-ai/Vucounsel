package org.vignanuniversity.vucounselling_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.semData;

import java.util.List;

public class SemRecyclerAdapter extends RecyclerView.Adapter<SemRecyclerAdapter.SemViewHolder>{
    Context context;
    List<semData> studentDataList;

    public SemRecyclerAdapter(Context context, List<semData> studentDataList) {
        this.context = context;
        this.studentDataList = studentDataList;
    }

    @NonNull
    @Override
    public SemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.marks_item,parent,false);
        SemViewHolder studentViewHolder = new SemViewHolder(v);
        return studentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SemViewHolder holder, int position) {
        semData studentData = studentDataList.get(position);

        holder.grade.setText(""+studentData.getGrade());
        holder.points.setText(studentData.getPoints());
        holder.code.setText(studentData.getCode());
        if(studentData.getName().length()>10){
            holder.name.setTextSize(10);
        }
        holder.name.setText(studentData.getName());
        holder.credits.setText(studentData.getCredits());

        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.semester_scrolling_animation));
    }

    @Override
    public int getItemCount() {
        return studentDataList.size();
    }

    public static class SemViewHolder extends RecyclerView.ViewHolder{

        TextView grade,code,name,credits,points;
        CardView cardView;
        public SemViewHolder(@NonNull View view) {
            super(view);
            cardView = view.findViewById(R.id.sem_card);
            grade = view.findViewById(R.id.s_grade);
            code = view.findViewById(R.id.s_code);
            name = view.findViewById(R.id.s_name);
            credits = view.findViewById(R.id.s_credit);
            points = view.findViewById(R.id.s_points);
        }
    }
}
