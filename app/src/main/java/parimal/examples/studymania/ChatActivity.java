package parimal.examples.studymania;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import Fragments.ChatFragment;
import Fragments.UserFragment;
import Model.Chats;

public class ChatActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //set title in toolbar as "StudyMania"
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("StudyMania");

        //get tablayout and viewpager
        final TabLayout tabLayout=(TabLayout)findViewById(R.id.tab_layout);
        final ViewPager viewPager=(ViewPager)findViewById(R.id.view_pager);

        //get current user instance and instance for firebasefirestore
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();

        //check for unread messages
        firebaseFirestore.collection("Chats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        //initialize viewpageradapter for viewpager
                        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                        int unreadMsg = 0;
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Chats message = doc.toObject(Chats.class);
                            if (message.getReceiverId().equals(firebaseUser.getUid()) && !message.getIsSeen().equals("true")) {
                                unreadMsg++;
                            }
                        }
                        //add fragemnts to viewpager
                        if (unreadMsg == 0) {
                            viewPagerAdapter.addFragment(new ChatFragment(), "Chats");
                        } else {
                            viewPagerAdapter.addFragment(new ChatFragment(), "(" + unreadMsg + ") Chats");
                        }
                        viewPagerAdapter.addFragment(new UserFragment(), "Users");

                        //set adapter to viewPager
                        viewPager.setAdapter(viewPagerAdapter);
                    }
                });
        //set tablayout with viewpager
        tabLayout.setupWithViewPager(viewPager);
    }

    //ViewpagerAdapter to set items in viewpager
    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fmanager)
        {
            super(fmanager);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        public int getCount()
        {
            return fragments.size();
        }

        //add a new fragment to view pager
        public void addFragment(Fragment fragment,String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
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
