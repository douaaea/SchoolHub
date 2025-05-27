package com.example.schoolapp.model;

import com.google.gson.annotations.SerializedName;

public class Subject {
    private Long id;
    private String name;

    @SerializedName("levelId") // Match the backend field name
    private Long levelId; // Change from Level object to Long

    // Transient field to store Level object after fetching
    private transient Level level;

    public Subject() {}

    public Subject(String name) {
        this.name = name;
    }

    public Subject(String name, Long levelId) {
        this.name = name;
        this.levelId = levelId;
    }

    public Subject(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Subject(Long id, String name, Long levelId) {
        this.id = id;
        this.name = name;
        this.levelId = levelId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getLevelId() { return levelId; }
    public void setLevelId(Long levelId) { this.levelId = levelId; }
    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }
}