package gclproject.onesong;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

    ImageView imageView;

    public DownloadImagesTask() {

    }

    @Override
    protected Bitmap doInBackground(ImageView... imageViews) {
        imageView = imageViews[0];
        try {
            URL thumbnailUrl = new URL(imageView.getTag().toString());
            HttpsURLConnection imageConnection = (HttpsURLConnection) thumbnailUrl.openConnection();
            imageConnection.setDoInput(true);
            imageConnection.connect();
            InputStream inputStreamOfImage = imageConnection.getInputStream();
            return BitmapFactory.decodeStream(inputStreamOfImage);
        } catch (MalformedURLException ex) {

        } catch (IOException ex) {

        } catch (Exception ex) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap imageBitmapResult) {
        if (imageBitmapResult == null) {
            imageView.setImageResource(R.drawable.default_thumbnail);
        } else {
            imageView.setImageBitmap(imageBitmapResult);
        }
    }
}