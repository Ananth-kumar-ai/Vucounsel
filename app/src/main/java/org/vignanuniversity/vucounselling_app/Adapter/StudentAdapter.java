package org.vignanuniversity.vucounselling_app.Adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.studentClass;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> implements Filterable {

    private List<studentClass> fullList;
    private List<studentClass> filteredList;
    private StudentClick onStudentClick;
    private String searchQuery = "";

    public StudentAdapter(List<studentClass> studentList, StudentClick studentClick) {
        this.fullList = new ArrayList<>(studentList);
        this.filteredList = new ArrayList<>(studentList);
        this.onStudentClick = studentClick;
    }

    public static int getCountOfList() {
        return count;
    }

    private static int count = 0;

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_1, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        studentClass student = filteredList.get(position);

        holder.name.setText(getHighlightedText(student.getName()));
        holder.regno.setText(getHighlightedText(student.getRegno()));
        // -------- ATTENDANCE (SAFE PARSING) --------
        String attStr = student.getAttendance();

        if (attStr != null && !attStr.trim().isEmpty()) {
            try {
                float att = Float.parseFloat(attStr.trim());
                holder.att.setText(Math.round(att) + " %");
            } catch (NumberFormatException e) {
                holder.att.setText("N/A");
            }
        } else {
            holder.att.setText("N/A");
        }

// -------- CGPA / MARKS (SAFE DISPLAY) --------
        String marks = student.getMarks();
        holder.cgpa.setText(
                (marks != null && !marks.trim().isEmpty()) ? marks : "N/A"
        );


        String imageUrl = URLs.grtStudentImageUrl(student.getRegno());
        Log.d("imageurl", imageUrl);

        Glide.with(holder.studentImage.getContext())
                .load(imageUrl)
                .into(holder.studentImage);

        holder.layout.setOnClickListener(v -> onStudentClick.onStudentClick(student));
    }

    @Override
    public int getItemCount() {
        count = filteredList.size();
        return count;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                searchQuery = constraint != null ? constraint.toString().toLowerCase().trim() : "";
                List<studentClass> resultList = new ArrayList<>();

                if (searchQuery.isEmpty()) {
                    resultList.addAll(fullList);
                } else {
                    for (studentClass student : fullList) {
                        if (student.getName().toLowerCase().contains(searchQuery) ||
                                student.getRegno().toLowerCase().contains(searchQuery)) {
                            resultList.add(student);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = resultList;
                results.count = resultList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<studentClass>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    private SpannableString getHighlightedText(String originalText) {
        SpannableString spannable = new SpannableString(originalText);
        if (!searchQuery.isEmpty()) {
            String lowerText = originalText.toLowerCase();
            int startIndex = lowerText.indexOf(searchQuery);
            if (startIndex >= 0) {
                spannable.setSpan(
                        new ForegroundColorSpan(Color.BLUE),
                        startIndex,
                        startIndex + searchQuery.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
        return spannable;
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        ImageView studentImage;
        TextView name, regno, att, cgpa;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.studentCard);
            studentImage = itemView.findViewById(R.id.chari_profile);
            name = itemView.findViewById(R.id.text_name);
            regno = itemView.findViewById(R.id.reg_number);
            att = itemView.findViewById(R.id.Att_value);
            cgpa = itemView.findViewById(R.id.CgpaVal);
        }
    }
}
