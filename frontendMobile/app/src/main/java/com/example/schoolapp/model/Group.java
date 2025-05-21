package com.example.schoolapp.model;

import com.google.gson.annotations.SerializedName;

public class Group {
    private Long id;
    private String name;
    @SerializedName("level")
    private Level level;

    public Group() {}

    public Group(String name, Level level) {
        this.name = name;
        this.level = level;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }

    @Override
    public String toString() {
        return name != null ? name : "Unnamed Group";
    }

}