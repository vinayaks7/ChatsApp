package com.example.chatsapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.example.chatsapp.Adapters.MessagesAdapter;
import com.example.chatsapp.Models.Message;
import com.example.chatsapp.R;
import com.example.chatsapp.databinding.ActivityChatBinding;
import com.example.chatsapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;

    String senderRoom, receiverRoom;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String senderuid;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog= new ProgressDialog(this);
        dialog.setMessage("Uploading image........");
        dialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        messages = new ArrayList<>();


        String name = getIntent().getStringExtra("name");
        String receiveruid = getIntent().getStringExtra("uid");
       senderuid = FirebaseAuth.getInstance().getUid();


        senderRoom = senderuid + receiveruid;
        receiverRoom = receiveruid + senderuid;


        adapter = new MessagesAdapter(this, messages, senderRoom, receiverRoom);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);


        database = FirebaseDatabase.getInstance();
        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessage(snapshot1.getKey());
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = binding.messageBox.getText().toString();
                if(!messageTxt.isEmpty()){
                Date date = new Date();

                Message message = new Message(messageTxt, senderuid, (int) date.getTime());
//                Toast.makeText(ChatActivity.this, messageTxt, Toast.LENGTH_SHORT).show();
                binding.messageBox.setText("");

                messages.add(message);
                adapter.updateItems(messages);
                String randomKey = database.getReference().push().getKey();


                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg", message.getMessage());
                lastMsgObj.put("lastMsgTime", date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .child(randomKey)
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                });
            }
            }
        });


        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,25);
            }
        });


        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 25) {
            if(data != null) {
                if (data.getData()!=null){

                        Uri selectedImage=data.getData();
                        Calendar calendar=Calendar.getInstance();
                        StorageReference reference= storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
                        dialog.show();
                        reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                dialog.dismiss();
                                if (task.isSuccessful()){
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String filePath=uri.toString();

                                            String messageTxt = binding.messageBox.getText().toString();
                                            Date date = new Date();
                                            Message message = new Message(messageTxt, senderuid, (int) date.getTime());
                                            message.setMessage("photo");
                                            message.setImageUrl(filePath);
                                            binding.messageBox.setText("");


                                            String randomKey = database.getReference().push().getKey();

                                            HashMap<String, Object> lastMsgObj = new HashMap<>();
                                            lastMsgObj.put("lastMsg", message.getMessage());
                                            lastMsgObj.put("lastMsgTime", date.getTime());

                                            database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                            database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                            database.getReference().child("chats")
                                                    .child(senderRoom)
                                                    .child("messages")
                                                    .child(randomKey)
                                                    .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    database.getReference().child("chats")
                                                            .child(receiverRoom)
                                                            .child("messages")
                                                            .child(randomKey)
                                                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    });
                                                }
                                            });



//                                            Toast.makeText(ChatActivity.this,filePath, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }
                        });

                }
            }
        }
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.chat_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}