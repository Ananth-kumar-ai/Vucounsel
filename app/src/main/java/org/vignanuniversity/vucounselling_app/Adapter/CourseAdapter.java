package org.vignanuniversity.vucounselling_app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.Course;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courses;

    public CourseAdapter(List<Course> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_att_cardnew, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.textViewCourseCode.setText(course.getCode());
        holder.textViewCourseName.setText(course.getName());
        holder.textViewAttendance.setText(String.format("Classes: %d / %d", course.getAttendedClasses(), course.getTotalClasses()));
        int attendancePercentage = Math.round(course.getAttendancePercentage());
        holder.textViewPercentage.setText(String.format("%d%%", attendancePercentage));
        holder.progressBarAttendance.setProgress(attendancePercentage);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCourseCode, textViewCourseName, textViewSection, textViewAttendance, textViewPercentage;
        CircularProgressIndicator progressBarAttendance;
        CourseViewHolder(View itemView) {
            super(itemView);
            textViewCourseCode = itemView.findViewById(R.id.textViewCourseCode);
            textViewCourseName = itemView.findViewById(R.id.textViewCourseName);
            textViewAttendance = itemView.findViewById(R.id.textViewAttendance);
            textViewPercentage = itemView.findViewById(R.id.textViewPercentage);
            progressBarAttendance = itemView.findViewById(R.id.progressBarAttendance);
        }
    }
}