package alireza.sn.mycontacts;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import alireza.sn.mycontacts.adapters.RecyclerAdapter;
import alireza.sn.mycontacts.models.MyFeatures;
import alireza.sn.mycontacts.models.MyInfo;
import alireza.sn.mycontacts.models.MyPreferenceManager;

public class FavoritesFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    List<MyInfo> list = new ArrayList<>();
    List<MyInfo> listAll = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setRecyclerView();
        init();
    }

    private void init() {
        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            HashSet<Integer> usedPositions = new HashSet<>();

            @Override
            public void callClick(int position) {
                MyFeatures.calling(position);
            }

            @Override
            public void itemClick(int position, ImageView pic, Button call, TextView phone) {
                MyFeatures.itemClick(position,pic,call,phone , usedPositions);
            }

            @Override
            public void starClick(int position, ImageView star) {
            }
        });
    }

    private void setRecyclerView() {
        listAll =MyPreferenceManager.getInstance(getContext()).getContactsList();
        if (!listAll.isEmpty()) {
            getFavorites();
        }
        adapter = new RecyclerAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void getFavorites() {
        for(int i = 0 ; i<listAll.size() ; i++){
            if (listAll.get(i).isMark())
                list.add(listAll.get(i));
        }
    }

    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerview_favorites);
    }


    @Subscribe
    public void notifyDataSetChange(List<MyInfo> favorite) {
        list.clear();
        list.addAll(favorite);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
