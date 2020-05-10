package parimal.examples.studymania;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TestCreateActivity extends AppCompatActivity implements LevelSelectDialogFragment.LevelSelectListener {

    private EditText qn_number_edittext;
    private Button qn_plus_button;
    private Button qn_minus_button;
    private EditText total_marks_edittext;
    private Button submit_button;
    String levelselected="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_create);

        qn_number_edittext=(EditText)findViewById(R.id.qn_number_edittext);
        qn_plus_button=(Button)findViewById(R.id.qn_plus_button);
        qn_minus_button=(Button)findViewById(R.id.qn_minus_button);
        total_marks_edittext=(EditText)findViewById(R.id.total_marks_edittext);
        submit_button=(Button)findViewById(R.id.test_create_submit);

        //button to increase number of questions
        qn_plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int no_of_qns=Integer.valueOf(qn_number_edittext.getText().toString())+1;
                qn_number_edittext.setText(""+no_of_qns);
            }
        });

        //button to decrease number of questions
        qn_minus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int no_of_qns = Integer.valueOf(qn_number_edittext.getText().toString());
                if (no_of_qns > 1){
                    no_of_qns-=1;
                    qn_number_edittext.setText(""+no_of_qns);
                }
            }
        });

        //submit button to submit the no of questions and total marks and open a dialog to set difficulty level
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int total_no_of_qns=Integer.valueOf(qn_number_edittext.getText().toString());
                int total_marks=Integer.valueOf(total_marks_edittext.getText().toString());
                //method to evaluate marks per question
                findMarksPerQuestion(total_no_of_qns,total_marks);
                //create a new object of SubjectSelectDialogFragment and show the created dialog
                DialogFragment levelSelectDialog=new LevelSelectDialogFragment();
                levelSelectDialog.setCancelable(false);
                levelSelectDialog.show(getSupportFragmentManager(),"LevelSelectDialog");
            }
        });
    }

    //find marks per question
    private void findMarksPerQuestion(int totalqns,int totalmarks)
    {
        double marksperquestion=(double)totalmarks/(double)totalqns;
        Log.d("test", "findMarksPerQuestion: "+marksperquestion);
    }

    //implement methods from LevelSelectListener interface positive button clicked
    @Override
    public void onPositiveButtonClicked(String[] level_list, int Position) {
        //find the level selected from dialog box and pass it to an intent
        levelselected=level_list[Position];
        Intent intent1=new Intent(TestCreateActivity.this,WebActivity.class);
        intent1.putExtra("level",levelselected);
        //start the WebActivity class to display google.com
        startActivity(intent1);

    }

    //implement methods from LevelSelectListener interface negative button clicked
    @Override
    public void onNegativeButtonClicked() {

    }
}
