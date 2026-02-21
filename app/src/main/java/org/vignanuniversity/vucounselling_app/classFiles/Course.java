package org.vignanuniversity.vucounselling_app.classFiles;

public class Course {
    private String code;
    private String name;
    private String section;
    private int totalClasses;
    private int attendedClasses;
    private float attendancePercentage;

    public Course(String code, String name, String section, int totalClasses, int attendedClasses, float attendancePercentage) {
        this.code = code;
        this.name = name;
        this.section = section;
        this.totalClasses = totalClasses;
        this.attendedClasses = attendedClasses;
        this.attendancePercentage = attendancePercentage;
    }

    // Getters and setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public void setAttendedClasses(int attendedClasses) {
        this.attendedClasses = attendedClasses;
    }

    public float getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(float attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }
}

