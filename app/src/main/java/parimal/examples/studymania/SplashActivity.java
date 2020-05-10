package parimal.examples.studymania;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class SplashActivity extends AppCompatActivity {
    GoogleSignInAccount gAccount;
    AccessToken accessToken;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //hide action bar for splash screen
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //create new handler for SplashActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //check for any logged in user and start the MainActivity class directly
                if(gAccount!=null||isLoggedIn) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                //else start the SignInActivity class for user to login
                else
                {
                    startActivity(new Intent(SplashActivity.this,SignInActivity.class));
                }
            }
        },3000);//open the new Activity after a delay of 3s
    }

    @Override
    protected void onStart() {
        super.onStart();
        //search for any already logged in user
        gAccount= GoogleSignIn.getLastSignedInAccount(this);
        accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();
    }
}
