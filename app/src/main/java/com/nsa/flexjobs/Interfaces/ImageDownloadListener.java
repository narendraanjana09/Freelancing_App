package com.nsa.flexjobs.Interfaces;

import android.graphics.Bitmap;

public interface ImageDownloadListener {
    void onImageDownloaded(final Bitmap bitmap);
    void onImageDownloadError();
}
