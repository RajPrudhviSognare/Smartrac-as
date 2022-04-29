package com.experis.smartrac.as.ReimbursementModule.Models;

/**
 * Created by RajPrudhviMarella on 02/Dec/2021.
 */

public class InternalStorageFileDataModel {
    private String directoryPath;
    private String filePath;

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
