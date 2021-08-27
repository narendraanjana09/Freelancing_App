package com.nsa.labelimages.Interfaces;

import android.graphics.Bitmap;

public interface ImageDownloadListener {
    void onImageDownloaded(final Bitmap bitmap);
    void onImageDownloadError();
}
