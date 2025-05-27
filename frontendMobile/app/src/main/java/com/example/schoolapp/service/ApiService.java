package com.example.schoolapp.service;

import com.example.schoolapp.model.*;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

public interface ApiService {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // Teachers
    @GET("/api/teachers")
    Call<List<Teacher>> getTeachers();

    @GET("/api/teachers/{id}")
    Call<Teacher> getTeacher(@Path("id") Long id);

    @POST("/api/teachers")
    Call<Teacher> addTeacher(@Body Teacher teacher);

    @PUT("/api/teachers/{id}")
    Call<Teacher> updateTeacher(@Path("id") Long id, @Body Teacher teacher);

    @DELETE("/api/teachers/{id}")
    Call<Void> deleteTeacher(@Path("id") Long id);

    // Students
    @POST("api/students")
    Call<Student> addStudent(@Body StudentDTO studentDTO);

    @GET("api/students")
    Call<List<Student>> getStudents();

    @GET("api/students/{id}")
    Call<Student> getStudent(@Path("id") Long id);

    @GET("api/students/email/{email}")
    Call<Student> getStudentByEmail(@Path("email") String email);

    @PUT("api/students/{id}")
    Call<Student> updateStudent(@Path("id") Long id, @Body StudentDTO studentDTO);

    @DELETE("api/students/{id}")
    Call<Void> deleteStudent(@Path("id") Long id);

    // Groups
    @POST("api/groups")
    Call<Group> addGroup(@Body GroupInputDTO groupInputDTO);

    @GET("api/groups")
    Call<List<Group>> getGroups();

    @GET("api/groups/{id}")
    Call<Group> getGroup(@Path("id") Long id);

    @PUT("api/groups/{id}")
    Call<Group> updateGroup(@Path("id") Long id, @Body GroupInputDTO groupInputDTO);

    @DELETE("api/groups/{id}")
    Call<Void> deleteGroup(@Path("id") Long id);

    @POST("api/levels")
    Call<Level> addLevel(@Body Level level);

    @GET("api/levels")
    Call<List<Level>> getLevels();

    @GET("api/levels/{id}")
    Call<Level> getLevel(@Path("id") Long id);

    @PUT("api/levels/{id}")
    Call<Level> updateLevel(@Path("id") Long id, @Body Level level);

    @DELETE("api/levels/{id}")
    Call<Void> deleteLevel(@Path("id") Long id);

    // Programs
    @POST("api/programs")
    Call<Program> addProgram(@Body Program program);

    @GET("api/programs")
    Call<List<Program>> getPrograms();

    @GET("api/programs/{id}")
    Call<Program> getProgram(@Path("id") Long id);

    @PUT("api/programs/{id}")
    Call<Program> updateProgram(@Path("id") Long id, @Body Program program);

    @DELETE("api/programs/{id}")
    Call<Void> deleteProgram(@Path("id") Long id);

    @GET("api/programs/teacher/{teacherId}")
    Call<List<Program>> getProgramsByTeacher(@Path("teacherId") Long teacherId);

    // Work Returns
    @GET("api/workreturns")
    Call<List<WorkReturn>> getWorkReturns();

    @PUT("api/workreturns/{id}")
    Call<WorkReturn> updateWorkReturn(@Path("id") Long id, @Body WorkReturn workReturn);

    @GET("api/workreturns/{id}/file")
    Call<ResponseBody> downloadFile(@Path("id") Long id);

    @POST("api/admins")
    Call<Admin> addAdmin(@Body Admin admin);

    // Assignments
    @POST("api/assignments")
    Call<AssignmentDTO> createAssignment(@Body AssignmentDTO assignmentDTO);

    @PUT("api/assignments/{id}")
    Call<AssignmentDTO> updateAssignment(@Path("id") Long id, @Body AssignmentDTO assignmentDTO);

    @GET("api/assignments")
    Call<List<AssignmentDTO>> getAssignments(); // Consolidated to match activity

    @GET("api/assignments/{id}")
    Call<AssignmentDTO> getAssignment(@Path("id") Long id);

    @GET("api/assignments/group/{groupId}")
    Call<List<AssignmentDTO>> getAssignmentsByGroup(@Path("groupId") Long groupId);

    @DELETE("api/assignments/{id}")
    Call<Void> deleteAssignment(@Path("id") Long id);
    @GET("api/assignments") // Same endpoint, different return type
    Call<List<Assignment>> getAssignmentsAsAssignment();
    // Grades
    @GET("api/grades")
    Call<List<Grade>> getGrades();

    @GET("api/grades/{id}")
    Call<Grade> getGrade(@Path("id") Long id);

    @POST("api/grades")
    Call<Grade> createGrade(@Body GradeDTO gradeDTO);

    @PUT("api/grades/{id}")
    Call<Grade> updateGrade(@Path("id") Long id, @Body GradeDTO gradeDTO);

    @DELETE("api/grades/{id}")
    Call<Void> deleteGrade(@Path("id") Long id);

    @POST("api/subjects") // Fixed to match backend
    Call<Subject> addSubject(@Body SubjectInputDTO subject);

    @GET("api/subjects")
    Call<List<Subject>> getSubjects();

    @GET("api/subjects") // Same endpoint, different return type
    Call<List<SubjectDTO>> getSubjectsDTO();

    @GET("api/subjects/{id}")
    Call<Subject> getSubject(@Path("id") Long id);

    @PUT("api/subjects/{id}")
    Call<Subject> updateSubject(@Path("id") Long id, @Body SubjectInputDTO subject);

    @DELETE("api/subjects/{id}")
    Call<Void> deleteSubject(@Path("id") Long id);

    @GET("api/workreturns")
    Call<List<WorkReturn>> getAllWorkReturns(
            @Query("groupId") Long groupId,
            @Query("studentId") Long studentId
    );
    @Multipart
    @POST("api/workreturns")
    Call<Map<String, Object>> createWorkReturn(
            @Part("assignmentId") RequestBody assignmentId,
            @Part("studentId") RequestBody studentId,
            @Part MultipartBody.Part file
    );
    @GET("api/workreturns/{id}")
    Call<WorkReturn> getWorkReturnById(@Path("id") Long id);

    // In your ApiService interface, ensure the endpoint matches exactly:
    @Streaming
    @GET("/api/workreturns/{id}/download")
    Call<ResponseBody> downloadWorkReturnFile(@Path("id") Long workReturnId);
    @PUT("api/workreturns/{id}")
    Call<Map<String, Object>> updateWorkReturn(
            @Path("id") Long id,
            @Body Map<String, Object> updates
    );
    // New method for downloading a file


    // New method for updating a grade
    @PUT("api/workreturns/{workReturnId}")
    Call<WorkReturn> updateWorkReturnGrade(@Path("workReturnId") Long workReturnId, @Body WorkReturn workReturn);
    @DELETE("api/workreturns/{id}")
    Call<Void> deleteWorkReturn(@Path("id") Long id);

}