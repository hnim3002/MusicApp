package com.example.btl_app_music.Fragment.BottomNavFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.btl_app_music.Adapter.ArtistAdapter;
import com.example.btl_app_music.Adapter.ListItemAdapter;
import com.example.btl_app_music.Adapter.YourPlayListAdapter;
import com.example.btl_app_music.Fragment.SubFragment.OnlineList;
import com.example.btl_app_music.Fragment.SubFragment.Playlist;
import com.example.btl_app_music.MainActivity;
import com.example.btl_app_music.Object.Item;
import com.example.btl_app_music.Object.ListItem;
import com.example.btl_app_music.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;

    int i = 0;

    private ListItemAdapter listItemAdapter;

    public static List<ListItem> listItems;

    List<Item> itemArtist = new ArrayList<>();

    List<Item> itemPublic = new ArrayList<>();

    List<Item> itemForYou = new ArrayList<>();

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = requireView().findViewById(R.id.musicRecyclerViewTong);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listItems = new ArrayList<>();

        listItemAdapter = new ListItemAdapter(listItems, getContext(), new YourPlayListAdapter.IClickItemListener() {
            @Override
            public void onClickItem(Item item) {
                if(item.getUserPlaylistType() == 1) {
                    if(item.getItemName().equals("Your Like Song         ")) {
                        openFragmentSonglist(item, 4);
                    } else {
                        openFragmentSonglist(item, 5);
                    }

                } else if(item.getUserPlaylistType() == 2) {
                    openFragmentSonglist(item, 6);
                }

            }
        }, new ArtistAdapter.IClickItemListener() {
            @Override
            public void onClickItem(Item item, int a) {
                if (a == 2) {
                    openFragmentSonglist(item, 2);
                } else if (a == 3) {
                    openFragmentSonglist(item, 3);
                }
            }
        }, new ListItemAdapter.IClickItemListener2() {
            @Override
            public void onClickItem(int playlistType) {
                if (playlistType == 2) {
                    openFragmentPlaylist(1);
                } else if (playlistType == 3) {
                    openFragmentPlaylist(2);
                }
            }
        });

        listItems.add(new ListItem(ListItemAdapter.TYPE_FORYOU,"Your Playlist",itemForYou, null, 1));
        listItems.add(new ListItem(ListItemAdapter.TYPE_ARTIST,"Artis For You", null, itemArtist, 2));
        listItems.add(new ListItem(ListItemAdapter.TYPE_ARTIST,"Public Playlist", null, itemPublic, 3));

        recyclerView.setAdapter(listItemAdapter);
        //getItemForYou();
        getUserPlaylist();
        getPublicPlaylist();
        getListSong();

    }

    private void openFragmentSonglist(Item item, int listType) {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        OnlineList onlineList = new OnlineList();
        Bundle bundle = new Bundle();
        bundle.putInt("online", listType);
        bundle.putString("listSongName", item.getItemName());
        onlineList.setArguments(bundle);
        fragmentTransaction.replace(R.id.homeFragment, onlineList);
        fragmentTransaction.addToBackStack(MainActivity.onlineTAG);
        fragmentTransaction.commit();
    }

    public void openFragmentPlaylist(int playlistType) {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Playlist playlist = new Playlist();
        Bundle bundle = new Bundle();
        bundle.putInt("playlist", playlistType);
        playlist.setArguments(bundle);
        fragmentTransaction.replace(R.id.homeFragment, playlist);
        fragmentTransaction.addToBackStack(MainActivity.playlistTAG);
        fragmentTransaction.commit();
    }




    public void getUserPlaylist() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = String.valueOf(mAuth.getUid());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("user/" + userId);
        itemForYou.add(new Item("Your Like Song         ", 1, "https://firebasestorage.googleapis.com/v0/b/login-register-email-firebase.appspot.com/o/Picture%2F%C3%A0dasdfasdfasdfasdfasdf.png?alt=media&token=9d8e3b0f-2db0-47ef-a215-4f64bf56b86d"));

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //if(!Objects.equals(snapshot.getKey(), "likePlaylist         ")) {
                    if(!Objects.equals(snapshot.getKey(), "Your Like Song         ")) {
                        Item item = new Item(snapshot.getKey(), 1, snapshot.child("img").getValue(String.class));
                        itemForYou.add(item);

                        if(itemForYou.size() == 1) {

                            listItemAdapter.notifyDataSetChanged();
                        }
                    }

               // }

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


        DatabaseReference mDatabase1 = database.getReference("publicPlaylist/" + userId);
        mDatabase1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Item item = new Item(snapshot.getKey(), 2, snapshot.child("img").getValue(String.class));
                itemForYou.add(item);

                if(itemForYou.size() == 1) {

                    listItemAdapter.notifyDataSetChanged();
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

    public void getListSong() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("artistPlayList");


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Item item = new Item(snapshot.getKey(), 0, snapshot.child("img").getValue(String.class));

                itemArtist.add(item);

                if(itemArtist.size() == 6) {

                    listItemAdapter.notifyDataSetChanged();
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

    public void getPublicPlaylist() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("publicPlaylist");

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = new Item(dataSnapshot.child("name").getValue(String.class), 0, dataSnapshot.child("img").getValue(String.class));
                    itemPublic.add(item);
                    if(itemPublic.size() == 6) {

                        listItemAdapter.notifyDataSetChanged();
                    }
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

    @Override
    public void onResume() {
        recyclerView.setAdapter(listItemAdapter);
        super.onResume();
    }
}