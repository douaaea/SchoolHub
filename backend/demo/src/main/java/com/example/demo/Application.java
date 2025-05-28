package com.example.demo;

import com.example.demo.Level.Level;
import com.example.demo.Subject.Subject;
import com.example.demo.Teacher.Teacher;
import com.example.demo.Admin.*;
import com.example.demo.Assignment.*;
import com.example.demo.Admin.AdminRepository;
import com.example.demo.Assignment.AssignmentRepository;
import com.example.demo.Grade.Grade;
import com.example.demo.Grade.GradeRepository;
import com.example.demo.Group.Group;
import com.example.demo.Program.Program;
import com.example.demo.Program.ProgramRepository;
import com.example.demo.Student.Student;
import com.example.demo.Student.StudentRepository;
import com.example.demo.Level.LevelRepository;
import com.example.demo.Subject.SubjectRepository;
import com.example.demo.Teacher.TeacherRepository;
import com.example.demo.WorkReturn.WorkReturn;
import com.example.demo.WorkReturn.WorkReturnRepository;
import com.example.demo.Group.GroupRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EntityScan(basePackages = "com.example.demo")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Profile("!test") // Ne s'exécute pas en mode test
    CommandLineRunner testEverything(LevelRepository levelRepo,
                                     SubjectRepository subjectRepo,
                                     TeacherRepository teacherRepo,
                                     GroupRepository groupRepo,
                                     ProgramRepository programRepo,
                                     AdminRepository adminRepo,
                                     StudentRepository studentRepo,
                                     AssignmentRepository assignmentRepo,
                                     GradeRepository gradeRepo,
                                     WorkReturnRepository workReturnRepo) {
        return args -> {
            Level level = new Level("First Level");
            levelRepo.save(level);

            Subject math = new Subject("Math", level);
            subjectRepo.save(math);

            Teacher teacher = new Teacher("teacher1@example.com", "pass123", "John", "Doe");
            teacherRepo.save(teacher);

            Group group = new Group("Group A", level);
            groupRepo.save(group);

            Program program = new Program(teacher, group, math);
            programRepo.save(program);

            Admin admin = new Admin("admin@example.com", "adminpass", "Alice","Chihab", "Adminson");
            adminRepo.save(admin);

            Student student = new Student("student@example.com", "studpass", "Jane", "Smith", level, group);
            studentRepo.save(student);

            Assignment assignment = new Assignment("Assignment 1", "Solve math problems", program);
            assignmentRepo.save(assignment);

            Grade grade = new Grade(95.0, student, assignment);
            gradeRepo.save(grade);

            System.out.println("✅ All entities created and saved successfully!");
        };
    }
}
