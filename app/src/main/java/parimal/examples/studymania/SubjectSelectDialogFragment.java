package parimal.examples.studymania;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

//Dialog fragment class to create a new alertdialog and set the action for dialog.
public class SubjectSelectDialogFragment extends DialogFragment {

    //get the position of item selected in dialog
    int position=0;

    //interface to be implemented by MainActivity class and defining the actions on positive and neg button clicked
    public interface SubjectSelectListener{
        void onPositiveButtonClicked(String[] subject_list,int Position);
        void onNegativeButtonClicked();
    }

    //interface listener to be attached to an activity
    SubjectSelectListener sListener;

    //check whether the given activity implements the interface and its methods.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            sListener=(SubjectSelectListener)context;
        }
        catch (Exception e)
        {
            throw new ClassCastException(getActivity().toString()+"must implement SubjectSelectListener");
        }
    }

    //method to create a new dialog.
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //string array including list of subjects from string.xml class
        final String[] subject_list = getActivity().getResources().getStringArray(R.array.subject_list);

        //create alertdialog with singlechoice option
        builder.setTitle("Select Subject")
                .setSingleChoiceItems(subject_list, position, new DialogInterface.OnClickListener() {
                    @Override
                    //set the position to the selected item
                    public void onClick(DialogInterface dialog, int which) {
                        position = which;
                    }
                })
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //call method from MainActivity class for specific action.
                        sListener.onPositiveButtonClicked(subject_list, position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //call method from MainActivity class for specific action.
                        sListener.onNegativeButtonClicked();
                    }
                });
        return builder.create();
    }
}
