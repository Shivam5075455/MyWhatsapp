package com.example.mywhatsapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.mywhatsapp.Adapters.ChatDetailAdapter;
import com.example.mywhatsapp.Models.ChatDetailModel;
import com.example.mywhatsapp.R;
import com.example.mywhatsapp.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ChatDetailActivity extends AppCompatActivity {

    private static final String TAG = "mydebug";
    ActivityChatDetailBinding binding;
    ChatDetailAdapter chatDetailAdapter;
    ChatDetailModel chatDetailModel;
    List<ChatDetailModel> chatDetailModelsList;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    String senderId;
    String senderRoom = "";
    String receiverRoom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://mywhatsapp-d2663-default-rtdb.firebaseio.com/").getReference("Users");
        String receiverId = getIntent().getStringExtra("receiverId");
        senderId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        Log.d(TAG, "receiverId: " + receiverId);
        chatDetailModelsList = new ArrayList<>();
        chatDetailAdapter = new ChatDetailAdapter(this, chatDetailModelsList, receiverId);
        binding.rvChatDetails.setAdapter(chatDetailAdapter);
//        set layout manager
        binding.rvChatDetails.setLayoutManager(new LinearLayoutManager(this));

        getUserInfo();
        onclickListener();
        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;
        filterMessages();
    }

    private void onclickListener() {
        binding.imgBack.setOnClickListener(v -> finish());
        binding.imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.etWriteMessage.getText().toString();
                if (!message.isEmpty()) {

                    chatDetailModel = new ChatDetailModel(senderId, message);
                    chatDetailModel.setTimestamp(new Date().getTime());
                    chatting(message);
                    binding.etWriteMessage.setText("");

                }

            }
        });
    }


    private void getUserInfo() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String image = intent.getStringExtra("image");

        binding.tvUserName.setText(name);
        Glide.with(this).load(image).into(binding.imgProfile);
    }


    public void chatting(String message) {
//        store the messages of both sender and receiver
//        sender message
        databaseReference = FirebaseDatabase.getInstance("https://mywhatsapp-d2663-default-rtdb.firebaseio.com/").getReference("chats")
                .child(senderRoom);
        databaseReference.push().setValue(new ChatDetailModel(auth.getUid(), message, new Date().getTime()));
//      receiver message
        databaseReference = FirebaseDatabase.getInstance("https://mywhatsapp-d2663-default-rtdb.firebaseio.com/").getReference("chats")
                .child(receiverRoom);
        databaseReference.push().setValue(new ChatDetailModel(auth.getUid(), message, new Date().getTime()));
    }

    public void filterMessages() {
        databaseReference = FirebaseDatabase.getInstance("https://mywhatsapp-d2663-default-rtdb.firebaseio.com/").getReference("chats")
                .child(senderRoom);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatDetailModelsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatDetailModel chatDetailModel = dataSnapshot.getValue(ChatDetailModel.class);
                    assert chatDetailModel != null;
                    chatDetailModel.setMessageId(dataSnapshot.getKey());
                    Log.d(TAG, "onDataChange: message id = " + chatDetailModel.getMessageId());
                    chatDetailModelsList.add(chatDetailModel);
                }
                chatDetailAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG,"onCancelled: "+error.getMessage());
            }
        });
    }

}