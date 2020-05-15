package Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import Model.Users;
import parimal.examples.studymania.R;
import parimal.examples.studymania.UserAdapter;

public class UserFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    //list to store users information
    private List<Users> userList;
    //edittext to search for a particular user
    private EditText userSearch;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        //get recycler view
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //initialize userlist
        userList = new ArrayList<>();

        //references to current firebase user and firebase firestore
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //get reference of usersearch and add textchangelistener to it.
        userSearch = view.findViewById(R.id.userSearch_edt);
        userSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //method to search all the users as specified by current user
                searchUser(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //methods call to get all users
        getUsers();

        return view;
    }

    private void searchUser(String s) {
        //create a query according to user search
        Query query = firebaseFirestore.collection("Users").orderBy("UserName")
                .startAt(s)
                .endAt(s + "\uf8ff");

        //add listener to firstore for realtime updates
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("UserFragment", "Listen failed.", e);
                    return;
                }
                userList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Users user = doc.toObject(Users.class);

                    assert user != null;
                    assert firebaseUser != null;

                    //add all users other than current user to userlist
                    if (!firebaseUser.getUid().equals(user.getUserId())) {
                        userList.add(user);
                    }
                }
                //add adapter to recyclerview to display all users excluding current user
                userAdapter = new UserAdapter(getContext(), userList, false);
                recyclerView.setAdapter(userAdapter);
            }
        });
    }

    private void getUsers()
    {
        //get the list of all other users
        firebaseFirestore.collection("Users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("UserFragment", "Listen failed.", e);
                            return;
                        }
                        //only if edittext is empty
                        if (userSearch.getText().toString().equals("")) {
                            userList.clear();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                Users user = doc.toObject(Users.class);

                                assert user != null;
                                assert firebaseUser != null;

                                //add all users other than current user to userlist
                                if (!firebaseUser.getUid().equals(user.getUserId())) {
                                    userList.add(user);
                                }
                            }
                            //add adapter to recyclerview to display all users excluding current user
                            userAdapter = new UserAdapter(getContext(), userList, false);
                            recyclerView.setAdapter(userAdapter);
                        }
                    }
                });
    }
}
