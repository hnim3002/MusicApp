package com.example.btl_app_music.Fragment.SubFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl_app_music.Adapter.SearchPlaylistAdapter;
import com.example.btl_app_music.MainActivity;
import com.example.btl_app_music.Object.Item;
import com.example.btl_app_music.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Playlist#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Playlist extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<Item> playlistItem;

    private TextView playlistName;

    public static int playlistType;

    private RecyclerView recyclerView;

    private SearchPlaylistAdapter searchPlaylistAdapter;

    private SearchView searchView;

    private ImageView backBtn;


    public Playlist() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Playlist.
     */
    // TODO: Rename and change types and number of parameters
    public static Playlist newInstance(String param1, String param2) {
        Playlist fragment = new Playlist();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        playlistName = requireView().findViewById(R.id.playlistName);

        recyclerView = requireView().findViewById(R.id.musicRecyclerViewPlaylist);

        backBtn = requireView().findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity a = (MainActivity) getActivity();
                assert a != null;
                a.onBackPressed();
            }
        });

        /*searchView = requireView().findViewById(R.id.searchBtn);

        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                findSong(newText);
                return true;
            }
        });*/

        //Tim Kiem//

        Bundle bundle = getArguments();

        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);

        assert bundle != null;
        playlistType = bundle.getInt("playlist", 0);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        playlistItem = new ArrayList<>();

        searchPlaylistAdapter = new SearchPlaylistAdapter(playlistItem, getContext(), new SearchPlaylistAdapter.IClickItemListener() {
            @Override
            public void onClickItem(Item item) {
                if(playlistType == 1) {
                    openFragmentSonglist( item, 2);
                } else if(playlistType == 2) {
                    openFragmentSonglist( item, 3);
                }
            }
        });

        recyclerView.setAdapter(searchPlaylistAdapter);

        if(playlistType == 1) {
            playlistName.setText("Artist Playlist");
            getArtistPlaylist();
        } else if(playlistType == 2) {
            playlistName.setText("Public Playlist");
            getPublicPlaylist();
        }
    }

    public void getArtistPlaylist() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("artistPlayList");


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Item item = new Item(snapshot.getKey(), 0, snapshot.child("img").getValue(String.class));
                playlistItem.add(item);
                searchPlaylistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getPublicPlaylist() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("publicPlaylist");


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = new Item(dataSnapshot.child("name").getValue(String.class), 0, dataSnapshot.child("img").getValue(String.class));
                    playlistItem.add(item);
                    searchPlaylistAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void findSong(String s) {
        List<Item> newList = new ArrayList<>();
        for (Item item : playlistItem) {
            if (item.getItemName().toLowerCase().contains(s.toLowerCase())) {
                newList.add(item);
            }
        }
        if (newList.isEmpty()) {
            Toast.makeText(getContext(),"No song found", Toast.LENGTH_SHORT).show();
        }
        else {
            searchPlaylistAdapter.updateList(newList);
        }
    }

    public void openFragmentSonglist(Item item, int listType) {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        OnlineList onlineList = new OnlineList();
        Bundle bundle = new Bundle();
        bundle.putInt("online", listType);
        bundle.putInt("itemType", item.getUserPlaylistType());
        bundle.putString("listSongName", item.getItemName());
        onlineList.setArguments(bundle);
        fragmentTransaction.replace(R.id.playlistFragment, onlineList);
        fragmentTransaction.addToBackStack(MainActivity.onlineTAG);
        fragmentTransaction.commit();
    }




}