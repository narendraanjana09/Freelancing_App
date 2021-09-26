package com.nsa.flexjobs.Interfaces;

public interface UploadComplete {
    void onComplete();
    void onProgress(double progress,double maxProgress);
}
