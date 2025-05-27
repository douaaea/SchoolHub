package com.example.schoolapp.model;

import com.google.gson.annotations.SerializedName;

public class Level {
    private Long id;
    private String name;

    public Level() {}

    public Level(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Level{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}