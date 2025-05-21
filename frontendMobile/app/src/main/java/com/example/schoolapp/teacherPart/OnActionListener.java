package com.example.schoolapp.teacherPart;

public interface OnActionListener {
    enum ActionType {
        DOWNLOAD,
        GRADE
    }

    void onAction(Long workReturnId, ActionType actionType);
}