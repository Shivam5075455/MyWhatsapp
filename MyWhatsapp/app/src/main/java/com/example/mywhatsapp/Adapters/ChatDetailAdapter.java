package com.example.mywhatsapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywhatsapp.Models.ChatDetailModel;
import com.example.mywhatsapp.R;
import com.example.mywhatsapp.databinding.ReceiverMessageSampleLayoutBinding;
import com.example.mywhatsapp.databinding.SenderMessageSampleLayoutBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatDetailAdapter extends RecyclerView.Adapter {
    private Context context;
    List<ChatDetailModel> chatDetailModelList;
    String receiverId;
    final static int VIEW_TYPE_SENDER = 1;
    final static int VIEW_TYPE_RECEIVER = 2;

    public ChatDetailAdapter(Context context, List<ChatDetailModel> chatDetailModelList) {
        this.context = context;
        this.chatDetailModelList = chatDetailModelList;
    }

    public ChatDetailAdapter(Context context, List<ChatDetailModel> chatDetailModelList, String receiverId) {
        this.context = context;
        this.chatDetailModelList = chatDetailModelList;
        this.receiverId = receiverId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_message_sample_layout, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_message_sample_layout, parent, false);
            return new ReceiverViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (chatDetailModelList.get(position).getUserId().equals(FirebaseAuth.getInstance().getUid())) {
            return VIEW_TYPE_SENDER;
        } else {
            return VIEW_TYPE_RECEIVER;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatDetailModel chatDetailModel = chatDetailModelList.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
                String receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you?")
                        .setPositiveButton("Delete for me", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chats");
                                databaseReference.child(senderRoom).child(chatDetailModel.getMessageId()).removeValue();
                            }
                        }).setNegativeButton("Delete for everyone", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chats");
                                databaseReference.child(senderRoom).child(chatDetailModel.getMessageId()).removeValue();
                                Log.d("msgid","SenderRoom: message id = "+chatDetailModel.getMessageId());

                            }
                        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();


                return false;
            }
        });

        if (holder.getItemViewType() == VIEW_TYPE_SENDER) {
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            senderViewHolder.binding.tvSenderMessage.setText(chatDetailModel.getMessage());
            senderViewHolder.binding.tvSenderTime.setText(getFormattedTime(chatDetailModel.getTimestamp()));
        } else {
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
            receiverViewHolder.binding.tvReceiverMessage.setText(chatDetailModel.getMessage());
            receiverViewHolder.binding.tvReceiverTime.setText(getFormattedTime(chatDetailModel.getTimestamp()));
        }
    }

    private String getFormattedTime(long timestamp) {
//      24 hours format ->  dd/MM/yyyy HH:mm
//      12 hours format ->  dd/MM/yyyy HH:mm a
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a");
        return sdf.format(new Date(timestamp));
    }

    @Override
    public int getItemCount() {
        return chatDetailModelList.size();
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        ReceiverMessageSampleLayoutBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ReceiverMessageSampleLayoutBinding.bind(itemView);
        }
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        SenderMessageSampleLayoutBinding binding;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SenderMessageSampleLayoutBinding.bind(itemView);
        }
    }
}
