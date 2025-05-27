package com.example.schoolapp.model;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import java.io.Serializable;

public class AssignmentDTO implements Serializable {
    private Long id;
    private String title;
    private String description;
    @SerializedName("delay")
    @JsonAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime delay;
    // For Serializable (used by Intent.putExtra)
    private transient LocalDateTime delayTransient; // Renamed to avoid conflict
    private String delayString;
    private String status;
    private Long subjectId;
    private Long groupId;
    private Long programId;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDelay() {
        // For Serializable: Reconstruct delay from delayString if needed
        if (delay == null && delayString != null) {
            delayTransient = LocalDateTime.parse(delayString, FORMATTER);
            delay = delayTransient;
        }
        return delay;
    }

    public void setDelay(LocalDateTime delay) {
        this.delay = delay;
        this.delayTransient = delay;
        this.delayString = (delay != null) ? delay.format(FORMATTER) : null;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public Long getProgramId() { return programId; }
    public void setProgramId(Long programId) { this.programId = programId; }

    @Override
    public String toString() {
        return "AssignmentDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", delay=" + getDelay() +
                ", status='" + status + '\'' +
                ", subjectId=" + subjectId +
                ", groupId=" + groupId +
                ", programId=" + programId +
                '}';
    }
}