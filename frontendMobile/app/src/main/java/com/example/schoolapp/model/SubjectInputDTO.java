package com.example.schoolapp.model;

public class SubjectInputDTO {
    private String name;
    private Long levelId;

    public SubjectInputDTO(String name, Long levelId) {
        this.name = name;
        this.levelId = levelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

}