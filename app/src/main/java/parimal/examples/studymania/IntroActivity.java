package parimal.examples.studymania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import Fragments.IntroScreenOne;
import Fragments.IntroScreenThree;
import Fragments.IntroScreenTwo;

public class IntroActivity extends AppCompatActivity {

    //get reference to intro_viewpager and fragment_number_tv
    ViewPager intro_viewpager;
    TextView fragment_no_tv;
    int total=3,count=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //hide action bar for splash screen
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //inflate intro_viewpager
        intro_viewpager=(ViewPager)findViewById(R.id.intro_viewpager);
        //set adapter for intro viewpager
        intro_viewpager.setAdapter(new IntroAdapter(getSupportFragmentManager()));
        //inflate fragment number textview
        fragment_no_tv=(TextView)findViewById(R.id.fragment_number_tv);
        //set text for fragment_no_tv
        fragment_no_tv.setText(count+"/"+total);
        //change fragment no on scrolling
        changeFragmentNumber();
    }

    class IntroAdapter extends FragmentPagerAdapter
    {

        //default constructor
        public IntroAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        //get various intro fragments inflated at different positions
        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return new IntroScreenOne();
                case 1:
                    return new IntroScreenTwo();
                case 2:
                    return new IntroScreenThree();
                default:
                    return new IntroScreenOne();
            }
        }

        //total number of intro fragments is 3
        @Override
        public int getCount() {
            return 3;
        }
    }
    //change fragment number on scrolling
    private void changeFragmentNumber()
    {
        intro_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //change the fragment number according to selected page
            @Override
            public void onPageSelected(int position) {
                count=0;
                count=position+1;
                fragment_no_tv.setText(count+"/"+total);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //close the application if back button is pressed
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
