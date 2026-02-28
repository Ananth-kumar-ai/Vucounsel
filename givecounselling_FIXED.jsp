<%@ page import="java.sql.*"  %>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.*,java.text.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="org.json.JSONObject,java.util.*,org.json.JSONArray"%>
<jsp:useBean id="db" scope="application" class="model.DataManager"/>

<%
String firstq     = request.getParameter("firstq");
String secq       = request.getParameter("secq");
String thirdq     = request.getParameter("thirdq");
String fourthq    = request.getParameter("fourthq");
String fifthq     = request.getParameter("fifthq");
String sixq       = request.getParameter("sixq");
String registerno = request.getParameter("registerno");
String empcode    = request.getParameter("empcode");
String weekname   = request.getParameter("weekname");
String attended   = request.getParameter("attended");
String datetime   = request.getParameter("datetime");
String givenfrom  = "App";

// FIX 1: Null-safety defaults for all parameters
if (registerno == null) registerno = "";
if (empcode    == null) empcode    = "";
if (weekname   == null) weekname   = "";
if (attended   == null) attended   = "";
if (datetime   == null) datetime   = "";
if (firstq     == null) firstq     = "-";
if (secq       == null) secq       = "-";
if (thirdq     == null) thirdq     = "No";
if (fourthq    == null) fourthq    = "-";
if (fifthq     == null) fifthq     = "No";
if (sixq       == null) sixq       = "-";

// FIX 1: Safe weekname transformation with null/empty guard
if (!weekname.isEmpty()) {
    StringBuilder sb = new StringBuilder(weekname);
    int stInd   = weekname.indexOf("_");
    int lastInd = weekname.lastIndexOf("_");
    if (stInd != lastInd && stInd != -1 && lastInd != -1) {
        sb.setCharAt(stInd,   '[');
        sb.setCharAt(lastInd, ']');
        weekname = sb.toString();
    }
}

Class.forName("com.mysql.jdbc.Driver");
Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/vignan","root","Atten!@nce");
Statement st = con.createStatement();

// FIX 4: Use .equals() safely (not ==, not !=)
if ("Yes".equals(attended)) {
    try {
        // FIX 2: Changed != "No"   to   !thirdq.equals("No")  etc.
        if (!thirdq.equals("No") && !fifthq.equals("No")) {
            st.executeUpdate("insert into egnify.counselling_report values(0,'"
                + registerno + "','" + empcode   + "','" + weekname + "','"
                + firstq     + "','" + secq      + "','-','Yes','"
                + thirdq     + "','" + fourthq   + "','-','Yes','"
                + fifthq     + "',CAST(N'" + datetime + "' as DateTime),'"
                + sixq       + "','" + attended  + "','" + givenfrom + "')");

        } else if (!thirdq.equals("No")) {
            // FIX 3: Removed the stray extra ' that was before CAST
            st.executeUpdate("insert into egnify.counselling_report values(0,'"
                + registerno + "','" + empcode   + "','" + weekname + "','"
                + firstq     + "','" + secq      + "','-','Yes','"
                + thirdq     + "','" + fourthq   + "','-','"
                + fifthq     + "','-',CAST(N'" + datetime + "' as DateTime),'"
                + sixq       + "','" + attended  + "','" + givenfrom + "')");

        } else if (!fifthq.equals("No")) {
            st.executeUpdate("insert into egnify.counselling_report values(0,'"
                + registerno + "','" + empcode   + "','" + weekname + "','"
                + firstq     + "','" + secq      + "','-','"
                + thirdq     + "','-','" + fourthq   + "','-','Yes','"
                + fifthq     + "',CAST(N'" + datetime + "' as DateTime),'"
                + sixq       + "','" + attended  + "','" + givenfrom + "')");

        } else {
            st.executeUpdate("insert into egnify.counselling_report values(0,'"
                + registerno + "','" + empcode   + "','" + weekname + "','"
                + firstq     + "','" + secq      + "','-','"
                + thirdq     + "','-','" + fourthq   + "','-','"
                + fifthq     + "','-',CAST(N'" + datetime + "' as DateTime),'"
                + sixq       + "','" + attended  + "','" + givenfrom + "')");
        }
        out.println("Success");
    } catch (Exception e) {
        out.println("Error (Yes branch): " + e.getMessage());
    }

} else {
    // Absent / Not Needed — save minimal attendance record
    try {
        st.executeUpdate("insert into egnify.counselling_report values(0,'"
            + registerno + "','" + empcode + "','" + weekname
            + "','-','-','-','-','-','-','-','-','-',CAST(N'"
            + datetime + "' AS DateTime),'" + attended + "','" + attended + "','App')");
        out.println("Attendance Saved: " + attended);
    } catch (Exception e) {
        out.println("Error (Absent/NN branch): " + e.getMessage());
    }
}

out.println("Done: " + firstq + " " + secq + " " + thirdq + " " + fourthq
        + " " + fifthq + " " + sixq + " " + empcode + " " + registerno
        + " " + weekname + " " + datetime);

con.close();
%>
