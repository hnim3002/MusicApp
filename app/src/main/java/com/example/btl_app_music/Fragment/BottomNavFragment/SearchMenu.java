package com.example.btl_app_music.Fragment.BottomNavFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.btl_app_music.Fragment.SubFragment.LocalList;
import com.example.btl_app_music.Fragment.SubFragment.OnlineList;
import com.example.btl_app_music.Fragment.SubFragment.Playlist;
import com.example.btl_app_music.MainActivity;
import com.example.btl_app_music.R;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchMenu extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView onlineSongBtn, localSongBtn, artisPlaylistBtn, publicPlaylistBtn;



    TextView text;

    public SearchMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchMenu newInstance(String param1, String param2) {
        SearchMenu fragment = new SearchMenu();
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
        return inflater.inflate(R.layout.fragment_search_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        text = requireView().findViewById(R.id.test);
        onlineSongBtn = requireView().findViewById(R.id.onlineSongPl);
        localSongBtn = requireView().findViewById(R.id.localSongPl);
        publicPlaylistBtn = requireView().findViewById(R.id.publicPlaylist);
        artisPlaylistBtn = requireView().findViewById(R.id.artistSongPl);


        onlineSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                OnlineList onlineList = new OnlineList();
                Bundle bundle = new Bundle();
                bundle.putInt("online", 1);
                onlineList.setArguments(bundle);
                fragmentTransaction.replace(R.id.searchFragment, onlineList);
                fragmentTransaction.addToBackStack(MainActivity.onlineTAG);
                fragmentTransaction.commit();

            }
        });

        localSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.searchFragment, new LocalList());
                fragmentTransaction.addToBackStack(MainActivity.localTAG);
                fragmentTransaction.commit();
            }
        });

        publicPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                Playlist playlist = new Playlist();
                Bundle bundle = new Bundle();
                bundle.putInt("playlist", 2);
                playlist.setArguments(bundle);
                fragmentTransaction.replace(R.id.searchFragment, playlist);
                fragmentTransaction.addToBackStack(MainActivity.playlistTAG);
                fragmentTransaction.commit();
            }
        });

        artisPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                Playlist playlist = new Playlist();
                Bundle bundle = new Bundle();
                bundle.putInt("playlist", 1);
                playlist.setArguments(bundle);
                fragmentTransaction.replace(R.id.searchFragment, playlist);
                fragmentTransaction.addToBackStack(MainActivity.playlistTAG);
                fragmentTransaction.commit();
            }
        });


       /* text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity a = (MainActivity)getActivity();
                assert a != null;
                a.openFragment();
            }
        });*/

    }


}