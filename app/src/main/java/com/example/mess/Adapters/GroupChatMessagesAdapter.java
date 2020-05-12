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
import com.example.mess.GroupEntity;
import com.example.mess.Messages;
import com.example.mess.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class GroupChatMessagesAdapter extends  RecyclerView.Adapter<GroupChatMessagesAdapter.ViewHolder> {
    private static final int SENDED_MESSAGE = 1;
    private static final int RECEIVED_MESSAGE = 2;
    private DataUtils dataUtils = new DataUtils();
    private List<GroupEntity> messagesList = new ArrayList<>();
    private Context context;

    private FirebaseAuth mAuth;

    public GroupChatMessagesAdapter(List<GroupEntity> messagesList, Context context){
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
        }
        else if (holder instanceof ReceivedMessageHolder){
            ReceivedMessageHolder receivedMessageHolder = (ReceivedMessageHolder) holder;
            receivedMessageHolder.initBaseView(messagesList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mAuth.getCurrentUser().getUid().equals(messagesList.get(position).getFrom())){
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageBody = itemView.findViewById(R.id.text_message_body);
            time = itemView.findViewById(R.id.text_message_time);

        }
        public void initBaseView(GroupEntity groupEntity){
            messageBody.setText(Html.fromHtml((groupEntity.getMessage().replace("<","&lt;").replace(">","&gt;").replace("\n","<br />")) + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));
            time.setText(dataUtils.getElapsedTime(groupEntity.getTime(), "chats_message"));
        }
    }

    public class SendedMessageHolde extends ViewHolder {

        public SendedMessageHolde(View view) {
            super(view);
        }

    }

    private class ReceivedMessageHolder extends ViewHolder {
        private TextView messageName;
        public ReceivedMessageHolder(View view) {
            super(view);
            messageName = view.findViewById(R.id.text_message_name);

        }
        public void setMessageName(GroupEntity groupEntity){
            messageName.setText(groupEntity.getName());
        }

    }
}
