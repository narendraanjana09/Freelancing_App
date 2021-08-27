package com.nsa.labelimages.Extra;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Storage {
    FirebaseStorage storage;
    StorageReference storageReference, testImagesReference, getUsersRefernce, taskImagesReference;
    public Storage(){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("AllInOne").child("Annotation");
        testImagesReference =storageReference.child("Test Images");
        taskImagesReference =storageReference.child("Task Images");
        getUsersRefernce =storageReference.child("users");
    }

    public StorageReference getTaskImagesReference() {
        return taskImagesReference;
    }

    public StorageReference getGetUsersRefernce() {
        return getUsersRefernce;
    }

    public StorageReference getTestImagesReference() {
        return testImagesReference;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }



    public StorageReference getStorageReference() {
        return storageReference;
    }

}