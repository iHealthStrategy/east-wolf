package com.ihealth.bean;

/**
 * 登录信息返回数据Bean类
 *
 * @author liyanwen
 * @date 2019-02-14
 */
public class LoginBean {
    private String message;
    private String token;
    private User user;
    private static class User{
        private String groupId;
        private String hospitalFullName;
        private String hospitalLogoImage;

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getHospitalFullName() {
            return hospitalFullName;
        }

        public void setHospitalFullName(String hospitalFullName) {
            this.hospitalFullName = hospitalFullName;
        }

        public String getHospitalLogoImage() {
            return hospitalLogoImage;
        }

        public void setHospitalLogoImage(String hospitalLogoImage) {
            this.hospitalLogoImage = hospitalLogoImage;
        }

        @Override
        public String toString() {
            return "User{" +
                    "groupId='" + groupId + '\'' +
                    ", hospitalFullName='" + hospitalFullName + '\'' +
                    ", hospitalLogoImage='" + hospitalLogoImage + '\'' +
                    '}';
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", user=" + user +
                '}';
    }
}
