package com.example.mywhatsapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mywhatsapp.Activity.LoginActivity;
import com.example.mywhatsapp.R;
import com.example.mywhatsapp.databinding.FragmentSettingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class SettingFragment extends Fragment {



    FragmentSettingBinding binding;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        binding = FragmentSettingBinding.bind(view);


        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://mywhatsapp-d2663-default-rtdb.firebaseio.com/").getReference("users");
        onClickListener();
        getCurrentUserInfo();
        return view;
    }

    public void onClickListener(){
        binding.tvLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });

        binding.constraintLayoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    public void getCurrentUserInfo() {
        if (auth.getCurrentUser() != null) {
            String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

            databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String name = snapshot.child("name").getValue(String.class);
                        String status = snapshot.child("status").getValue(String.class);
                        String image = snapshot.child("image").getValue(String.class);

                        binding.tvUserName.setText(name);
                        binding.tvUserStatus.setText(status);
                        Glide.with(requireContext()).load(image).into(binding.imgProfileImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }
}