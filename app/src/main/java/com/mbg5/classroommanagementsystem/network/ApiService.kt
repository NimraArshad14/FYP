// ApiService.kt
package com.mbg5.classroommanagementsystem.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ----- TEACHERS -----
    @GET("api/admin/teachers")
    suspend fun listTeachers(): Response<List<TeacherResponse>>

    @GET("api/admin/teachers/{id}")
    suspend fun getTeacher(@Path("id") id: String): Response<TeacherResponse>

    @POST("api/admin/teachers")
    suspend fun createTeacher(@Body req: TeacherRequest): Response<TeacherResponse>

    @PUT("api/admin/teachers/{id}")
    suspend fun updateTeacher(
        @Path("id") id: String,
        @Body req: TeacherRequest
    ): Response<TeacherResponse>

    @DELETE("api/admin/teachers/{id}")
    suspend fun deleteTeacher(@Path("id") id: String): Response<Unit>


    // ----- STUDENTS -----
    @GET("api/admin/students")
    suspend fun listStudents(): Response<List<StudentResponse>>

    @GET("api/admin/students/{id}")
    suspend fun getStudent(@Path("id") id: String): Response<StudentResponse>

    @Multipart
    @POST("api/admin/students")
    suspend fun createStudent(
        @Part("data") data: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<StudentResponse>

    @PUT("api/admin/students/{id}")
    suspend fun updateStudent(
        @Path("id") id: String,
        @Body req: StudentRequest
    ): Response<StudentResponse>

    @DELETE("api/admin/students/{id}")
    suspend fun deleteStudent(@Path("id") id: String): Response<Unit>


    // ----- CLASSROOMS -----
    @GET("api/admin/classes")
    suspend fun listClasses(): Response<List<ClassroomResponse>>

    @GET("api/admin/classes/{id}")
    suspend fun getClass(@Path("id") id: String): Response<ClassroomResponse>

    @POST("api/admin/classes")
    suspend fun createClass(@Body req: ClassroomRequest): Response<ClassroomResponse>

    @PUT("api/admin/classes/{id}")
    suspend fun updateClass(
        @Path("id") id: String,
        @Body req: ClassroomRequest
    ): Response<ClassroomResponse>

    @DELETE("api/admin/classes/{id}")
    suspend fun deleteClass(@Path("id") id: String): Response<Unit>

    // ----- FEES -----
    @GET("api/admin/fees")
    suspend fun listFees(): Response<List<FeeResponse>>

    @GET("api/admin/fees/{id}")
    suspend fun getFee(@Path("id") id: String): Response<FeeResponse>

    @GET("api/admin/fees/student/{studentId}")
    suspend fun getFeesByStudent(@Path("studentId") studentId: String): Response<List<FeeResponse>>

    @POST("api/admin/fees")
    suspend fun createFee(@Body req: FeeRequest): Response<FeeResponse>

    @PUT("api/admin/fees/{id}")
    suspend fun updateFee(
        @Path("id") id: String,
        @Body req: FeeRequest
    ): Response<FeeResponse>

    @DELETE("api/admin/fees/{id}")
    suspend fun deleteFee(@Path("id") id: String): Response<Unit>

    @PUT("api/admin/fees/{id}/mark-paid")
    suspend fun markFeeAsPaid(@Path("id") id: String): Response<FeeResponse>

    @PUT("api/admin/fees/{id}/mark-verified")
    suspend fun markFeeAsVerified(@Path("id") id: String): Response<FeeResponse>

    @GET("api/admin/fees/unpaid")
    suspend fun getUnpaidFees(): Response<List<FeeResponse>>

    @GET("api/admin/fees/unverified")
    suspend fun getUnverifiedFees(): Response<List<FeeResponse>>

    // ----- GRADES -----
    @GET("/api/admin/classes/{classId}")
    suspend fun getClass(
        @Header("X-User-Id") userId: String,
        @Path("classId") classId: String
    ): Response<ClassroomResponse>

    @GET("/api/admin/classes/{classId}/grades")
    suspend fun listGradesForClass(
        @Header("X-User-Id") userId: String,
        @Path("classId") classId: String
    ): Response<List<GradeResponse>>

    @POST("/api/admin/classes/{classId}/grades")
    suspend fun createGrade(
        @Header("X-User-Id") userId: String,
        @Path("classId") classId: String,
        @Body req: GradeRequest
    ): Response<GradeResponse>

    @PUT("/api/admin/grades/{gradeId}")
    suspend fun updateGrade(
        @Header("X-User-Id") userId: String,
        @Path("gradeId") gradeId: String,
        @Body req: GradeRequest
    ): Response<GradeResponse>

    @DELETE("/api/admin/grades/{gradeId}")
    suspend fun deleteGrade(
        @Header("X-User-Id") userId: String,
        @Path("gradeId") gradeId: String
    ): Response<Unit>

    @GET("/api/student/classes/{classId}/grades")
    suspend fun listMyGradesForClass(
        @Header("X-User-Id") userId: String,
        @Path("classId") classId: String
    ): Response<List<GradeResponse>>

    @POST("api/quizzes")
    suspend fun createQuiz(
        @Query("classId") classId: String,
        @Query("title") title: String,
        @Body questions: List<Question>
    ): Response<Quiz>

    @GET("api/quizzes/class/{classId}")
    suspend fun getQuizzesByClass(
        @Path("classId") classId: String
    ): Response<List<Quiz>>

    @POST("api/quizzes/attempt")
    suspend fun submitQuizAttempt(
        @Query("quizId") quizId: String,
        @Body answers: Map<String, String>
    ): Response<QuizAttempt>

    @GET("api/quizzes/{quizId}/my-attempts")
    suspend fun getStudentAttempts(
        @Path("quizId") quizId: String
    ): Response<List<QuizAttempt>>

    // SCHEDULE ENDPOINTS
    @Multipart
    @POST("api/admin/schedule/upload")
    suspend fun uploadSchedule(
        @Part file: MultipartBody.Part
    ): Response<ScheduleResponse>

    @GET("api/schedule/latest")
    suspend fun getLatestSchedule(): Response<ScheduleResponse>

    // Complaint endpoints
    @POST("api/complaints/create")
    suspend fun createComplaint(
        @Body request: ComplaintRequest,
        @Query("studentId") studentId: String,
        @Query("studentName") studentName: String,
        @Query("studentClass") studentClass: String
    ): ComplaintResponse

    @GET("api/complaints/all")
    suspend fun getAllComplaints(): List<ComplaintResponse>

    @GET("api/complaints/student/{studentId}")
    suspend fun getComplaintsByStudent(@Path("studentId") studentId: String): List<ComplaintResponse>

    @PUT("api/complaints/{complaintId}/status")
    suspend fun updateComplaintStatus(
        @Path("complaintId") complaintId: String,
        @Query("status") status: String,
        @Query("adminResponse") adminResponse: String
    ): ComplaintResponse

    @DELETE("api/complaints/{complaintId}")
    suspend fun deleteComplaint(@Path("complaintId") complaintId: String): Response<Unit>

    // Leave endpoints
    @POST("api/leaves/create")
    suspend fun createLeave(
        @Body request: LeaveRequest,
        @Query("studentId") studentId: String,
        @Query("studentName") studentName: String,
        @Query("studentClass") studentClass: String,
        @Query("teacherName") teacherName: String
    ): LeaveResponse

    @GET("api/leaves/student/{studentId}")
    suspend fun getLeavesByStudent(@Path("studentId") studentId: String): List<LeaveResponse>

    @GET("api/leaves/teacher/{teacherId}")
    suspend fun getLeavesByTeacher(@Path("teacherId") teacherId: String): List<LeaveResponse>

    @GET("api/leaves/all")
    suspend fun getAllLeaves(): List<LeaveResponse>

    @PUT("api/leaves/{leaveId}/status")
    suspend fun updateLeaveStatus(
        @Path("leaveId") leaveId: String,
        @Query("status") status: String,
        @Query("teacherResponse") teacherResponse: String
    ): LeaveResponse

    @DELETE("api/leaves/{leaveId}")
    suspend fun deleteLeave(@Path("leaveId") leaveId: String): Response<Unit>
}
