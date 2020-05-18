package parimal.examples.studymania;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class TestCreateActivity extends AppCompatActivity implements LevelSelectDialogFragment.LevelSelectListener {

    private EditText total_marks_edittext;
    private Button submit_button;
    String levelselected="";
    RadioGroup totalqns_rg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_create);

        total_marks_edittext=(EditText)findViewById(R.id.total_marks_edittext);
        submit_button=(Button)findViewById(R.id.test_create_submit);


        //submit button to submit the no of questions and total marks and open a dialog to set difficulty level
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get total number of qns by calling getRadioButtonText Method
                int selected_no_of_qns=Integer.valueOf(getRadioButtonText());
                int total_marks=Integer.valueOf(total_marks_edittext.getText().toString());
                Log.d("noofqns", "onClick: "+selected_no_of_qns);
                //method to evaluate marks per question
                double marks=findMarksPerQuestion(selected_no_of_qns,total_marks);
                //create a new object of SubjectSelectDialogFragment and show the created dialog
                DialogFragment levelSelectDialog=new LevelSelectDialogFragment();
                levelSelectDialog.setCancelable(false);
                levelSelectDialog.show(getSupportFragmentManager(),"LevelSelectDialog");
            }
        });
    }

    //get total no of qnqs
    private String getRadioButtonText()
    {
        //inflate radiogroup
        totalqns_rg=(RadioGroup)findViewById(R.id.totalqns_rg);
        //get selected radiobutton id
        int selectedId=totalqns_rg.getCheckedRadioButtonId();
        RadioButton selected_rb=(RadioButton)findViewById(selectedId);
        //return total number of qns
        return selected_rb.getText().toString();
    }

    //find marks per question
    private double findMarksPerQuestion(int totalqns,int totalmarks)
    {
        double marksperquestion=(double)totalmarks/(double)totalqns;
        return marksperquestion;
    }

    //implement methods from LevelSelectListener interface positive button clicked
    @Override
    public void onPositiveButtonClicked(String[] level_list, int Position) {
        //find the level selected from dialog box and pass it to an intent
        levelselected=level_list[Position];
        Intent intent1=new Intent(TestCreateActivity.this,TestDisplayActivity.class);
        intent1.putExtra("level",levelselected);
        //start the WebActivity class to display google.com
        startActivity(intent1);

    }

    //implement methods from LevelSelectListener interface negative button clicked
    @Override
    public void onNegativeButtonClicked() {

    }
}
