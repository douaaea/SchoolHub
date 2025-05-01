package com.example.demo.Assignment;

import com.example.demo.Subject.Subject;
import com.example.demo.Group.Group;
import com.example.demo.Program.Program;
import com.example.demo.WorkReturn.WorkReturn;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String description;

    private LocalDateTime delay;

    // Many assignments belong to one subject
    @ManyToOne
    @JoinColumn(name = "subject_id")
    @JsonIgnoreProperties({"assignments", "teachers", "level", "grades"})
    private Subject subject;

    // One assignment can have many work returns
    @OneToMany(mappedBy = "assignment")
    @JsonIgnoreProperties("assignment")
    private List<WorkReturn> workReturns;

    // Many assignments can be given to one group
    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonIgnoreProperties({"assignments", "students", "teachers", "level"})
    private Group group;

    @ManyToOne
    private Program program;

    public Assignment() {}

    public Assignment(String title, String description, LocalDateTime delay, Subject subject) {
        this.title = title;
        this.description = description;
        this.delay = delay;
        this.subject = subject;
    }

    public Assignment(String title, String description, Group group, Subject subject) {
        this.title = title;
        this.description = description;
        this.group = group;
        this.subject = subject;
    }
    public Assignment(String title, String description, Program program) {
        this.title = title;
        this.description = description;
        this.program = program;
        
    }
    

    // Getters and setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id; 
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDelay() {
        return delay;
    }
    public void setDelay(LocalDateTime delay) {
        this.delay = delay;
    }

    public Subject getSubject() {
        return subject;
    }
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public List<WorkReturn> getWorkReturns() {
        return workReturns;
    }
    public void setWorkReturns(List<WorkReturn> workReturns) {
        this.workReturns = workReturns;
    }

    public Group getGroup() {
        return group;
    }
    public void setGroup(Group group) {
        this.group = group;
    }
}
