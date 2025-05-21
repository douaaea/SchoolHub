package com.example.schoolapp.model;

public class GradeDTO {
    private Double score;
    private Long studentId;
    private Long subjectId;
    private Long assignmentId;
    private String assignmentName; // Added field for assignment name

    public GradeDTO() {}

    public GradeDTO(Double score, Long studentId, Long subjectId, Long assignmentId, String assignmentName) {
        this.score = score;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.assignmentId = assignmentId;
        this.assignmentName = assignmentName;
    }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }
    public String getAssignmentName() { return assignmentName; }
    public void setAssignmentName(String assignmentName) { this.assignmentName = assignmentName; }

    @Override
    public String toString() {
        return "Assignment: " + (assignmentName != null ? assignmentName : "ID " + (assignmentId != null ? assignmentId : "N/A")) +
                ", Score: " + (score != null ? score : "N/A");
    }
}