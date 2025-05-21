package com.example.schoolapp.model;

public class StudentDTO {
    public Long id; // Added id field
    public String email;
    public String password;
    public String firstname;
    public String lastname;
    public Long groupId;
    public Long levelId;

    public StudentDTO(String email, String password, String firstname, String lastname, Long groupId, Long levelId) {
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.groupId = groupId;
        this.levelId = levelId;
    }

    // Add getters for the new id field
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}