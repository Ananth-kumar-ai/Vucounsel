package org.vignanuniversity.vucounselling_app.classFiles;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class semData implements Parcelable {

    String grade,code,name,credits,points;

    public semData() {
    }

    public semData(String grade, String code, String name, String credits, String points) {
        this.grade = grade;
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.points = points;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

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

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    protected semData(Parcel in) {
        grade = in.readString();
        code = in.readString();
        name = in.readString();
        credits = in.readString();
        points = in.readString();
    }

    public static final Creator<semData> CREATOR = new Creator<semData>() {
        @Override
        public semData createFromParcel(Parcel in) {
            return new semData(in);
        }

        @Override
        public semData[] newArray(int size) {
            return new semData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(grade);
        dest.writeString(code);
        dest.writeString(points);
        dest.writeString(name);
        dest.writeString(credits);
    }
}
