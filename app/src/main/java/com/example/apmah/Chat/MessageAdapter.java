package com.example.apmah.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apmah.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<MessageClass> userMessageList;
    private FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();

    public MessageAdapter(List<MessageClass> userMessageList){

        this.userMessageList = userMessageList;
    }

    public  class MessageViewHolder extends  RecyclerView.ViewHolder{

        public TextView senderMessageText,receiverMessageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.send_MessageLayout);
            receiverMessageText = itemView.findViewById(R.id.receive_MessageLayout);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layoutmessages,parent,false);
        return new MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        MessageClass messageClass = userMessageList.get(position);

        String fromUser = messageClass.getFrom();
        String messageType = messageClass.getType();

        if(messageType.equals("text")){
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.senderMessageText.setVisibility(View.INVISIBLE);

            if(fromUser.equals(User.getUid())){
                holder.receiverMessageText.setVisibility(View.INVISIBLE);
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setText(messageClass.getMessage());
            }
            else{
                holder.senderMessageText.setVisibility(View.INVISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setText(messageClass.getMessage());
            }
        }

    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }


}
