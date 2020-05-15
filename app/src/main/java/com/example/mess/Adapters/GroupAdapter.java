package com.example.mess.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mess.Groups;
import com.example.mess.MessageEntity;
import com.example.mess.R;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private List<Groups> messageEntityList;
    private Context context;
    private ClickInterface clickInterface;

    public void setClickListner(ClickInterface clickListner){
        this.clickInterface = clickListner;
    }
    public interface ClickInterface{
        void onItemClickListner(Groups messageEntity, int position);

    }

    public GroupAdapter(List<Groups> messageEntityList, Context context){
        this.messageEntityList = messageEntityList;
        this.context = context;
    }
    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chats_row, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {
        holder.initView(messageEntityList.get(position));
    }

    @Override
    public int getItemCount() {
        return messageEntityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView groupName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            groupName = itemView.findViewById(R.id.group_name);
        }
        public void initView(Groups messageEntity){
            groupName.setText(messageEntity.getDisplayName());
            itemView.setOnClickListener(view -> clickInterface.onItemClickListner(messageEntity,getAdapterPosition()));
        }



    }
}
