package alireza.sn.mycontacts;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.widget.SearchView;;
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

public class ContactsFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerAdapter adapter;

    public static TextView textView;

    SearchView searchView;

     List<MyInfo> contactsList = new ArrayList<>();
     List<MyInfo> favorite = new ArrayList<>();

    @Subscribe
    public void notifyDataSetChange (String post) {
        if (post.equals("received_contacts")) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.framgent_contacts, container, false);
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        setRecyclerView();
        new MyFeatures(getActivity(),contactsList).getAndSaveContacts();
        setFavorites();
        init();
    }

    public  void setFavorites() {
        for (int i=0; i<contactsList.size() ; i++ ) {

            if (contactsList.get(i).isMark())
                favorite.add(contactsList.get(i));
        }
        EventBus.getDefault().post(favorite);
        new MyFeatures(favorite);
    }

    private void init() {
        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            HashSet<Integer> usedPositions = new HashSet<>();

            @Override
            public void callClick(int position) {
                MyFeatures.calling(position);
            }

            @Override
            public void itemClick(int position, ImageView pic, Button callBtn, TextView phone) {
                MyFeatures.itemClick(position,pic,callBtn,phone , usedPositions);
            }

            @Override
            public void starClick(int position, ImageView star) {
                if (contactsList.get(position).isMark()) {
                    star.setImageResource(R.drawable.icon_favorites);
                    star.animate().rotation(-10).start();
                    contactsList.get(position).setMark(false);
                    favorite.remove(contactsList.get(position));
                } else {
                    star.setImageResource(R.drawable.icon_favorites_full);
                    // animations(star);
                    contactsList.get(position).setMark(true);
                    favorite.add(contactsList.get(position));
                }
                MyPreferenceManager.getInstance(getActivity()).putContactsList(contactsList);
                EventBus.getDefault().post(favorite);
            }

        });
    }
    private static void animations(ImageView img) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(img, "alpha", 0f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(img, "rotation", 10);
        alpha.setDuration(500).start();
        rotation.setDuration(500).start();
    }

    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerview_contacts);
        searchView = view.findViewById(R.id.search_view);
        textView = view.findViewById(R.id.loading);
    }

    private void setRecyclerView() {
        contactsList = MyPreferenceManager.getInstance(getContext()).getContactsList();
        adapter = new RecyclerAdapter(contactsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
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

