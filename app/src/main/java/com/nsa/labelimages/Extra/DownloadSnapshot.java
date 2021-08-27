package com.nsa.labelimages.Extra;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nsa.labelimages.Interfaces.SnapShotListener;

import org.jetbrains.annotations.NotNull;

public class DownloadSnapshot extends AsyncTask<DatabaseReference,Void, DataSnapshot> {
   SnapShotListener snapShotListener;

    public DownloadSnapshot(SnapShotListener snapShotListener){
        this.snapShotListener = snapShotListener;
    }
    DataSnapshot snap=null;
    @Override
    protected DataSnapshot doInBackground(DatabaseReference... databaseReferences){

        databaseReferences[0].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               onPostExecute(snapshot);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
       return null;
    }

    @Override
    protected void onPostExecute(DataSnapshot snapshot){

            snapShotListener.onSnapshot(snapshot);

    }
}
