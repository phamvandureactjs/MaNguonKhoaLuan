package com.example.app.Main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.History;
import com.example.app.HistoryAdapter;
import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fragment_History extends Fragment {

    private View fm_history;
    private RecyclerView historyView;
    private HistoryAdapter mHAdapter;
    private List<History> mList;
    DatabaseReference mData;
    private String device;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fm_history = inflater.inflate(R.layout.fragment_history, container, false);

        mData = FirebaseDatabase.getInstance().getReference();

        anhXa();
        read_Name();
        listView_1_Show();

        return fm_history;
    }

    // Ánh xạ ID
    private void anhXa() {
        historyView = fm_history.findViewById(R.id.historyView);
    }

    // Cấu hình list view
    private void listView_1_Show() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        historyView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        historyView.addItemDecoration(dividerItemDecoration);

        mList = new ArrayList<>();
        mHAdapter = new HistoryAdapter(mList);
        historyView.setAdapter(mHAdapter);

        mData.child(device + "/History").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    History history = dataSnapshot.getValue(History.class);
                    mList.add(history);
                }
                mHAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Lấy tên thiết bị
    private void read_Name() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                String name_1 = profile.getDisplayName();

                if (name_1 != null) {
                    device = name_1;
                }
            }
        }
    }
}
