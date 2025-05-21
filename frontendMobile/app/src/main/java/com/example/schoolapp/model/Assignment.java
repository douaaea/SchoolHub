package com.example.schoolapp.model;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class Assignment {
    private Long id;
    private String title;
    private String description;
    private String delay; // LocalDateTime mapped to String (ISO format)
    private String status;
    private Subject subject;
    private Group group;
    private Program program;

    // Constructor for creating new assignment
    public Assignment(String title, String description, String delay, Subject subject) {
        this.title = title;
        this.description = description;
        this.delay = delay;
        this.subject = subject;
        this.status = "Not Started";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDelay() {
        return delay != null ? LocalDateTime.parse(delay) : null;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Long getSubjectId() {
        return subject != null ? subject.getId() : null;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime delayDateTime = getDelay();
        String delayStr = delayDateTime != null ? delayDateTime.format(formatter) : "No Due Date";
        return title != null ? title + " (Due: " + delayStr + ")" : "Untitled Assignment (No Due Date)";
    }
}