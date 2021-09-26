package com.nsa.flexjobs.Model;

import java.util.List;

public class UserModel {
   private String id;
   private String message_token;
   private String name;
   private String imageLink;
   private String joinDate;
   private PhoneModel phoneModel;
   private String email;
   private String address;
   private BalanceModel balance;
   private List<ApplicationModel> applicaModelList;
    private ReferralModel referral;
   private IDModel idModel;
   private List<TransactionModel> transactionModelList;
   private List<String> notificationsList;

    public UserModel() {
    }

    public UserModel(String id, String message_token, String name, String imageLink, String joinDate, PhoneModel phoneModel, String email, String address, BalanceModel balance, List<ApplicationModel> applicaModelList, ReferralModel referral, IDModel idModel, List<TransactionModel> transactionModelList, List<String> notificationsList) {
        this.id = id;
        this.message_token = message_token;
        this.name = name;
        this.imageLink = imageLink;
        this.joinDate = joinDate;
        this.phoneModel = phoneModel;
        this.email = email;
        this.address = address;
        this.balance = balance;
        this.applicaModelList = applicaModelList;
        this.referral = referral;
        this.idModel = idModel;
        this.transactionModelList = transactionModelList;
        this.notificationsList = notificationsList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage_token() {
        return message_token;
    }

    public void setMessage_token(String message_token) {
        this.message_token = message_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public PhoneModel getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(PhoneModel phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BalanceModel getBalance() {
        return balance;
    }

    public void setBalance(BalanceModel balance) {
        this.balance = balance;
    }

    public List<ApplicationModel> getApplicaModelList() {
        return applicaModelList;
    }

    public void setApplicaModelList(List<ApplicationModel> applicaModelList) {
        this.applicaModelList = applicaModelList;
    }

    public ReferralModel getReferral() {
        return referral;
    }

    public void setReferral(ReferralModel referral) {
        this.referral = referral;
    }

    public IDModel getIdModel() {
        return idModel;
    }

    public void setIdModel(IDModel idModel) {
        this.idModel = idModel;
    }

    public List<TransactionModel> getTransactionModelList() {
        return transactionModelList;
    }

    public void setTransactionModelList(List<TransactionModel> transactionModelList) {
        this.transactionModelList = transactionModelList;
    }

    public List<String> getNotificationsList() {
        return notificationsList;
    }

    public void setNotificationsList(List<String> notificationsList) {
        this.notificationsList = notificationsList;
    }

    public static class PhoneModel {
        private String number;
        private String code;

        public PhoneModel() {
        }

        public PhoneModel(String number, String code) {
            this.number = number;
            this.code = code;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
    public static class ReferralModel{
        private String myCode;
        private String link;
        private String byCode;

        public ReferralModel() {
        }

        public ReferralModel(String myCode, String link, String byCode) {
            this.myCode = myCode;
            this.link = link;
            this.byCode = byCode;
        }

        public String getMyCode() {
            return myCode;
        }

        public void setMyCode(String myCode) {
            this.myCode = myCode;
        }

        public String getByCode() {
            return byCode;
        }

        public void setByCode(String byCode) {
            this.byCode = byCode;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
