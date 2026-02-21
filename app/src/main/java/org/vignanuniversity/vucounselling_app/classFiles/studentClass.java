package org.vignanuniversity.vucounselling_app.classFiles;

public class studentClass {
    String regno,name,empcode,attendance,marks,feedback,studentImage,studentReport;

    public studentClass() {
    }

    public studentClass(String regno, String name, String empcode, String attendance, String marks, String feedback, String studentImage, String studentReport) {
        this.regno = regno;
        this.name = name;
        this.empcode = empcode;
        this.attendance = attendance;
        this.marks = marks;
        this.feedback = feedback;
        this.studentImage = studentImage;
        this.studentReport = studentReport;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmpcode() {
        return empcode;
    }

    public void setEmpcode(String empcode) {
        this.empcode = empcode;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getStudentImage() {
        return studentImage;
    }

    public void setStudentImage(String studentImage) {
        this.studentImage = studentImage;
    }

    public String getStudentReport() {
        return studentReport;
    }

    public void setStudentReport(String studentReport) {
        this.studentReport = studentReport;
    }

    @Override
    public String toString() {
        return "studentClass{" +
                "regno='" + regno + '\'' +
                ", name='" + name + '\'' +
                ", empcode='" + empcode + '\'' +
                ", attendance='" + attendance + '\'' +
                ", marks='" + marks + '\'' +
                ", feedback='" + feedback + '\'' +
                ", studentImage='" + studentImage + '\'' +
                ", studentReport='" + studentReport + '\'' +
                '}';
    }
}
