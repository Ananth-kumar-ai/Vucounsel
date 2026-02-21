package org.vignanuniversity.vucounselling_app.Adapter;

public class URLs {
    private static String baseURL = "http://160.187.169.24/";
    private static String backUpURL = "http://160.187.169.12/";
    private static String fileAttendance = "cattendance.jsp";
    private static String fileInternalMarks = "internal_main.jsp";
    private static String fileCGPA = "aggregate_api.jsp";
    private static String fileSemesterMarks = "finalmarks_api.jsp";
    private static String filePersonalDetails = "personal_details.jsp";
    private static String fileRegNo = "?regno=";
    static final String apiURL = "counselling_jspapi/";
    static final String fileLogin = "clogin.jsp";
    static final String username ="?empcode=";
    static final String fileImage = "StaffPhotos/";
    static final String extension = ".JPG";
    static final String fileEmployeeDetails = "c_data_header.jsp";
    static final String fileStudentReports = "c_counselling_students_report.jsp";
    static final String fileDynamicWeeks = "dynamicweeks.jsp?registerno=";

    public static String getLoginUrl(){
        return baseURL+apiURL+fileLogin;
    }
    public static String getImageUrl(String usercode){
        return baseURL+apiURL+fileImage+usercode+extension;
    }
    public static String grtStudentImageUrl(String usercode){
        return "http://160.187.169.14/jspapi/photos/"+usercode+extension;
    }
    public static String getEmployeeDetailsUrl(String usercode){
        return baseURL+apiURL+fileEmployeeDetails+username+usercode;
    }
    public static String getStudentReportsUrl(String usercode){
        return baseURL+apiURL+fileStudentReports+"?Id="+usercode;
    }
    public static String getMainAttendanceUrl(String regno) {
        return baseURL+apiURL+fileAttendance+fileRegNo+regno;
    }
    public static String getStudentInfo(String regno){
        return baseURL+apiURL+filePersonalDetails+fileRegNo+regno;
    }
    public static String getInternalMarksUrl(String regno){
        return baseURL+apiURL+fileInternalMarks+fileRegNo+regno;
    }
    public static String getCGPA(String regno){
        return baseURL+apiURL+fileCGPA+fileRegNo+regno;
    }
    public static String getSemesterMarks(String regno,int year,int sem){
        return baseURL+apiURL+fileSemesterMarks+fileRegNo+regno+"&year="+year+"&sem="+sem;
    }
    public static String getDynamicWeeks(String regno){
        return baseURL+apiURL+fileDynamicWeeks+regno;
    }

}
