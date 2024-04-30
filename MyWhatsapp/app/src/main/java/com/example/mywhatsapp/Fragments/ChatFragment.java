package com.example.mywhatsapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mywhatsapp.Activity.ChatDetailActivity;
import com.example.mywhatsapp.Adapters.ChatAdapter;
import com.example.mywhatsapp.Models.ChatModel;
import com.example.mywhatsapp.R;
import com.example.mywhatsapp.databinding.ChatItemSampleLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class ChatFragment extends Fragment implements ChatAdapter.OnChatItemClickListener {

    final static String TAG = "mydebug";
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    ChatItemSampleLayoutBinding binding;
    TextView tvName;
    List<ChatModel> chatModelList;
    ChatAdapter chatAdapter;
    RecyclerView recyclerViewChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatModelList = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), chatModelList);
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerViewChat = view.findViewById(R.id.recyclerviewChat);

        recyclerViewChat.setAdapter(chatAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewChat.setLayoutManager(linearLayoutManager);
//        binding = ChatItemSampleLayoutBinding.bind(view);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://mywhatsapp-d2663-default-rtdb.firebaseio.com/");
        getUserInfo();
        chatAdapter.setOnChatItemClickListener(this);

        return view;
    }//end of onCreateView

    @Override
    public void onChatItemClick(int position) {
        ChatModel clickChatModelItem = chatModelList.get(position);
        Intent intent = new Intent(getContext(), ChatDetailActivity.class);
        intent.putExtra("name", clickChatModelItem.getUsername());
        intent.putExtra("image", clickChatModelItem.getImage());
        intent.putExtra("receiverId", clickChatModelItem.getReceiverId());
        startActivity(intent);
    }

    public void getUserInfo() {
        Log.d(TAG, "getUserInfo: started");
        DatabaseReference databaseReference = firebaseDatabase.getReference("users");
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        Log.d(TAG, "getUserInfo: current user id: " + uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userDataSnapshot : snapshot.getChildren()) {
                        try {
                            Log.d(TAG, "onDataChange: alluserid: " + userDataSnapshot.getKey());
                            if (!uid.equals(userDataSnapshot.getKey())) {
                                String username = userDataSnapshot.child("name").getValue(String.class);
                                String image = userDataSnapshot.child("image").getValue(String.class);
                                String receiverId = userDataSnapshot.child("uid").getValue(String.class);
                                Log.d(TAG, "onDataChange: receiverId: " + receiverId);
//                                String lastMessage = userDataSnapshot.child("lastMessage").getValue(String.class);
                                String lastMessage = "Last Message";
                                if (username != null && image != null) {
                                    chatModelList.add(new ChatModel(username, image,lastMessage,receiverId));
                                }
                            }

                        } catch (Exception e) {
                            Log.d(TAG, "onDataChange: " + e.getMessage());
                        }
                    }
                    // Notify the adapter after adding data
                    chatAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "getUserInfo: snapshot is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

}