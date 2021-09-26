package com.nsa.flexjobs.Extra;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBase {
    FirebaseDatabase database;
    DatabaseReference appReference,referenceUsersCount,referenceUsers, referenceTask,
          referencePaymentQueueHistory,referencePaymentCreditHistory,referenceChats;



    public FireBase(){
        database = FirebaseDatabase.getInstance();
        appReference=database.getReference("AllInOne2");
        referenceUsersCount=appReference.child("user_count");
        referenceUsers=appReference.child("users");
        referenceChats=appReference.child("chats");
        referenceTask =appReference.child("tasks");
        referencePaymentQueueHistory =appReference.child("requestedPayment");
        referencePaymentCreditHistory=appReference.child("creditPaymemt");
    }

    public DatabaseReference getReferenceChats() {
        return referenceChats;
    }

    public DatabaseReference getReferencePaymentCreditHistory() {
        return referencePaymentCreditHistory;
    }

    public DatabaseReference getReferencePaymentQueueHistory() {
        return referencePaymentQueueHistory;
    }

    public DatabaseReference getReferenceUsersCount() {
        return referenceUsersCount;
    }

    public DatabaseReference getReferenceTask() {
        return referenceTask;
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public DatabaseReference getReferenceUsers() {
        return referenceUsers;
    }
}
