package parimal.examples.studymania;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class LevelSelectDialogFragment extends DialogFragment
{
    //get the position of item selected in dialog
    int position=0;

    //interface to be implemented by TestCreateActivity class and defining the actions on positive and neg button clicked
    public interface LevelSelectListener{
        void onPositiveButtonClicked(String[] level_list,int Position);
        void onNegativeButtonClicked();
    }

    //interface listener to be attached to an activity
    LevelSelectDialogFragment.LevelSelectListener lListener;

    //check whether the given activity implements the interface and its methods.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            lListener=(LevelSelectDialogFragment.LevelSelectListener)context;
        }
        catch (Exception e)
        {
            throw new ClassCastException(getActivity().toString()+"must implement LevelSelectListener");
        }
    }

    //method to create a new dialog.
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //string array including list of subjects from string.xml class
        final String[] level_list = getActivity().getResources().getStringArray(R.array.level_list);

        //create alertdialog with singlechoice option
        builder.setTitle("Select Difficulty Level")
                .setSingleChoiceItems(level_list, position, new DialogInterface.OnClickListener() {
                    @Override
                    //set the position to the selected item
                    public void onClick(DialogInterface dialog, int which) {
                        position = which;
                    }
                })
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //call method from MainActivity class for specific action.
                        lListener.onPositiveButtonClicked(level_list, position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //call method from MainActivity class for specific action.
                        lListener.onNegativeButtonClicked();
                    }
                });
        return builder.create();
    }
}
