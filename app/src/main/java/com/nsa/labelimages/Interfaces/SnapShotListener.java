package com.nsa.labelimages.Interfaces;

import com.google.firebase.database.DataSnapshot;

public interface SnapShotListener {
    void onSnapshot(DataSnapshot snapshot);
}
