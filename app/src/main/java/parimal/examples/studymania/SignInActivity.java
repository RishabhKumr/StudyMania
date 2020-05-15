package parimal.examples.studymania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    private SignInButton g_sign_in_button;
    private LoginButton fb_login_button;
    private EditText email_login_editext,password_login_edittext;
    private TextView register_textview;
    private Button email_login_button;
    CallbackManager callbackManager;
    private static final int RC_SIGN_IN=1;
    public static final String TAG="SignInActivity";
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //inflate the fb signin,firebase and gmail signin buttons.
        g_sign_in_button=(SignInButton)findViewById(R.id.g_sign_in_button);
        fb_login_button=(LoginButton)findViewById(R.id.fb_login_button);
        email_login_button=(Button)findViewById(R.id.email_login_button);

        //get an instance of firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();


        //method call for google signin
        signInWithGoogle();
        //method call to sign with firebase email and password
        signInWithFirebase();
        //method to call for fb signin
        //signInWithFb();
    }

    public void signInWithGoogle()
    {
        //create a new GSO with email request.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        //create a new google client for google sign in
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        //set on click listener on google signin button
        g_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    public void signIn()
    {
        //open a new intent and retrieve the results of google sign in
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //get the google signed in user details from this method
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        //transfer the results of fb sign in to callback manager
        else
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
//

    //get the google signed in user's account details,store it in firestore and move to the MainActivity after login
    private void handleSignInResult(@org.jetbrains.annotations.NotNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //get references to user profile
                                firebaseUser = firebaseAuth.getCurrentUser();
                                final String name = firebaseUser.getDisplayName();
                                final String email = firebaseUser.getEmail();
                                final String imageurl = firebaseUser.getPhotoUrl().toString();
                                final String imageurl1="default";
                                final String uid = firebaseUser.getUid();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("UserId", uid);
                                hashMap.put("UserName", name);
                                hashMap.put("Email", email);
                                if(imageurl!=null) {
                                    hashMap.put("ImageUrl", imageurl);
                                }
                                else
                                {
                                    hashMap.put("ImageUrl",imageurl1);
                                }

                                //add userdata in firestore
                                firebaseFirestore.collection("Users")
                                        .document(uid)
                                        .set(hashMap, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(),"Sign in successfully",Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Sign in failed", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }
                    });
        }
        catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(),"Authentication Failed",Toast.LENGTH_LONG).show();
        }
    }

    //method for fb signin
    public void signInWithFb()
    {
        //create a callback manager and register it to receive user account details.
        callbackManager=CallbackManager.Factory.create();
        fb_login_button.setReadPermissions(Arrays.asList("email","public_profile"));
        fb_login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            //start the MainActivity after login
            public void onSuccess(LoginResult loginResult) {
                startActivity(new Intent(SignInActivity.this,MainActivity.class));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    //method to sign in using firebase email
    public void signInWithFirebase()
    {
        email_login_editext=(EditText)findViewById(R.id.email_login_edittext);
        password_login_edittext=(EditText)findViewById(R.id.password_login_edittext);
        register_textview=(TextView)findViewById(R.id.register_textview);
        email_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=email_login_editext.getText().toString();
                String password=password_login_edittext.getText().toString();
                //check for empty email edittext
                if(TextUtils.isEmpty(email))
                {
                    email_login_editext.setError("Please enter your Email!");
                    return;
                }
                //check for empty password edittext
                if(TextUtils.isEmpty(password))
                {
                    password_login_edittext.setError("Please enter your Password!");
                    return;
                }

                //authenticate using firebase email and password login
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SignInActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else
                        {
                            Toast.makeText(SignInActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //open the register button when register textview is clicked
        register_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,RegisterActivity.class));
            }
        });

    }

    //close the application if back button is pressed
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
