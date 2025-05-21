package com.example.schoolapp.model;

public class Subject {
    private Long id;
    private String name;
    private Level level;

    public Subject() {}

    public Subject(String name) {
        this.name = name;
    }

    public Subject(String name, Level level) {
        this.name = name;
        this.level = level;
    }

    public Subject(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Subject(Long id, String name, Level level) {
        this.id = id;
        this.name = name;
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}