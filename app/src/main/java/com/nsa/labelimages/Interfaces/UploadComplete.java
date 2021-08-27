package com.nsa.labelimages.Interfaces;

public interface UploadComplete {
    void onComplete();
    void onProgress(double progress,double maxProgress);
}
