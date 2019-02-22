package com.ihealth.bean;


import java.util.List;

public class HospitalBean{
    private int resultStatus;
    private String resultMessage;
    private List<resultContent> resultContent;
    public static class resultContent {
        private String hospitalId;
        private String hospitalCode;
        private String name;
        private String fullname;
        private String logoImg;

        public resultContent(String hospitalId, String hospitalCode, String name, String fullname, String logoImg) {
            this.hospitalId = hospitalId;
            this.hospitalCode = hospitalCode;
            this.name = name;
            this.fullname = fullname;
            this.logoImg = logoImg;
        }

        public String getHospitalId() {
            return hospitalId;
        }

        public void setHospitalId(String hospitalId) {
            this.hospitalId = hospitalId;
        }

        public String getHospitalCode() {
            return hospitalCode;
        }

        public void setHospitalCode(String hospitalCode) {
            this.hospitalCode = hospitalCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getLogoImg() {
            return logoImg;
        }

        public void setLogoImg(String logoImg) {
            this.logoImg = logoImg;
        }

        @Override
        public String toString() {
            return "HospitalBean{" +
                    "hospitalId='" + hospitalId + '\'' +
                    ", hospitalCode='" + hospitalCode + '\'' +
                    ", name='" + name + '\'' +
                    ", fullname='" + fullname + '\'' +
                    ", logoImg='" + logoImg + '\'' +
                    '}';
        }
    }

    public int getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(int resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public List<HospitalBean.resultContent> getResultContent() {
        return resultContent;
    }

    public void setResultContent(List<HospitalBean.resultContent> resultContent) {
        this.resultContent = resultContent;
    }
}


