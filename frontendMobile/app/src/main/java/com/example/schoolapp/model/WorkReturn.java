package com.example.schoolapp.model;

import com.google.gson.annotations.SerializedName;

public class WorkReturn {
    @SerializedName("id")
    private Long id;

    @SerializedName("student")
    private Student student;

    @SerializedName("assignment")
    private Assignment assignment;

    @SerializedName("fileUrl")
    private String fileUrl; // URL or path to the submitted file

    @SerializedName("grade")
    private String grade; // Or Integer, depending on your backend

    private String studentName; // Enriched field
    private String assignmentName; // Enriched field
    private String subjectName; // Enriched field

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getAssignmentName() { return assignmentName; }
    public void setAssignmentName(String assignmentName) { this.assignmentName = assignmentName; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getFilePath() {
        // Assuming fileUrl is a full URL or path, and you want to extract the file path
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        // If fileUrl is a URL and you want to extract just the file path part
        // Example: from "http://example.com/uploads/homework.pdf" → "/uploads/homework.pdf"
        try {
            java.net.URL url = new java.net.URL(fileUrl);
            return url.getPath();
        } catch (Exception e) {
            // If it's not a valid URL, return it as-is (assuming it's a file path already)
            return fileUrl;
        }
    }

}