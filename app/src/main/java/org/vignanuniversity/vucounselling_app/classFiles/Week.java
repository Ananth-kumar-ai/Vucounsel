package org.vignanuniversity.vucounselling_app.classFiles;

public class Week {
    private String name;
    private String startDate;
    private String endDate;
    private String status;

    public Week(String name, String startDate, String endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Week(String name, String startDate, String endDate, String status) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getName() { return name; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return name + " [" + startDate + " to " + endDate + "]";
    }
    public void setName(String name) { this.name = name; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setStatus(String status) { this.status = status;}
}

