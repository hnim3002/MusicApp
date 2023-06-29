package com.example.btl_app_music.Fragment.BottomNavFragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.btl_app_music.Adapter.LibraryPlaylistAdapter;
import com.example.btl_app_music.Fragment.SubFragment.OnlineList;
import com.example.btl_app_music.MainActivity;
import com.example.btl_app_music.Object.Item;
import com.example.btl_app_music.Object.MusicList;
import com.example.btl_app_music.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YourLibrary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YourLibrary extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SearchView searchViewBtn;

    private ImageButton addPlaylistBtn;

    private RecyclerView recyclerView;

    private List<Item> itemList;

    private boolean publicOrPrivate;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    private String playlistName;

    private LibraryPlaylistAdapter playlistAdapter;

    private CircleImageView userAvatar;

    boolean name = false;
    public YourLibrary() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YourLibrary.
     */
    // TODO: Rename and change types and number of parameters
    public static YourLibrary newInstance(String param1, String param2) {
        YourLibrary fragment = new YourLibrary();
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
        return inflater.inflate(R.layout.fragment_your_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity a = (MainActivity)getActivity();

        searchViewBtn = requireView().findViewById(R.id.searchBtn);

        addPlaylistBtn = requireView().findViewById(R.id.addPlaylistBtn);

        recyclerView = requireView().findViewById(R.id.musicRecyclerViewLibrary);

        userAvatar = requireView().findViewById(R.id.profile_image);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        Glide.with(requireActivity()).load(user.getPhotoUrl()).error(R.drawable.default_avatar).into(userAvatar);

        searchViewBtn.clearFocus();

        searchViewBtn.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                findPlaylist(newText);
                return true;
            }
        });




        addPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert a != null;
                openBottomSheet();

            }
        });

        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        itemList = new ArrayList<>();

        playlistAdapter = new LibraryPlaylistAdapter(itemList, getContext(), new LibraryPlaylistAdapter.IClickItemListener() {
            @Override
            public void onClickItem(Item item) {
                if(item.getUserPlaylistType() == 1) {
                    if(item.getItemName().equals("Your Like Song         ")) {
                        openFragmentSonglist(item, 4);
                    } else {
                        openFragmentSonglist(item, 5);
                    }
                } else if (item.getUserPlaylistType() == 2) {
                    openFragmentSonglist(item, 6);
                }
            }
        });

        recyclerView.setAdapter(playlistAdapter);
        getUserPlaylist();

    }

    private void openFragmentSonglist(Item item, int listType) {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        OnlineList onlineList = new OnlineList();
        Bundle bundle = new Bundle();
        bundle.putInt("online", listType);
        bundle.putInt("itemType", item.getUserPlaylistType());
        bundle.putString("listSongName", item.getItemName());
        onlineList.setArguments(bundle);
        fragmentTransaction.replace(R.id.yourLibrary, onlineList);
        fragmentTransaction.addToBackStack(MainActivity.onlineTAG);
        fragmentTransaction.commit();
    }



    public void openBottomSheet() {
       /* bottomSheetLayout = findViewById(R.id.bottomSheetLayout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

        if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }*/

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_dialog);


        LinearLayout playlistDialog = dialog.findViewById(R.id.layoutPlaylist);
        LinearLayout blendDialog = dialog.findViewById(R.id.layoutBlend);

        blendDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicOrPrivate = false;
                openCreatePlaylistDialog();
                dialog.dismiss();
            }
        });


        playlistDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicOrPrivate = true;
                openCreatePlaylistDialog();
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void openCreatePlaylistDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.create_playlist_dialog);

        TextInputEditText inputDialog = dialog.findViewById(R.id.editTextDialog);
        CardView confirmButton = dialog.findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                String userId = String.valueOf(mAuth.getUid());
                playlistName = String.valueOf(inputDialog.getText());

                if(publicOrPrivate) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference mDatabase = database.getReference("user/" + userId);

                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean a = false;
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (Objects.equals(dataSnapshot.getKey(), String.valueOf(inputDialog.getText()))) {
                                    TextInputLayout emailInputLayout = dialog.findViewById(R.id.emailLayout);
                                    a = true;
                                    emailInputLayout.setErrorEnabled(true);
                                    emailInputLayout.setError("Playlist name already exists");
                                }
                            }
                            if(!a) {
                                mDatabase.child(playlistName + "/name").setValue(playlistName);
                                mDatabase.child(playlistName + "/img").setValue("https://firebasestorage.googleapis.com/v0/b/login-register-email-firebase.appspot.com/o/Picture%2Fmusic-note-icon-song-melody-tune-flat-symbol-free-vector.png?alt=media&token=afe7db19-732f-4889-92b8-1b45363d7da5");
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference mDatabase = database.getReference("publicPlaylist/" + userId);

                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean a = false;
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (Objects.equals(dataSnapshot.getKey(), String.valueOf(inputDialog.getText()))) {
                                    TextInputLayout emailInputLayout = dialog.findViewById(R.id.emailLayout);
                                    a = true;
                                    emailInputLayout.setErrorEnabled(true);
                                    emailInputLayout.setError("Playlist name already exists");
                                }
                            }
                            if(!a) {
                                mDatabase.child(playlistName + "/name").setValue(playlistName);
                                mDatabase.child(playlistName + "/img").setValue("https://firebasestorage.googleapis.com/v0/b/login-register-email-firebase.appspot.com/o/Picture%2FUntitled-1.png?alt=media&token=501e5549-7c2d-4d34-8eb9-c3453d2de999");
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    public void getUserPlaylist() {
        mAuth = FirebaseAuth.getInstance();
        String userId = String.valueOf(mAuth.getUid());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = database.getReference("user/" + userId);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Item item = new Item(snapshot.getKey(), 1, snapshot.child("img").getValue(String.class));
                itemList.add(item);
                playlistAdapter.notifyDataSetChanged();
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
                itemList.add(item);
                playlistAdapter.notifyDataSetChanged();
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
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 212:
                if(itemList.get(item.getGroupId()).getUserPlaylistType() == 1 && !itemList.get(item.getGroupId()).getItemName().equals("likePlaylist         ")) {
                    DeletePlaylistPrivate(item.getGroupId());
                } else if (itemList.get(item.getGroupId()).getUserPlaylistType() == 2) {
                    DeletePlaylistPublic(item.getGroupId());
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void DeletePlaylistPrivate(int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String s = String.valueOf(mAuth.getUid());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference mDatabase = database.getReference("user/" + s + "/" + itemList.get(position).getItemName());

        mDatabase.removeValue();

        playlistAdapter.Delete(position);

    }

    private void DeletePlaylistPublic(int position) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String s = String.valueOf(mAuth.getUid());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference mDatabase = database.getReference("publicPlaylist/" + s + "/" +  itemList.get(position).getItemName());

        mDatabase.removeValue();

        playlistAdapter.Delete(position);
    }


    public void findPlaylist(String s) {
        ArrayList<Item> items = new ArrayList<>();
        for (Item list : itemList) {
            if (list.getItemName().toLowerCase().contains(s.toLowerCase())) {
                items.add(list);
            }
        }
        if (items.isEmpty()) {
            Toast.makeText(getContext(),"No song found", Toast.LENGTH_SHORT).show();
        }
        else {
            playlistAdapter.updateList(items);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        Glide.with(requireActivity()).load(user.getPhotoUrl()).error(R.drawable.default_avatar).into(userAvatar);
    }
}