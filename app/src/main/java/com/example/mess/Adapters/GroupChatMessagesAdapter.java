package com.example.mess.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mess.DataUtils;
import com.example.mess.MessageEntity;
import com.example.mess.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatMessagesAdapter extends  RecyclerView.Adapter<GroupChatMessagesAdapter.ViewHolder> {
    private static final int SENDED_MESSAGE = 1;
    private static final int RECEIVED_MESSAGE = 2;
    private DataUtils dataUtils = new DataUtils();
    private DatabaseReference usersRef;
    private List<MessageEntity> messagesList = new ArrayList<>();
    private Context context;

    private FirebaseAuth mAuth;

    public GroupChatMessagesAdapter(List<MessageEntity> messagesList, Context context){
        mAuth = FirebaseAuth.getInstance();
        this.messagesList = messagesList;
        this.context = context;
    }
    @NonNull
    @Override
    public GroupChatMessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDED_MESSAGE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sended, parent, false);
            return new SendedMessageHolde(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatMessagesAdapter.ViewHolder holder, int position) {
        if (holder instanceof SendedMessageHolde){
            SendedMessageHolde sendedMessageHolde = (SendedMessageHolde) holder;
            sendedMessageHolde.initBaseView(messagesList.get(position));
            sendedMessageHolde.checkData(messagesList.get(position),holder,position);
        }
        else if (holder instanceof ReceivedMessageHolder){
            ReceivedMessageHolder receivedMessageHolder = (ReceivedMessageHolder) holder;
            receivedMessageHolder.initBaseView(messagesList.get(position));
            receivedMessageHolder.checkData(messagesList.get(position),holder,position);
            receivedMessageHolder.setMessageName(messagesList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mAuth.getCurrentUser().getUid().equals(messagesList.get(position).getUserId())){
            return SENDED_MESSAGE;
        }else {
            return RECEIVED_MESSAGE;
        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageBody;
        public TextView time;
        public TextView dateMsg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageBody = itemView.findViewById(R.id.text_message_body);
            time = itemView.findViewById(R.id.text_message_time);
            dateMsg = itemView.findViewById(R.id.text_message_body_created);
        }
        public void checkData(MessageEntity m, ViewHolder holder, int position) {

            if (position < messagesList.size() - 1) {


                if (!dataUtils.getEqualsHeaderMessage(messagesList.get(position).getTime(), messagesList.get(position + 1).getTime())) {

                    dateMsg.setVisibility(View.VISIBLE);
                    dateMsg.setText(dataUtils.getElapsedTime(messagesList.get(position).getTime(), "chats_header"));


                } else {
                    dateMsg.setVisibility(View.GONE);

                }


            } else {

                dateMsg.setVisibility(View.VISIBLE);
                dateMsg.setText(dataUtils.getElapsedTime(messagesList.get(position).getTime(), "chats_header"));


            }
        }
        public void initBaseView(MessageEntity messageEntity){
            messageBody.setText(Html.fromHtml((messageEntity.getMessage().replace("<","&lt;").replace(">","&gt;").replace("\n","<br />")) + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));
            time.setText(dataUtils.getElapsedTime(messageEntity.getTime(), "chats_message"));
        }
    }

    public class SendedMessageHolde extends ViewHolder {

        public SendedMessageHolde(View view) {
            super(view);
        }

    }

    private class ReceivedMessageHolder extends ViewHolder {
        private TextView messageName;
        private CircleImageView image_message_profile;
        public ReceivedMessageHolder(View view) {
            super(view);
            image_message_profile = view.findViewById(R.id.image_message_profile);
            messageName = view.findViewById(R.id.text_message_name);

        }
        public void setMessageName(MessageEntity messageEntity){
            messageName.setText(messageEntity.getName());
            String fromUserID = messageEntity.getUserId();

            usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.hasChild("image"))
                    {
                        String receiverImage = dataSnapshot.child("image").getValue().toString();

                        Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(image_message_profile);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
}
