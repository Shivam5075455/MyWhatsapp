package com.example.mywhatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mywhatsapp.Activity.ChatDetailActivity;
import com.example.mywhatsapp.Models.ChatModel;
import com.example.mywhatsapp.R;
import com.example.mywhatsapp.databinding.ChatItemSampleLayoutBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    List<ChatModel> chatModelList;
    private static OnChatItemClickListener chatItemClickListener;

    public ChatAdapter(Context context, List<ChatModel> chatModelList) {
        this.context = context;
        this.chatModelList = chatModelList;
    }

    //    interface to set onclick on items
    public interface OnChatItemClickListener {
        void onChatItemClick(int position);
    }

    public void setOnChatItemClickListener(OnChatItemClickListener ChatItemClickListener) {
        this.chatItemClickListener = ChatItemClickListener;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_sample_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        ChatModel chatModel = chatModelList.get(position);
        holder.binding.tvName.setText(chatModel.getUsername());
        holder.binding.tvLastMessage.setText(chatModel.getLastmessage());
        holder.binding.tvReceiverId.setText(chatModel.getReceiverId());
        Glide.with(context).load(chatModel.getImage()).into(holder.binding.profileImage);
        String receiverId = chatModel.getReceiverId();

    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

//    ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ChatItemSampleLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatItemSampleLayoutBinding.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(chatItemClickListener != null){
                int position = getAdapterPosition();
                if(position!=RecyclerView.NO_POSITION){
                    chatItemClickListener.onChatItemClick(position);
                }
            }
        }
    }

}
