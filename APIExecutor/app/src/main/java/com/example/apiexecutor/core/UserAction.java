package com.example.apiexecutor.core;

import android.os.Parcel;
import android.os.Parcelable;

public class UserAction implements Parcelable {
    private String actionName;
    private String viewPath;
    private int viewId;
    private String text;
    public UserAction(String actionName,String viewPath,int viewId){
        this.actionName = actionName;
        this.viewPath = viewPath;
        this.viewId = viewId;
    }


    protected UserAction(Parcel in) {
        actionName = in.readString();
        viewPath = in.readString();
        viewId = in.readInt();
        text = in.readString();
    }

    public static final Creator<UserAction> CREATOR = new Creator<UserAction>() {
        @Override
        public UserAction createFromParcel(Parcel in) {
            return new UserAction(in);
        }

        @Override
        public UserAction[] newArray(int size) {
            return new UserAction[size];
        }
    };

    public String getActionName() {
        return actionName;
    }

    public String getViewPath() {
        return viewPath;
    }

    public int getViewId() {
        return viewId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(actionName);
        dest.writeString(viewPath);
        dest.writeInt(viewId);
        dest.writeString(text);
    }
}
