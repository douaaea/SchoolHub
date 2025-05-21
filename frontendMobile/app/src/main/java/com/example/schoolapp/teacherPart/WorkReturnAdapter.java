package com.example.schoolapp.teacherPart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.schoolapp.R;
import com.example.schoolapp.model.WorkReturn;
import java.util.List;

public class WorkReturnAdapter extends BaseAdapter {
    private final Context context;
    private final List<WorkReturn> workReturnList;
    private final OnActionListener onActionListener;

    public WorkReturnAdapter(Context context, List<WorkReturn> workReturnList, OnActionListener listener) {
        this.context = context;
        this.workReturnList = workReturnList;
        this.onActionListener = listener;
    }

    @Override
    public int getCount() {
        return workReturnList.size();
    }

    @Override
    public Object getItem(int position) {
        return workReturnList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_work_return, parent, false);
            holder = new ViewHolder();
            holder.textStudentName = convertView.findViewById(R.id.textStudentName);
            holder.textAssignmentName = convertView.findViewById(R.id.textAssignmentName);
            holder.textSubjectName = convertView.findViewById(R.id.textSubjectName);
            holder.textGrade = convertView.findViewById(R.id.textGrade);
            holder.buttonDownload = convertView.findViewById(R.id.buttonDownload);
            holder.buttonGrade = convertView.findViewById(R.id.buttonGrade);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WorkReturn workReturn = workReturnList.get(position);
        holder.textStudentName.setText(workReturn.getStudentName());
        holder.textAssignmentName.setText(workReturn.getAssignmentName());
        holder.textSubjectName.setText(workReturn.getSubjectName());
        holder.textGrade.setText(workReturn.getGrade() != null ? workReturn.getGrade() : "Not Graded");

        holder.buttonDownload.setOnClickListener(v -> onActionListener.onAction(workReturn.getId(), OnActionListener.ActionType.DOWNLOAD));
        holder.buttonGrade.setOnClickListener(v -> onActionListener.onAction(workReturn.getId(), OnActionListener.ActionType.GRADE));

        return convertView;
    }

    private static class ViewHolder {
        TextView textStudentName;
        TextView textAssignmentName;
        TextView textSubjectName;
        TextView textGrade;
        Button buttonDownload;
        Button buttonGrade;
    }
}