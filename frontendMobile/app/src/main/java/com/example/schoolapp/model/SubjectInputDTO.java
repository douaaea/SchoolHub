package com.example.schoolapp.model;

import com.google.gson.annotations.SerializedName;

public class SubjectInputDTO {
    @SerializedName("name")
    private String name;

    @SerializedName("levelId")
    private Long levelId;

    public SubjectInputDTO() {}

    public SubjectInputDTO(String name, Long levelId) {
        this.name = name;
        this.levelId = levelId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getLevelId() { return levelId; }
    public void setLevelId(Long levelId) { this.levelId = levelId; }
}