package gclproject.onesong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ListMediaItemsAdapter extends BaseAdapter {

    Context context;

    protected List<MediaItem> listMediaItems;
    LayoutInflater inflater;

    public ListMediaItemsAdapter(Context context, List<MediaItem> listMediaItems){
        this.listMediaItems = listMediaItems;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount(){
        return listMediaItems.size();
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.media_list_view_item, parent, false);

            viewHolder.imgMediaThumbnail = (ImageView)convertView.findViewById(R.id.imgMediaThumbnail);
            viewHolder.txtMediaTitle = (TextView)convertView.findViewById(R.id.txtMediaTitle);
            viewHolder.txtMediaLength = (TextView)convertView.findViewById(R.id.txtMediaLength);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        MediaItem mediaFile = listMediaItems.get(position);
        int mediaFileNameDisplayMaximumLength = mediaFile.getmMediaFileName().length() >= 20 ? 20 : mediaFile.getmMediaFileName().length();
        viewHolder.txtMediaTitle.setText(mediaFile.getmMediaFileName().substring(0, mediaFileNameDisplayMaximumLength) + "...");
        String mediaThumbnailUrl = mediaFile.getmThumbnailUrl();
        viewHolder.imgMediaThumbnail.setTag(mediaThumbnailUrl);

        DownloadImagesTask getImageFromSkyDriveTask = new DownloadImagesTask();
        getImageFromSkyDriveTask.execute(viewHolder.imgMediaThumbnail);

        int mediaLength = mediaFile.getmMediaLength();
        int mediaLengthSecond = (int)(Math.floor(mediaLength / 1000));
        int mediaLengthMinute = (int)(mediaLengthSecond / 60);
        mediaLengthSecond = mediaLengthSecond - mediaLengthMinute * 60;
        viewHolder.txtMediaLength.setText(mediaLength <= 0 ?
                "" :
                String.format("%02d", mediaLengthMinute) + ":" + String.format("%02d", mediaLengthSecond));

        return convertView;
    }

    public MediaItem getItem(int position){
        return listMediaItems.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    private class ViewHolder {
        ImageView imgMediaThumbnail;
        TextView txtMediaTitle;
        TextView txtMediaLength;
    }
}
