package com.nsa.flexjobs.Interfaces;

import com.nsa.flexjobs.Model.UserModel;

public interface OnSyncUserData {
    public void newUserData(UserModel userModel);
    public void newRequestdata();
}
