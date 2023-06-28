package com.example.btl_app_music.Fragment.SubFragment;

import static com.example.btl_app_music.MainActivity.MY_REQUEST_CODE;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.btl_app_music.Adapter.MusicAdapter;
import com.example.btl_app_music.Object.MusicList;
import com.example.btl_app_music.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalList extends Fragment {



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView accountBtn;

    private Dialog dialog;

    private SearchView searchView;

    public static MusicAdapter musicAdapter;
    public static List<MusicList> musicLists;
    private RecyclerView musicRecyclerView;








    private RelativeLayout relativeLayout, sortBtn;

    static int sortFlag = 0;


    private Button a;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        getMusicFiles();
                    } else {
                        // PERMISSION NOT GRANTED
                    }
                }
            }
    );

    public LocalList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocalList.
     */
    // TODO: Rename and change types and number of parameters
    public static LocalList newInstance(String param1, String param2) {
        LocalList fragment = new LocalList();
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
        return inflater.inflate(R.layout.fragment_local_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        relativeLayout = requireView().findViewById(R.id.content);

        sortBtn =  requireView().findViewById(R.id.sortBtn);


        accountBtn =  requireView().findViewById(R.id.btnAccount);

        musicRecyclerView =  (RecyclerView)requireView().findViewById(R.id.musicRecyclerView);
        musicRecyclerView.setHasFixedSize(true);
        musicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));








        //Tim Kiem//


        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getMusicFiles();
        }
        else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

    }

    public void findSong(String s) {
        ArrayList<MusicList> newMusicList = new ArrayList<>();
        for (MusicList list : musicLists) {
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

    private void getMusicFiles() {
        ContentResolver contentResolver = requireActivity().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        List<MusicList> musicLists2 = new ArrayList<>();

        Cursor cursor = contentResolver.query(uri, null, MediaStore.Audio.Media.DATA+" LIKE?", new String[]{"%.mp3%"}, null);
        while(cursor.moveToNext()){
            final String getMusicFileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            final String getArtisName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            long cursorId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

            //Uri musicFileUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorId);

            String getDuration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION));

            String getPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

            final MusicList musicList = new MusicList(getMusicFileName, getArtisName, getDuration, getPath );

            if (getDuration != null) {
                musicLists2.add(musicList);
            }
        }


        musicLists = new ArrayList<>(new LinkedHashSet<>(musicLists2));


        musicAdapter = new MusicAdapter(musicLists, getContext());

        musicRecyclerView.setAdapter(musicAdapter);

        cursor.close();
    }


}