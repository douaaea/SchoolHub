package com.example.schoolapp.model;

public class GroupInputDTO {
    public String name;
    public Long levelId;

    public GroupInputDTO(String name, Long levelId) {
        this.name = name;
        this.levelId = levelId;
    }
}