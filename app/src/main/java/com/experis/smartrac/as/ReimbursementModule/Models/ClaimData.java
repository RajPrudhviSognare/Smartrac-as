package com.experis.smartrac.as.ReimbursementModule.Models;

import com.google.gson.annotations.Expose;

/**
 * Created by RajPrudhviMarella on 14/Mar/2022.
 */

public class ClaimData {
    @Expose
    private String requestID = "0";
    @Expose
    private String rowID = "0";
    @Expose
    private String head = "0";
    @Expose
    private String hotelCity = "0";
    @Expose
    private String limit = "0";
    @Expose
    private String isAmountApproved;

    //    private List<Object> proofAttach = null;
//
//    private List<Object> imagePath = null;
    @Expose
    private String isHeadApproved;

    //    private List<Object> isAmountModified = null;
    @Expose
    private String mode = "0";
    @Expose
    private String tollTax = "0";
    @Expose
    private String cityName = "0";
    @Expose
    private String startTime = "0";
    @Expose
    private String endTime = "0";

    //    private List<Object> cityName1 = null;
//
//    private List<Object> proofAttach3 = null;
//
//    private List<Object> imagePath2 = null;
    @Expose
    private String baseAmount = "0";
    @Expose
    private String taxTotalAmount = "0";
    @Expose
    private String othersHead = "0";
    @Expose
    private String billFrom = "0";
    @Expose
    private String billTo = "0";
    @Expose
    private String amount = "0.00";
    @Expose
    private String billNumber = "0";
    @Expose
    private String journeyFrom = "0";
    @Expose
    private String journeyTo = "0";
    @Expose
    private String journeyKM = "0";
    @Expose
    private String billDate = "0";
    @Expose
    private String hotelName = "0";
    @Expose
    private String remarks = "0";
    @Expose
    private String ticketDate = "0";
    @Expose
    private String startKM = "0";
    @Expose
    private String endKM = "0";
    @Expose
    private String ProofAttach = "";
    @Expose
    private String ProofAttach2 = "";
    @Expose
    private String ProofAttach3 = "";

    public String getProofAttach() {
        return ProofAttach;
    }

    public void setProofAttach(String proofAttach) {
        ProofAttach = proofAttach;
    }

    public String getProofAttach2() {
        return ProofAttach2;
    }

    public void setProofAttach2(String proofAttach2) {
        ProofAttach2 = proofAttach2;
    }

    public String getProofAttach3() {
        return ProofAttach3;
    }

    public void setProofAttach3(String proofAttach3) {
        ProofAttach3 = proofAttach3;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRowID() {
        return rowID;
    }

    public void setRowID(String rowID) {
        this.rowID = rowID;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getHotelCity() {
        return hotelCity;
    }

    public void setHotelCity(String hotelCity) {
        this.hotelCity = hotelCity;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getIsAmountApproved() {
        return isAmountApproved;
    }

    public void setIsAmountApproved(String isAmountApproved) {
        this.isAmountApproved = isAmountApproved;
    }

//    public List<Object> getProofAttach() {
//        return proofAttach;
//    }
//
//    public void setProofAttach(List<Object> proofAttach) {
//        this.proofAttach = proofAttach;
//    }
//
//    public List<Object> getImagePath() {
//        return imagePath;
//    }
//
//    public void setImagePath(List<Object> imagePath) {
//        this.imagePath = imagePath;
//    }

    public String getIsHeadApproved() {
        return isHeadApproved;
    }

    public void setIsHeadApproved(String isHeadApproved) {
        this.isHeadApproved = isHeadApproved;
    }

//    public List<Object> getIsAmountModified() {
//        return isAmountModified;
//    }
//
//    public void setIsAmountModified(List<Object> isAmountModified) {
//        this.isAmountModified = isAmountModified;
//    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTollTax() {
        return tollTax;
    }

    public void setTollTax(String tollTax) {
        this.tollTax = tollTax;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

//    public List<Object> getCityName1() {
//        return cityName1;
//    }
//
//    public void setCityName1(List<Object> cityName1) {
//        this.cityName1 = cityName1;
//    }
//
//    public List<Object> getProofAttach3() {
//        return proofAttach3;
//    }
//
//    public void setProofAttach3(List<Object> proofAttach3) {
//        this.proofAttach3 = proofAttach3;
//    }
//
//    public List<Object> getImagePath2() {
//        return imagePath2;
//    }
//
//    public void setImagePath2(List<Object> imagePath2) {
//        this.imagePath2 = imagePath2;
//    }

    public String getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(String baseAmount) {
        this.baseAmount = baseAmount;
    }

    public String getTaxTotalAmount() {
        return taxTotalAmount;
    }

    public void setTaxTotalAmount(String taxTotalAmount) {
        this.taxTotalAmount = taxTotalAmount;
    }

    public String getOthersHead() {
        return othersHead;
    }

    public void setOthersHead(String othersHead) {
        this.othersHead = othersHead;
    }

    public String getBillFrom() {
        return billFrom;
    }

    public void setBillFrom(String billFrom) {
        this.billFrom = billFrom;
    }

    public String getBillTo() {
        return billTo;
    }

    public void setBillTo(String billTo) {
        this.billTo = billTo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getJourneyFrom() {
        return journeyFrom;
    }

    public void setJourneyFrom(String journeyFrom) {
        this.journeyFrom = journeyFrom;
    }

    public String getJourneyTo() {
        return journeyTo;
    }

    public void setJourneyTo(String journeyTo) {
        this.journeyTo = journeyTo;
    }

    public String getJourneyKM() {
        return journeyKM;
    }

    public void setJourneyKM(String journeyKM) {
        this.journeyKM = journeyKM;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTicketDate() {
        return ticketDate;
    }

    public void setTicketDate(String ticketDate) {
        this.ticketDate = ticketDate;
    }

    public String getStartKM() {
        return startKM;
    }

    public void setStartKM(String startKM) {
        this.startKM = startKM;
    }

    public String getEndKM() {
        return endKM;
    }

    public void setEndKM(String endKM) {
        this.endKM = endKM;
    }

}
