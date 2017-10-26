package com.test.internship.internshiptesting.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Thompson on 16-07-2017.
 */
//Network history information


public class DataUsage {

    public String name,upload,download,total;
    public Drawable icon;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
//Name of the application
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
//Uploaded Data
    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }
//Downloaded Data
    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }
//total data
    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
