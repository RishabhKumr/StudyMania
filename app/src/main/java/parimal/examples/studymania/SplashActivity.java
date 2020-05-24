package parimal.examples.studymania;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    AccessToken accessToken;
    private boolean isLoggedIn;
    FirebaseUser firebaseUser;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //hide action bar for splash screen
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //get introstatus from sharedpreferences
        sharedPreferences=getSharedPreferences("MyPref",MODE_PRIVATE);
        final int introstatus=sharedPreferences.getInt("introstatus",0);

        //create new handler for SplashActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //check for any logged in user and start the MainActivity class directly
                if (firebaseUser != null)//||isLoggedIn)
                {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                //else check for introstatus for intro and then user to login
                else {
                    //if the intro has been displayed once,go direct to signinactivity
                    if (introstatus != 0 && introstatus == 3) {
                        startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    } else {
                        //go to introactivity
                        startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                    }
                }
            }
        },3000);//open the new Activity after a delay of 3s
    }

    @Override
    protected void onStart() {
        super.onStart();

        //check for firebase email login user
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        //accessToken = AccessToken.getCurrentAccessToken();
        //isLoggedIn = accessToken != null && !accessToken.isExpired();
    }
}
