package com.nsa.flexjobs.Interfaces;

import com.google.firebase.database.DataSnapshot;

public interface SnapShotListener {
    void onSnapshot(DataSnapshot snapshot);
}
