package com.example.mess;


import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mess.Adapters.GroupAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment implements GroupAdapter.ClickInterface {

    private View groupFragmentView;
    private RecyclerView listView;
    private List<Groups> messageEntityList = new ArrayList<>();
    private GroupAdapter arrayAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> list_of_groups = new ArrayList<>();
    private DatabaseReference GroupRef;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        InitializeFields();

        RetrieveAndDisplayGroups();




        return groupFragmentView;
    }



    private void RetrieveAndDisplayGroups() {

        GroupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Iterator interator = dataSnapshot.getChildren().iterator();
                while (interator.hasNext()) {
                    Groups messageEntity = new Groups();
                    messageEntity.setDisplayName(((DataSnapshot) interator.next()).getKey());
                    System.out.println(messageEntity.getDisplayName());
                    messageEntityList.add(messageEntity);
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void InitializeFields() {
        listView = groupFragmentView.findViewById(R.id.list_view);
        layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        listView.setLayoutManager(layoutManager);
        arrayAdapter = new GroupAdapter(messageEntityList,getContext());
        listView.setAdapter(arrayAdapter);
        arrayAdapter.setClickListner(this);

    }

    @Override
    public void onItemClickListner(Groups messageEntity, int position) {
        String currentGroupName = messageEntityList.get(position).getDisplayName();
        Intent intent = new Intent(getContext(), GroupChatActivity.class);
        intent.putExtra("groupName", currentGroupName);
        startActivity(intent);
    }
}
