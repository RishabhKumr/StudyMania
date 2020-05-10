package parimal.examples.studymania;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity  implements SubjectSelectDialogFragment.SubjectSelectListener {
    private Button test_create_button;
    //to get the selected subject
    String subjectselected="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //button to create test
        test_create_button=(Button)findViewById(R.id.test_create_button);
        //set onclicklistener on button
        test_create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a new object of SubjectSelectDialogFragment and show the created dialog
                DialogFragment subjectSelectDialog=new SubjectSelectDialogFragment();
                subjectSelectDialog.setCancelable(false);
                subjectSelectDialog.show(getSupportFragmentManager(),"SubjectSelectDialog");
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
}
