package com.depec.depechuancayosur;

import com.google.firebase.firestore.PropertyName;

public class Inscrito {
    private String userId;
    private String name;
    private String phoneNumber;
    private double amountPaid;
    private String firstInstallment;
    private String secondInstallment;
    private String thirdInstallment;
    private String paymentReceiptUrl;
    private boolean verified;
    private boolean firstInstallmentVerified;
    private boolean secondInstallmentVerified;
    private boolean thirdInstallmentVerified;
    private String church;
    private boolean hasTent;
    private String busNumber;
    private String seatNumber;
    private String alergia;
    private int age;
    private String qrCodeUrl;
    private boolean qrGenerated;
    private String documentId;

    public Inscrito() {}

    @PropertyName("userId")
    public String getUserId() {
        return userId;
    }

    @PropertyName("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @PropertyName("phoneNumber")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @PropertyName("amountPaid")
    public double getAmountPaid() {
        return amountPaid;
    }

    @PropertyName("amountPaid")
    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    @PropertyName("firstInstallment")
    public String getFirstInstallment() {
        return firstInstallment;
    }

    @PropertyName("firstInstallment")
    public void setFirstInstallment(String firstInstallment) {
        this.firstInstallment = firstInstallment;
    }

    @PropertyName("secondInstallment")
    public String getSecondInstallment() {
        return secondInstallment;
    }

    @PropertyName("secondInstallment")
    public void setSecondInstallment(String secondInstallment) {
        this.secondInstallment = secondInstallment;
    }

    @PropertyName("thirdInstallment")
    public String getThirdInstallment() {
        return thirdInstallment;
    }

    @PropertyName("thirdInstallment")
    public void setThirdInstallment(String thirdInstallment) {
        this.thirdInstallment = thirdInstallment;
    }

    @PropertyName("paymentReceiptUrl")
    public String getPaymentReceiptUrl() {
        return paymentReceiptUrl;
    }

    @PropertyName("paymentReceiptUrl")
    public void setPaymentReceiptUrl(String paymentReceiptUrl) {
        this.paymentReceiptUrl = paymentReceiptUrl;
    }

    @PropertyName("verified")
    public boolean isVerified() {
        return verified;
    }

    @PropertyName("verified")
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @PropertyName("firstInstallmentVerified")
    public boolean isFirstInstallmentVerified() {
        return firstInstallmentVerified;
    }

    @PropertyName("firstInstallmentVerified")
    public void setFirstInstallmentVerified(boolean firstInstallmentVerified) {
        this.firstInstallmentVerified = firstInstallmentVerified;
    }

    @PropertyName("secondInstallmentVerified")
    public boolean isSecondInstallmentVerified() {
        return secondInstallmentVerified;
    }

    @PropertyName("secondInstallmentVerified")
    public void setSecondInstallmentVerified(boolean secondInstallmentVerified) {
        this.secondInstallmentVerified = secondInstallmentVerified;
    }

    @PropertyName("thirdInstallmentVerified")
    public boolean isThirdInstallmentVerified() {
        return thirdInstallmentVerified;
    }

    @PropertyName("thirdInstallmentVerified")
    public void setThirdInstallmentVerified(boolean thirdInstallmentVerified) {
        this.thirdInstallmentVerified = thirdInstallmentVerified;
    }

    @PropertyName("church")
    public String getChurch() {
        return church;
    }

    @PropertyName("church")
    public void setChurch(String church) {
        this.church = church;
    }

    @PropertyName("hasTent")
    public boolean isHasTent() {
        return hasTent;
    }

    @PropertyName("hasTent")
    public void setHasTent(boolean hasTent) {
        this.hasTent = hasTent;
    }

    @PropertyName("busNumber")
    public String getBusNumber() {
        return busNumber;
    }

    @PropertyName("busNumber")
    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    @PropertyName("seatNumber")
    public String getSeatNumber() {
        return seatNumber;
    }

    @PropertyName("seatNumber")
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    @PropertyName("alergia")
    public String getAlergia() {
        return alergia;
    }

    @PropertyName("alergia")
    public void setAlergia(String alergia) {
        this.alergia = alergia;
    }

    @PropertyName("age")
    public int getEdad() {
        return age;
    }

    @PropertyName("age")
    public void setEdad(int age) {
        this.age = age;
    }

    @PropertyName("qrCodeUrl")
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    @PropertyName("qrCodeUrl")
    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    @PropertyName("qrGenerated")
    public boolean isQrGenerated() {
        return qrGenerated;
    }

    @PropertyName("qrGenerated")
    public void setQrGenerated(boolean qrGenerated) {
        this.qrGenerated = qrGenerated;
    }

    @PropertyName("documentId")
    public String getDocumentId() {
        return documentId;
    }

    @PropertyName("documentId")
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public boolean hasFirstInstallment() {
        return firstInstallment != null && !firstInstallment.isEmpty();
    }

    public boolean hasSecondInstallment() {
        return secondInstallment != null && !secondInstallment.isEmpty();
    }

    public boolean hasThirdInstallment() {
        return thirdInstallment != null && !thirdInstallment.isEmpty();
    }
}
