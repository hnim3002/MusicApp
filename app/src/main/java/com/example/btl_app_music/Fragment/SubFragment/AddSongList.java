package com.example.btl_app_music.Fragment.SubFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.example.btl_app_music.Adapter.AddSongAdapter;
import com.example.btl_app_music.Adapter.MusicAdapterOnline;
import com.example.btl_app_music.Object.MusicList;
import com.example.btl_app_music.Object.MusicListOnline;
import com.example.btl_app_music.Position;
import com.example.btl_app_music.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddSongList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddSongList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SearchView searchView;

    private AddSongAdapter musicAdapter;


    private List<MusicList> musicAddSong, songAdded;
    private RecyclerView musicRecyclerView;

    private int listType;

    private Button a;




    private RelativeLayout sortBtn;

    public AddSongList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddSongList.
     */
    // TODO: Rename and change types and number of parameters
    public static AddSongList newInstance(String param1, String param2) {
        AddSongList fragment = new AddSongList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_song_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sortBtn =  requireView().findViewById(R.id.sortBtn);

        searchView = requireView().findViewById(R.id.findSong);

        musicRecyclerView =  (RecyclerView)requireView().findViewById(R.id.musicRecyclerViewAdd);

        musicRecyclerView.setHasFixedSize(true);

        musicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        musicAddSong = new ArrayList<>();


        Bundle bundle = getArguments();

        assert bundle != null;
        songAdded = (List<MusicList>) bundle.getSerializable("musicList");

        musicAdapter = new AddSongAdapter(musicAddSong, getContext(), new AddSongAdapter.IClickItemListener() {
            @Override
            public void onClickItem(MusicList list1) {
                String listSongName = bundle.getString("listSongName");
                int songType = bundle.getInt("listType", 0);
                if(songType == 5) {
                    addSongPrivate(list1, listSongName);
                } else if (songType == 6) {
                    MusicListOnline musicListOnline = new MusicListOnline(list1.getArtist(), list1.getDuration(), list1.getPath(), list1.getTitle());
                    addSongPublic(musicListOnline, listSongName);
                }

            }
        });

        musicRecyclerView.setAdapter(musicAdapter);
        getListSong();
    }

    public void getListSong() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("onlineSong");


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                boolean a = false;
                MusicListOnline musicList = snapshot.getValue(MusicListOnline.class);
                assert musicList != null;
                MusicList list = new MusicList(musicList.getTitle(), musicList.getArtist(), musicList.getDuration(), musicList.getPath());
                for(MusicList list1: songAdded) {
                    if(musicList.getTitle().equals(list1.getTitle())) {
                        a = true;
                    }
                }
                if(!a) {
                    musicAddSong.add(list);
                }
                musicAdapter.notifyDataSetChanged();
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

    public void addSongPrivate(MusicList list, String a) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String s = String.valueOf(mAuth.getUid());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("user/" + s + "/" + a + "/songList");
        mDatabase.child(list.getTitle()).setValue(list);
    }

    public void addSongPublic(MusicListOnline list, String a) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String s = String.valueOf(mAuth.getUid());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("publicPlaylist/" + s + "/" + a + "/songList");
        mDatabase.child(list.getTitle()).setValue(list);
    }
}