package com.example.btl_app_music.Fragment.SubFragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.btl_app_music.MainActivity;
import com.example.btl_app_music.Adapter.MusicAdapterOnline;
import com.example.btl_app_music.Object.MusicList;
import com.example.btl_app_music.Object.MusicListOnline;
import com.example.btl_app_music.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnlineList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnlineList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean allowRefresh = false;

    private SearchView searchView;

    public static MusicAdapterOnline musicAdapter;


    public static List<MusicList> musicListsOnline;
    public static RecyclerView musicRecyclerView;

    List<MusicList> newlst = new ArrayList<>();

    private int listType;

    private Button a;




    private RelativeLayout sortBtn;

    private FloatingActionButton addSongBtn;

    private int itemType;

    private String listSongName;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        getMusicFiles();
                    }
                }
            }
    );



    public OnlineList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnlineList.
     */
    // TODO: Rename and change types and number of parameters
    public static OnlineList newInstance(String param1, String param2) {
        OnlineList fragment = new OnlineList();
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
        return inflater.inflate(R.layout.fragment_online_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        a = requireView().findViewById(R.id.test);
        musicListsOnline = new ArrayList<>();


        sortBtn =  requireView().findViewById(R.id.sortBtn);

        searchView = requireView().findViewById(R.id.findSong);

        addSongBtn = requireView().findViewById(R.id.addSong);

        addSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragmentAdd();
            }
        });

        musicRecyclerView =  (RecyclerView)requireView().findViewById(R.id.musicRecyclerView);

        musicRecyclerView.setHasFixedSize(true);

        musicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity a = (MainActivity)getActivity();
                assert a != null;
                a.onPressRandomBtnOnline(musicListsOnline);
            }
        });

        searchView.clearFocus();

        //Tim Kiem//
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
        });
        Bundle bundle = getArguments();

        assert bundle != null;
        listType = bundle.getInt("online", 0);
        itemType = bundle.getInt("itemType", 0);




        switch (listType) {
            case 1:
                getListSong();
                break;
            case 2:
                listSongName = bundle.getString("listSongName");
                getArtistSong(listSongName );
                break;
            case 3:
                listSongName = bundle.getString("listSongName");
                getPublicSong(listSongName );
                break;
            case 4:
                listSongName = bundle.getString("listSongName");
                getUserPrivateSong(listSongName);
                break;
            case 5:
                addSongBtn.setVisibility(View.VISIBLE);
                listSongName = bundle.getString("listSongName");
                getUserPrivateSong(listSongName);
                break;
            case 6:
                addSongBtn.setVisibility(View.VISIBLE);
                listSongName = bundle.getString("listSongName");
                getUserPublicSong(listSongName);
                break;
            case 7:
                onClickRequestPermission();
                break;

        }



        musicAdapter = new MusicAdapterOnline(itemType, musicListsOnline, getContext());

        musicRecyclerView.setAdapter(musicAdapter);
    }

    public void onClickRequestPermission() {

        if(requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getMusicFiles();


        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

    }

    public void getListSong() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("onlineSong");


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MusicListOnline musicList = snapshot.getValue(MusicListOnline.class);
                assert musicList != null;
                MusicList list = new MusicList(musicList.getTitle(), musicList.getArtist(), musicList.getDuration(), musicList.getPath());
                musicListsOnline.add(list);
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

    public void getArtistSong(String a) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("artistPlayList/" + a + "/songList");


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MusicListOnline musicList = snapshot.getValue(MusicListOnline.class);
                assert musicList != null;
                MusicList list = new MusicList(musicList.getTitle(), musicList.getArtist(), musicList.getDuration(), musicList.getPath());
                musicListsOnline.add(list);
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

    public void getPublicSong(String a) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("publicPlaylist");


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    if (Objects.equals(dataSnapshot.child("name").getValue(String.class), a)) {
                        for(DataSnapshot data: dataSnapshot.child("songList").getChildren()) {
                            MusicListOnline musicListOnline = data.getValue(MusicListOnline.class);
                            MusicList musicList = new MusicList(musicListOnline.getTitle(), musicListOnline.getArtist(), musicListOnline.getDuration(), musicListOnline.getPath());
                            musicListsOnline.add(musicList);
                        }

                        musicAdapter.notifyDataSetChanged();
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

    public void getUserPrivateSong(String a) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String s = String.valueOf(mAuth.getUid());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference mDatabase = database.getReference("user/" + s + "/" + a /*+ "/songList"*/);


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(Objects.equals(snapshot.getKey(), "songList")) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MusicList musicList = dataSnapshot.getValue(MusicList.class);
                        musicListsOnline.add(musicList);
                        musicAdapter.notifyDataSetChanged();
                    }


                }




            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                musicListsOnline.clear();
                if(Objects.equals(snapshot.getKey(), "songList")) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MusicList musicList = dataSnapshot.getValue(MusicList.class);
                        musicListsOnline.add(musicList);
                        musicAdapter.notifyDataSetChanged();
                    }


                }
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

    public void getUserPublicSong(String a) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String s = String.valueOf(mAuth.getUid());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference mDatabase = database.getReference("publicPlaylist/" + s + "/" + a);


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(Objects.equals(snapshot.getKey(), "songList")) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        List<MusicList> a = new ArrayList<>();
                        MusicListOnline musicListOnline = dataSnapshot.getValue(MusicListOnline.class);
                        assert musicListOnline != null;
                        MusicList musicList = new MusicList(musicListOnline.getTitle(), musicListOnline.getArtist(), musicListOnline.getDuration(), musicListOnline.getPath());
                        a.add(musicList);
                        musicListsOnline.add(musicList);
                        musicAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                musicListsOnline.clear();
                if(Objects.equals(snapshot.getKey(), "songList")) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        List<MusicList> a = new ArrayList<>();
                        MusicListOnline musicListOnline = dataSnapshot.getValue(MusicListOnline.class);
                        assert musicListOnline != null;
                        MusicList musicList = new MusicList(musicListOnline.getTitle(), musicListOnline.getArtist(), musicListOnline.getDuration(), musicListOnline.getPath());
                        a.add(musicList);
                        musicListsOnline.add(musicList);
                        musicAdapter.notifyDataSetChanged();
                    }
                }
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




    public void openFragmentAdd() {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        AddSongList addSongList = new AddSongList();
        Bundle bundle = new Bundle();
        Bundle bundle2 = getArguments();
        assert bundle2 != null;
        String listSongName = bundle2.getString("listSongName");
        int listType = bundle2.getInt("online", 0);
        bundle.putInt("listType" , listType);
        bundle.putString("listSongName", listSongName);
        bundle.putSerializable("musicList", (Serializable) musicListsOnline);
        addSongList.setArguments(bundle);
        fragmentTransaction.add(R.id.onlinePlaylist, addSongList);
        fragmentTransaction.addToBackStack(MainActivity.addSonglist);
        fragmentTransaction.commit();
    }

    public void findSong(String s) {
        ArrayList<MusicList> newMusicList = new ArrayList<>();
        for (MusicList list : musicListsOnline) {
            if (list.getTitle().toLowerCase().contains(s.toLowerCase())) {
                newMusicList.add(list);
            }
        }
        if (newMusicList.isEmpty()) {
            Toast.makeText(getContext(),"No song found", Toast.LENGTH_SHORT).show();
        }
        else {
            musicAdapter.updateList(newMusicList);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 213:
                if(itemType == 1) {
                    DeleteSongPrivate(item.getGroupId());
                } else if(itemType == 2) {
                    DeleteSongPublic(item.getGroupId());
                }

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void DeleteSongPrivate(int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String s = String.valueOf(mAuth.getUid());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference mDatabase = database.getReference("user/" + s + "/" + listSongName + "/songList/" + musicListsOnline.get(position).getTitle());

        mDatabase.removeValue();

        musicAdapter.Delete(position);
    }

    private void DeleteSongPublic(int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String s = String.valueOf(mAuth.getUid());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference mDatabase = database.getReference("publicPlaylist/" + s + "/" +  listSongName + "/songList/" + musicListsOnline.get(position).getTitle());

        mDatabase.removeValue();

        musicAdapter.Delete(position);
    }

    private void getMusicFiles() {
        ContentResolver contentResolver = requireActivity().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        List<MusicList> musicLists2 = new ArrayList<>();

        Cursor cursor = contentResolver.query(uri, null, MediaStore.Audio.Media.DATA+" LIKE?", new String[]{"%.mp3%"}, null);
        while(cursor.moveToNext()){
            final String getMusicFileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            final String getArtisName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));


            String getDuration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION));

            String generateDuration = null;

            if (getDuration != null) {
                generateDuration = String.format(Locale.getDefault(),"%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(getDuration)),
                        TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(getDuration)) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(getDuration))));
            }

            String getPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

            final MusicList musicList = new MusicList(getMusicFileName, getArtisName, generateDuration, getPath);

            if (getDuration != null) {
                musicListsOnline.add(musicList);
            }
        }

        cursor.close();
    }



}