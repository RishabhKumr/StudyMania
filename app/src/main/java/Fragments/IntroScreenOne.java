package Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import parimal.examples.studymania.MainActivity;
import parimal.examples.studymania.R;
import parimal.examples.studymania.SignInActivity;

//fragment to show intro slide1
public class IntroScreenOne extends Fragment {

    Button skip_button;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_intro_screen_one, container, false);


        //inflate skip button
        skip_button=view.findViewById(R.id.skip_button);

        //set listener on skip button
        skip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            //move to mainactivity class
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SignInActivity.class));
                //set shared preferences to 3 on skip
                sharedPreferences=getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("introstatus",3);
                editor.commit();
            }
        });
        return view;
    }
}
