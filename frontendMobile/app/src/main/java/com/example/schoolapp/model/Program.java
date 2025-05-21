package com.example.schoolapp.model;

import com.google.gson.annotations.SerializedName;

public class Program {
    private Long id;

    @SerializedName("teacher")
    private Teacher teacher;

    @SerializedName("group")
    private Group group;

    @SerializedName("subject")
    private Subject subject;

    public Program() {}

    public Program(Teacher teacher, Group group, Subject subject) {
        this.teacher = teacher;
        this.group = group;
        this.subject = subject;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

}