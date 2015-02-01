package gclproject.onesong;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveStatus;

import java.util.Arrays;

public class LiveLoginActivity extends Activity {

    // UI references
    private View viewProgress;
    private View viewLoginForm;

    // OneDrive clients (for authentication, connect, etc.)
    static LiveAuthClient authenticationClient;
    static LiveConnectClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_login);

        // Setup UI
        viewProgress = findViewById(R.id.login_progress);
        viewLoginForm = findViewById(R.id.login_form);
        Button btnSignIn = (Button) findViewById(R.id.sign_in_button);

        ActionBar actionBar = getActionBar();
        if (actionBar != null)
        {
            actionBar.hide();
        }

        // Setup OneDrive authentication client
        authenticationClient = new LiveAuthClient(this, Config.CLIENT_ID);

        // Sign in button
        btnSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show progress loading ring
                showProgress(true);

                // Show progress loading message
                TextView txtLoginMessage = (TextView) findViewById(R.id.txtLoginMessage);
                txtLoginMessage.setVisibility(View.VISIBLE);
                txtLoginMessage.setText(R.string.message_login_loading);

                // Login to OneDrive
                authenticationClient.login(LiveLoginActivity.this, Arrays.asList(Config.SCOPES), new LiveAuthListener() {
                    @Override
                    public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
                        if (status == LiveStatus.CONNECTED) {
                            client = new LiveConnectClient(session);
                            Intent playerPage = new Intent(LiveLoginActivity.this, PlayerActivity.class);
                            startActivity(playerPage);
                        } else {
                            client = null;
                            TextView txtLoginMessage = (TextView) findViewById(R.id.txtLoginMessage);
                            txtLoginMessage.setText(R.string.message_login_complete_with_error + status.toString());
                        }
                    }

                    @Override
                    public void onAuthError(LiveAuthException exception, Object userState) {
                        client = null;
                        TextView txtLoginMessage = (TextView) findViewById(R.id.txtLoginMessage);
                        txtLoginMessage.setText(R.string.message_login_fail + exception.getMessage());
                    }
                });
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            viewProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            viewProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    viewProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            viewProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        viewLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}



