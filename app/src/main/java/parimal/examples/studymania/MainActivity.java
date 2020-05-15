package parimal.examples.studymania;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity  implements SubjectSelectDialogFragment.SubjectSelectListener {
    private Button test_create_button;
    //to get the selected subject
    String subjectselected="";
    private Button logout_button;
    GoogleSignInAccount gAccount;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    private Button chat_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //button to create test
        test_create_button=(Button)findViewById(R.id.test_create_button);
        //button to logout
        logout_button=(Button)findViewById(R.id.logout_button);
        //button to chat
        chat_button=(Button)findViewById(R.id.chat_button);
        //get reference to firebase user and firestore
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();

        //set onclicklistener on test_create_button
        test_create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a new object of SubjectSelectDialogFragment and show the created dialog
                DialogFragment subjectSelectDialog=new SubjectSelectDialogFragment();
                subjectSelectDialog.setCancelable(false);
                subjectSelectDialog.show(getSupportFragmentManager(),"SubjectSelectDialog");
            }
        });

        //set onclicklistener on chat button
        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //move to chatactivity
                startActivity(new Intent(getApplicationContext(),ChatActivity.class));

            }
        });

        //button to logout from app
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseUser!=null)
                {
                    FirebaseAuth.getInstance().signOut();
                }
                startActivity(new Intent(MainActivity.this,SignInActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    //close the application if back button is pressed
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    //implement methods from SubjectSelectListener interface for positive button clicked
    @Override
    public void onPositiveButtonClicked(String[] subject_list, int Position) {
        //get the selected subject name and pass it to an intent.
        subjectselected=subject_list[Position];
        Log.d("main", "onPositiveButtonClicked: "+subjectselected);
        Intent intent=new Intent(MainActivity.this,TestCreateActivity.class);
        intent.putExtra("Subjectname",subjectselected);
        //start the TestCreateActivity to take other details
        startActivity(intent);
    }

    //implement methods from SubjectSelectListener interface negative button clicked
    @Override
    public void onNegativeButtonClicked() {

    }

    //add status of current user to firestore
    private void status(String Status)
    {
        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("Status",Status);
        firebaseFirestore.collection("Users").document(firebaseUser.getUid())
                .update(hashMap);
    }

    //set status
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    //set status
    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
