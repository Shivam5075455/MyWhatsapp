package com.example.mywhatsapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mywhatsapp.R;
import com.example.mywhatsapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;

    final static String TAG = "mydebug";

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        binding.btnSignup.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                signupwithEmail(email, password);
            }
        });


    }

    public void signupwithEmail(String email, String password) {

        if (!binding.etName.getText().toString().isEmpty() && !binding.etEmail.getText().toString().isEmpty() && !binding.etPassword.getText().toString().isEmpty()) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://mywhatsapp-d2663-default-rtdb.firebaseio.com/").getReference("users").child(uid);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("uid", uid);
                    hashMap.put("name", binding.etName.getText().toString());
                    hashMap.put("email", binding.etEmail.getText().toString());
                    hashMap.put("password", binding.etPassword.getText().toString());
                    hashMap.put("image", "");
                    hashMap.put("status", "Hey there! I am using whatsapp");
                    databaseReference.setValue(hashMap).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Account created and stored in fireabase");
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Somethind went wrong", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "somethind went wrong");
                        }
                    });
                } else {
                    Toast.makeText(this, "This account is already exist", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "This account is already exist");
                }
            });
        } else {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();


        }
    }
}