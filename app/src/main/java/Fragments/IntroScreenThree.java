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

import parimal.examples.studymania.R;
import parimal.examples.studymania.SignInActivity;

//fragment to show intro slide3
public class IntroScreenThree extends Fragment {

    SharedPreferences sharedPreferences;
    Button finish_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_intro_screen_three, container, false);

        //inflate finish button and set listener on it
        finish_button=view.findViewById(R.id.finish_button_intro3);
        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open sign in activity for user to login
                startActivity(new Intent(getContext(), SignInActivity.class));
                //set shared preferences to 3 on completion
                sharedPreferences=getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("introstatus",3);
                editor.commit();
            }
        });
        return view;
    }
}
