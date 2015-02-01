package gclproject.onesong;

import gclproject.onesong.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.VideoView;
import android.net.Uri;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveStatus;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationListener;
import com.microsoft.live.LiveOperationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();

    // UI references.
    private Button btnPlay;
    private ToggleButton btnLoop;
    private TextView txtDurationTimer;

    /**
     * Update timer on progress bar
     * */
    public void updateProgressBar() {
        // Running this thread after 100 milliseconds
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread to update the current moment of playing media
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            VideoView video = (VideoView)findViewById(R.id.videoView2);
            long totalDuration = video.getDuration();
            long currentDuration = video.getCurrentPosition();

            long currentSeconds = (int) (currentDuration / 1000);
            long totalSeconds = (int) (totalDuration / 1000);

            // Calculate percentage
            double percentage =(((double)currentSeconds)/totalSeconds)*100;

            // Update progress bar
            final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
            int progress = (int)percentage;
            seekBar.setProgress(progress);

            int mediaLengthSecond = (int)(Math.floor(currentDuration / 1000));
            int mediaLengthMinute = mediaLengthSecond / 60;
            mediaLengthSecond = mediaLengthSecond - mediaLengthMinute * 60;
            txtDurationTimer.setText(String.format("%02d", mediaLengthMinute) + ":" + String.format("%02d", mediaLengthSecond));

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnLoop = (ToggleButton) findViewById(R.id.btnLoop);
        txtDurationTimer = (TextView) findViewById(R.id.txtDurationTimer);

        final VideoView video = (VideoView)findViewById(R.id.videoView2);
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, video, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (btnLoop.isChecked()) {
                    video.start();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long totalDuration = video.getDuration();
                    video.seekTo((int)(((double)progress) / 100 * totalDuration));
                }
            }
        });

        readSkyDriveUserProfile();
        readSkyDrive();
    }

    public void onButtonClick(View v) {
        VideoView videoPlayer = (VideoView)findViewById(R.id.videoView2);

        switch (v.getId()) {
            case R.id.btnPlay:
                if (videoPlayer.isPlaying()) {
                    videoPlayer.pause();
                    btnPlay.setText("►");
                } else {
                    videoPlayer.start();
                    btnPlay.setText("❙❙");
                }
                break;
            case R.id.btnLoop:

                break;
        }
    }

    private List<MediaItem> availableMedia;

    private void readSkyDriveUserProfile(){
        LiveConnectClient client = LiveLoginActivity.client;
        client.getAsync("/me", new LiveOperationListener() {
            public void onComplete(LiveOperation operation) {
                try {
                    setTitle("Welcome, " + (operation.getResult()).getString("name"));
                } catch (JSONException e) {
                    TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
                    txtMessage.setText("Error: " + e.getMessage());
                }
            }

            public void onError(LiveOperationException exception, LiveOperation operation) {
                TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
                txtMessage.setText("Error: " + exception.getMessage());
            }
        });
    }

    private void readSkyDrive() {
        LiveConnectClient client = LiveLoginActivity.client;
        client.getAsync("me/skydrive/files", new LiveOperationListener() {
            public void onComplete(LiveOperation operation) {
                JSONArray returnedData;

                try {
                    returnedData = (operation.getResult()).getJSONArray("data");

                    availableMedia = new ArrayList<>();

                    for(int i = 0; i < returnedData.length(); i++) {
                        JSONObject skyDriveFolder = returnedData.getJSONObject(i);

                        String skyDriveFolderType = skyDriveFolder.getString("type");

                        if (skyDriveFolderType.toLowerCase().equals("folder") || skyDriveFolderType.toLowerCase().equals("album")) {
                            readSkyDriveFolder(skyDriveFolder.getString("id"));
                        }
                    }
                } catch (JSONException e) {
                    TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
                    txtMessage.setText("Error: " + e.getMessage());
                }
            }

            public void onError(LiveOperationException exception, LiveOperation operation) {
                TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
                txtMessage.setText("Error: " + exception.getMessage());
            }
        });
    }

    private void readSkyDriveFolder(String folderID) {
        LiveConnectClient client = LiveLoginActivity.client;
        client.getAsync(folderID + "/files", new LiveOperationListener() {
            public void onComplete(LiveOperation operation) {
                JSONArray returnedData;

                TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
                txtMessage.setText("Reading OneDrive folder...");

                try {
                    returnedData = (operation.getResult()).getJSONArray("data");

                    for(int i = 0; i < returnedData.length(); i++) {
                        JSONObject mediaFile = returnedData.getJSONObject(i);

                        String mediaFileName = mediaFile.getString("name");

                        if (mediaFileName.toLowerCase().endsWith(".mp3") || mediaFileName.toLowerCase().endsWith(".mp4")) {
                            String mediaURL = mediaFile.getString("source");
                            String mediaDuration = (mediaFile.has("duration")) ? mediaFile.getString("duration") : null;
                            int mediaLength = (mediaDuration != null && mediaDuration.length() > 0) ? Integer.parseInt(mediaDuration) : 0;
                            String mediaThumbnail = (mediaFile.has("picture")) ? mediaFile.getString("picture") : "";

                            availableMedia.add(new MediaItem(mediaFileName, mediaURL, mediaLength, mediaThumbnail));
                        }
                    }

                    txtMessage.setText("There are " + (availableMedia.size()) + " media files found.");

                    ListMediaItemsAdapter adapter = new ListMediaItemsAdapter(getApplicationContext(), availableMedia);

                    ListView listMedia = (ListView) findViewById(R.id.listMedia);
                    listMedia.setAdapter(adapter);

                    listMedia.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Uri uri = Uri.parse(availableMedia.get(position).getmMediaSourceUrl());

                            VideoView videoPlayer = (VideoView)findViewById(R.id.videoView2);
                            if (!uri.equals(videoPlayer.getTag(R.id.tag_first))) {

                                videoPlayer.setBackgroundColor(Color.TRANSPARENT);
                                if (availableMedia.get(position).getmMediaFileName().toLowerCase().endsWith(".mp3")) {
                                    videoPlayer.setBackgroundColor(Color.BLACK);
                                }

                                videoPlayer.setVideoURI(uri);
                                videoPlayer.setTag(R.id.tag_first, uri);
                                videoPlayer.setTag(R.id.is_video_looping, false);
                            }

                            videoPlayer.start();

                            btnPlay.setText("❙❙");

                            updateProgressBar();
                        }

                    });
                } catch (JSONException e) {
                    txtMessage.setText("Error: " + e.getMessage());
                }
            }

            public void onError(LiveOperationException exception, LiveOperation operation) {
                TextView txtMessage = (TextView) findViewById(R.id.txtMessage);
                txtMessage.setText("Error: " + exception.getMessage());
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menuPrivacyPolicy:
                createAlertWithOKOnly(R.string.message_privacy_policy_title, R.string.message_privacy_policy_content);
                return true;
            case R.id.menuLogout:
                LiveAuthClient authenticationClient = LiveLoginActivity.authenticationClient;
                authenticationClient.logout(new LiveAuthListener() {
                    @Override
                    public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
                        if (status == LiveStatus.UNKNOWN) {
                            Intent loginPage = new Intent(PlayerActivity.this, LiveLoginActivity.class);
                            startActivity(loginPage);
                        }
                    }

                    @Override
                    public void onAuthError(LiveAuthException exception, Object userState) {
                        createAlertWithOKOnly(R.string.message_logout_fail_title, R.string.message_logout_fail_content);
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void createAlertWithOKOnly(int titleID, int messageID)
    {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
        builder.setMessage(messageID)
                .setTitle(titleID);
        builder.setPositiveButton(R.string.dialog_box_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }
}
