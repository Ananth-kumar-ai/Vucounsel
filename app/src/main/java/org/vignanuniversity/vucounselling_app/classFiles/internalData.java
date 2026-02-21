package org.vignanuniversity.vucounselling_app.classFiles;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class internalData implements Parcelable {
    String name,status;

    public internalData() {
    }

    public internalData(String name, String status) {
        this.name = name;
        this.status = status;
    }

    protected internalData(Parcel in) {
        name = in.readString();
        status = in.readString();
    }

    public static final Creator<internalData> CREATOR = new Creator<internalData>() {
        @Override
        public internalData createFromParcel(Parcel in) {
            return new internalData(in);
        }

        @Override
        public internalData[] newArray(int size) {
            return new internalData[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof internalData)) return false;
        internalData that = (internalData) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getStatus(), that.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getStatus());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(status);
    }
}