package gclproject.onesong;

public class MediaItem {
    private String mMediaFileName;
    private String mMediaSourceUrl;
    private int mMediaLength;
    private String mThumbnailUrl;

    public MediaItem(String mediaFileName, String mediaSourceUrl, int mediaLength, String thumbnailUrl) {
        mMediaFileName = mediaFileName;
        mMediaSourceUrl = mediaSourceUrl;
        mMediaLength = mediaLength;
        mThumbnailUrl = thumbnailUrl;
    }

    public String getmMediaSourceUrl() {
        return mMediaSourceUrl;
    }

    public String getmMediaFileName() {
        return mMediaFileName;
    }

    public int getmMediaLength() { return mMediaLength; }

    public String getmThumbnailUrl() { return mThumbnailUrl; }

    @Override
    public String toString() {
        return mMediaFileName;
    }
}
