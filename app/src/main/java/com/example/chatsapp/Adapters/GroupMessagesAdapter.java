package com.example.chatsapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatsapp.Models.Message;
import com.example.chatsapp.Models.User;
import com.example.chatsapp.R;
import com.example.chatsapp.databinding.ItemReceiveGroupBinding;
import com.example.chatsapp.databinding.ItemRecieveBinding;
import com.example.chatsapp.databinding.ItemSentBinding;
import com.example.chatsapp.databinding.ItemSentGroupBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupMessagesAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;

    public GroupMessagesAdapter(Context context, ArrayList<Message> messages) {
        this.messages = messages;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent_group, parent, false);
            return new SentViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive_group, parent, false);
            return new RecevierViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        int reactions[] = new int[]{

                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

            ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {


                if (holder.getClass() == SentViewHolder.class) {
                    SentViewHolder viewHolder = (SentViewHolder) holder;


                    viewHolder.binding.feeling.setImageResource(reactions[pos]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                } else {
                    RecevierViewHolder viewHolder = (RecevierViewHolder) holder;
                    viewHolder.binding.feeling.setImageResource(reactions[pos]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                }

                message.setFeeling(pos);

                FirebaseDatabase.getInstance().getReference()
                        .child("public")
                        .child(message.getMessageId()).setValue(message);

                return true; // true is closing popup, false is requesting a new selection
            });

        if (holder.getClass() == SentViewHolder.class) {

            SentViewHolder viewHolder = (SentViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessageId());

            if(message.getMessageId().equals("")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(viewHolder.binding.image);
            }

            FirebaseDatabase.getInstance()
                    .getReference().child("users")
                    .child(message.getSenderId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                User user=snapshot.getValue(User.class);
                                viewHolder.binding.name.setText("@"+user.getName());
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            if (message.getFeeling() >= 0) {
//                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
            viewHolder.binding.image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        }
        else {
            RecevierViewHolder viewHolder = (RecevierViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessageId());


            if(message.getMessageId().equals("")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(viewHolder.binding.image);
            }



            FirebaseDatabase.getInstance()
                    .getReference().child("users")
                    .child(message.getSenderId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                User user=snapshot.getValue(User.class);
                                assert user != null;
                                viewHolder.binding.name.setText(user.getName());
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

            if (message.getFeeling() >= 0) {
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
            viewHolder.binding.image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        }
    }

    public void updateItems(ArrayList<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderId())) {

            return ITEM_SENT;
        } else {

            return ITEM_RECEIVE;
        }

    }


    public class SentViewHolder extends RecyclerView.ViewHolder {

        ItemSentGroupBinding binding;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentGroupBinding.bind(itemView);
        }
    }


    public class RecevierViewHolder extends RecyclerView.ViewHolder {
        ItemReceiveGroupBinding binding;

        public RecevierViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveGroupBinding.bind(itemView);
        }
    }

}
