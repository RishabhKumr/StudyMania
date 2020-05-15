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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    AccessToken accessToken;
    private boolean isLoggedIn;
    FirebaseUser firebaseUser;

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
                if(firebaseUser!=null)//||isLoggedIn)
                    {
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

        //check for firebase email login user
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        //accessToken = AccessToken.getCurrentAccessToken();
        //isLoggedIn = accessToken != null && !accessToken.isExpired();
    }
}
