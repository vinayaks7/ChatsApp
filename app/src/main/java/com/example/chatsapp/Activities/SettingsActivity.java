package com.example.chatsapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatsapp.Models.User;
import com.example.chatsapp.databinding.ActivityChatBinding;
import com.example.chatsapp.databinding.ActivityMainBinding;
import com.example.chatsapp.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        String senderuid = FirebaseAuth.getInstance().getUid();


        database.getReference().child("users").child(senderuid).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = (String) snapshot.getValue();
                binding.editTextTextPersonName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        database.getReference().child("users").child(senderuid).child("profileImage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String pimage = (String) snapshot.getValue();
                Log.i("Image", pimage);

                Glide
                        .with(SettingsActivity.this)
                        .load(pimage)
                        .into(binding.imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 25);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {

                    Uri selectedImage = data.getData();
                    binding.imageView.setImageURI(selectedImage);
                    uploadToFirebase(selectedImage);

                }
            }
        }


    }

    private void uploadToFirebase(Uri selectedImage) {


        if (selectedImage != null) {


            Log.i("Heyyy", "GGGGGGg");
            try {
                StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
                Log.i("Heyyy", "trhtrh");
                reference.putFile(selectedImage).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {

                            String imageUrl = uri.toString();
                            String uid = auth.getUid();
                            String phone = auth.getCurrentUser().getPhoneNumber();
                            String name = binding.editTextTextPersonName.getText().toString();

                            User user = new User(uid, name, phone, imageUrl);

                            database.getReference().child("users")
                                    .child(uid)
                                    .setValue(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
//                                                dialog.dismiss();
                                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                        });
                    }


                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {


            Log.i("yyy", "GGGGGGg");

//            String uid = auth.getUid();
//            String phone = auth.getCurrentUser().getPhoneNumber();
//            String name=binding.editTextTextPersonName.getText().toString();
//
//
//            User user = new User(uid, name, phone, "No image");
//
//            database.getReference()
//                    .child("users")
//                    .child(uid)
//                    .setValue(user)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                           Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    });
        }


    }
}