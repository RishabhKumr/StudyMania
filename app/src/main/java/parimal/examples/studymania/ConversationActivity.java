package parimal.examples.studymania;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import Model.Chats;
import Model.Users;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.appevents.codeless.internal.UnityReflection.sendMessage;

public class ConversationActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    CircleImageView pro_image;
    TextView userName;

    //button to send a message
    ImageButton buttonSend;
    //editText to type a message
    EditText msg_send;
    ConversationAdapter conversationAdapter;
    //list to store messages between users
    List<Chats> messageList;

    RecyclerView recyclerView;

    //to check whether user has seen a message
    EventListener seenLis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        //get toolbar to display other user's name and profile image
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            //go back to parent activity
            public void onClick(View v) {
                startActivity(new Intent(ConversationActivity.this, ChatActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        //get recycler view to display messages and display messages from bottom to top fashion.
        recyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //get references to views
        pro_image = (CircleImageView) findViewById(R.id.prof_image_chat);
        userName = (TextView) findViewById(R.id.userName_chat);
        buttonSend = (ImageButton) findViewById(R.id.button_msg_send);
        msg_send = (EditText) findViewById(R.id.msg_to_send_edt);

        //get other user's id from calling intent
        final String userId = getIntent().getStringExtra("userid");

        //add listener to send button
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msg_send.getText().toString();
                if (!msg.equals("")) {
                    //call sendMessage()
                    sendMessage(firebaseUser.getUid(), userId, msg);
                } else {
                    Toast.makeText(getApplicationContext(), "You cannot send an empty message", Toast.LENGTH_LONG).show();
                }
                msg_send.setText("");
            }
        });

        //get reference to firebase user and firestore
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //set username and profile image in toolbar
        firebaseFirestore.collection("Users").document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        Users user = documentSnapshot.toObject(Users.class);
                        userName.setText(user.getUserName());
                        if ("default".equals(user.getImageUrl())) {
                            pro_image.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            Glide.with(getApplicationContext()).load(user.getImageUrl()).into(pro_image);
                        }
                        //call read_message()
                        read_message(firebaseUser.getUid(), userId, user.getImageUrl());
                    }
                });

        //call seenMessage() to check whether a message is seen or not
        seenMessage(userId);
    }

    //to check if a message has been seen by other user
    private void seenMessage(final String userId) {

        firebaseFirestore.collection("Chats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Chats chat = doc.toObject(Chats.class);

                            if (chat.getReceiverId().equals(firebaseUser.getUid()) && chat.getSenderId().equals(userId)) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("isSeen", "true");
                                doc.getReference().update(hashMap);
                            }
                        }
                    }
                });
    }

    //send a message from current user to other user
    private void sendMessage(String senderId,String receiverId,String message)
    {

        String timestamp=String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("senderId",senderId);
        hashMap.put("receiverId",receiverId);
        hashMap.put("message",message);
        hashMap.put("isSeen","false");
        hashMap.put("timestamp",timestamp);

        firebaseFirestore.collection("Chats").add(hashMap);
    }

    //read messages from database and store them in messageList.
    private void read_message(final String selfId, final String userId, final String imageUrl)
    {
        messageList=new ArrayList<>();
        firebaseFirestore.collection("Chats").orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        messageList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Chats chat = doc.toObject(Chats.class);
                            if (selfId.equals(chat.getSenderId()) && userId.equals(chat.getReceiverId())
                                    || userId.equals(chat.getSenderId()) && selfId.equals(chat.getReceiverId())) {
                                messageList.add(chat);
                            }
                            //create an adapter from recycler view and set recycler view to display from last message when opened
                            conversationAdapter = new ConversationAdapter(ConversationActivity.this, messageList, imageUrl);
                            recyclerView.setAdapter(conversationAdapter);
                            recyclerView.smoothScrollToPosition(conversationAdapter.getItemCount());
                        }
                    }
                });
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
