package com.experis.smartrac.as.ReimbursementModule.Models;


/**
 * Created by RajPrudhviMarella on 5/Dec/2021.
 */


public class InQueueForUploadDataModel {

    private String byteCode = "";
    private boolean isNewAdded = false;

    public boolean isNewAdded() {
        return isNewAdded;
    }

    public void setNewAdded(boolean newAdded) {
        isNewAdded = newAdded;
    }

    public String getByteCode() {
        return byteCode;
    }

    public void setByteCode(String byteCode) {
        this.byteCode = "data:image/png;base64," + byteCode.replace(" ", "").replace("\n", "");
    }
}
