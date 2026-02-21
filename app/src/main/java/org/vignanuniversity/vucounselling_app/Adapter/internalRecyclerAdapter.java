package org.vignanuniversity.vucounselling_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.vignanuniversity.vucounselling_app.R;
import org.vignanuniversity.vucounselling_app.classFiles.internalData;

import java.util.List;

public class internalRecyclerAdapter extends RecyclerView.Adapter<internalRecyclerAdapter.interViewHolder>{

    Context context;
    List<internalData> iDataList;
    SubjectClick onSubjectClick;

    public internalRecyclerAdapter(Context context, List<internalData> iDataList,SubjectClick subjectClick) {
        this.context = context;
        this.iDataList = iDataList;
        this.onSubjectClick = subjectClick;
    }

    @NonNull
    @Override
    public interViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.internal_item,parent,false);
        interViewHolder studentViewHolder = new interViewHolder(v);
        return studentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull interViewHolder holder, int position) {
        internalData studentData = iDataList.get(position);
        holder.name.setText(studentData.getName());
        holder.itemView.setOnClickListener(view -> onSubjectClick.onClickListener(studentData));
    }

    @Override
    public int getItemCount() {
        return iDataList.size();
    }

    public static class interViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        CardView cardView;
        public interViewHolder(@NonNull View view) {
            super(view);
            cardView = view.findViewById(R.id.internal_card);
            name = view.findViewById(R.id.s_grade);
        }
    }

    public interface SubjectClick{
        void onClickListener(internalData internalData);
    }
}