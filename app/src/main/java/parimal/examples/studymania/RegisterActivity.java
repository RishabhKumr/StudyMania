package parimal.examples.studymania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Script;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText name_reg_edittext,email_reg_editext,password_reg_edittext,passwordconf_reg_editext;
    private Button register_button;
    private TextView login_textview;
    FirebaseAuth firebaseAuth;
    public static Uri imageurl;
    private static String imageurl1="default";
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //inflate the register button
        register_button=(Button)findViewById(R.id.register_button);

        //get an instance of firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        //method call to create new account using firebase email,password method
        createAccountWithEmailPassword();
    }

    public void createAccountWithEmailPassword()
    {
        name_reg_edittext=(EditText)findViewById(R.id.name_reg_editText);
        email_reg_editext=(EditText)findViewById(R.id.email_reg_edittext);
        password_reg_edittext=(EditText)findViewById(R.id.password_reg_edittext);
        passwordconf_reg_editext=(EditText)findViewById(R.id.passwordconf_reg_edittext);
        login_textview=(TextView)findViewById(R.id.login_textview);
        //set listener on register button to create a new account with email and password
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name=name_reg_edittext.getText().toString();
                final String email=email_reg_editext.getText().toString();
                String password=password_reg_edittext.getText().toString();
                String confirmpassword=passwordconf_reg_editext.getText().toString();

                //check for empty name edittext
                if(TextUtils.isEmpty(name))
                {
                    name_reg_edittext.setError("Please enter your Name!");
                    return;
                }
                //check for empty email edittext
                if (TextUtils.isEmpty(email))
                {
                    email_reg_editext.setError("Please enter your Email!");
                    return;
                }
                //check for empty password edittext
                if(TextUtils.isEmpty(password))
                {
                    passwordconf_reg_editext.setError("Please enter your Password!");
                    return;
                }
                //check for empty confirmpassword edittext
                if(TextUtils.isEmpty(confirmpassword))
                {
                    passwordconf_reg_editext.setError("Please Confirm your Password!");
                    return;
                }
                //check if both passwords match
                if(!password.equals(confirmpassword)&&!TextUtils.isEmpty(confirmpassword))
                {
                    passwordconf_reg_editext.setError("Passwords do not match,Please enter password again");
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //store data in firestore using hashmap
                            firebaseUser = firebaseAuth.getCurrentUser();
                            String uid = firebaseUser.getUid();
                            imageurl=firebaseUser.getPhotoUrl();
                            Log.d("PHOTOURL", "onComplete: "+imageurl);

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

                            //add data to firestore
                            firebaseFirestore.collection("Users")
                                    .document(uid)
                                    .set(hashMap, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Registered Successfully",Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Regitration Failed", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //redirect user to signin activity for login.
        login_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignInActivity.class));
            }
        });
    }
}
