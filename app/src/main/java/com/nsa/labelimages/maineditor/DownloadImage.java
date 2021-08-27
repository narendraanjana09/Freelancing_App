package com.nsa.labelimages.maineditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.nsa.labelimages.Interfaces.ImageDownloadListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImage extends AsyncTask<Uri,Void, Bitmap> {
    ImageDownloadListener downloadListener;
   public DownloadImage(ImageDownloadListener downloadListener){
        this.downloadListener=downloadListener;
    }
    @Override
    protected Bitmap doInBackground(Uri... uris) {
        final Uri url = uris[0];
        Bitmap bitmap = null;

        try {
            final InputStream inputStream = new URL(url.toString()).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (final MalformedURLException malformedUrlException) {
            Log.e("ImageDown", "doInBackground: "+malformedUrlException.getMessage() );
            // Handle error
        } catch (final IOException ioException) {
            Log.e("ImageDown", "doInBackground: "+ioException.getMessage() );
            // Handle error
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (null != bitmap) {
            downloadListener.onImageDownloaded(bitmap);
        } else {
            downloadListener.onImageDownloadError();
        }
    }
}
