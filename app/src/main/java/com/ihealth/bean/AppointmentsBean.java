package com.ihealth.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 预约信息的实体类
 * Created by Liuhuan on 2019/06/05.
 */
public class AppointmentsBean implements Serializable {

    private Patient patient;
    private Appointments appointment;
    private List<PatientReport> patientReport;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public List<PatientReport> getPatientReport() {
        return patientReport;
    }

    public void setPatientReport(List<PatientReport> patientReport) {
        this.patientReport = patientReport;
    }

    public Appointments getAppointment() {
        return appointment;
    }

    public void setAppointments(Appointments appointments) {
        this.appointment = appointment;
    }

    public class PatientReport{
        private String startDate;
        private String endDate;
        private String content;

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public class  Patient implements Serializable{

        private String userId;
        private String idCard;
        private String nickname;
        private String phoneNumber;
        private String socialInsurance;

        private String _id;
        private String height;
        private String weight;
        private String doctor;
        private String mobile;
        private String avatar;
        private String petname;
        private String patientType;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getSocialInsurance() {
            return socialInsurance;
        }

        public void setSocialInsurance(String socialInsurance) {
            this.socialInsurance = socialInsurance;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getDoctor() {
            return doctor;
        }

        public void setDoctor(String doctor) {
            this.doctor = doctor;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getPetname() {
            return petname;
        }

        public void setPetname(String petname) {
            this.petname = petname;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPatientType() {
            return patientType;
        }

        public void setPatientType(String patientType) {
            this.patientType = patientType;
        }
    }

    public static class Appointments implements Serializable{
        private String _id;
        private String isOutPatient;
        private String blood;
        private String insulinAt;
        private String footAt;
        private String eyeGroundAt;
        private String healthTech;
        private String nutritionAt;
        private String quantizationAt;
        private String healthCareTeamId;
        private String type;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getIsOutPatient() {
            return isOutPatient;
        }

        public void setIsOutPatient(String isOutPatient) {
            this.isOutPatient = isOutPatient;
        }

        public String getBlood() {
            return blood;
        }

        public void setBlood(String blood) {
            this.blood = blood;
        }

        public String getInsulinAt() {
            return insulinAt;
        }

        public void setInsulinAt(String insulinAt) {
            this.insulinAt = insulinAt;
        }

        public String getFootAt() {
            return footAt;
        }

        public void setFootAt(String footAt) {
            this.footAt = footAt;
        }

        public String getEyeGroundAt() {
            return eyeGroundAt;
        }

        public void setEyeGroundAt(String eyeGroundAt) {
            this.eyeGroundAt = eyeGroundAt;
        }

        public String getHealthTech() {
            return healthTech;
        }

        public void setHealthTech(String healthTech) {
            this.healthTech = healthTech;
        }

        public String getNutritionAt() {
            return nutritionAt;
        }

        public void setNutritionAt(String nutritionAt) {
            this.nutritionAt = nutritionAt;
        }

        public String getQuantizationAt() {
            return quantizationAt;
        }

        public void setQuantizationAt(String quantizationAt) {
            this.quantizationAt = quantizationAt;
        }

        public String getHealthCareTeamId() {
            return healthCareTeamId;
        }

        public void setHealthCareTeamId(String healthCareTeamId) {
            this.healthCareTeamId = healthCareTeamId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}


