package com.example.btl_app_music;

import static com.example.btl_app_music.Service.Service.mediaPlayer;
import static com.example.btl_app_music.Service.Service.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.btl_app_music.Fragment.SubFragment.AddSongList;
import com.example.btl_app_music.Fragment.SubFragment.OnlineList;
import com.example.btl_app_music.Fragment.BottomNavFragment.SearchMenu;
import com.example.btl_app_music.Fragment.BottomNavFragment.ViewPagerAdapter;
import com.example.btl_app_music.Fragment.SubFragment.Playlist;
import com.example.btl_app_music.Object.MusicList;
import com.example.btl_app_music.Service.Service;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.example.btl_app_music.LoginHandle.Login;

public class MainActivity extends AppCompatActivity implements SongChangeListener {
    //tesst

    public static final int MY_REQUEST_CODE = 10;
    public static List<MusicList> musicPlayerList = new ArrayList<>();

    public static  boolean isMusicPlayerOpen = false;

    public static final String addSonglist = AddSongList.class.getName();
    public static final String playlistTAG = Playlist.class.getName();
    public static final String onlineTAG = OnlineList.class.getName();

    /*private LinearLayout bottomSheetLayout;

    private BottomSheetBehavior bottomSheetBehavior;*/
    public static final String TAG = SearchMenu.class.getName();
    public static boolean repeatIsPlaying;
    public static boolean onOf;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private MusicList list;
    private ImageView spinDis;

    private LinearLayout linearLayout;

    private ImageView nextBtn;
    private ImageView prevBtn;
    private RelativeLayout toPlayer;
    private Animation rotateAnimation;
    private ImageView spinDis2;
    private Animation animShow, animHide;


    public static boolean isPlaying = false;
    private SeekBar playerSeekBar;
    private ImageView playPauseImg;
    //private Timer timer;
    public static int currentSongListPosition = 0;

    public static int repeatFlag = 0;
    public static int repeatOneFlag = 0;
    private TextView nowPlaying;
    private TextView nowPlaying2;

    public static boolean shuffleFlag = false;
    public static int shuffleSwich = 0;
    private ViewPager2 mViewPager2;
    private BottomNavigationView mBottomNavigationView;


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle == null) {
                return;
            }
            isPlaying = bundle.getBoolean("isplaying");
            int actionMusic = bundle.getInt("action");
            handleLayoutMusic(actionMusic);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        String s = String.valueOf(mAuth.getUid());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference mDatabase = database.getReference("user/" + s);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean a = false;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                   if(dataSnapshot.getKey().equals("Your Like Song         ")) {
                       a = true;
                   }
                }
                if(!a) {
                    DatabaseReference mDatabase2 = database.getReference("user/" + s + "/Your Like Song         ");
                    mDatabase2.child("name").setValue("Your Like Song         ");
                    mDatabase2.child("img").setValue("https://firebasestorage.googleapis.com/v0/b/login-register-email-firebase.appspot.com/o/Picture%2F%C3%A0dasdfasdfasdfasdfasdf.png?alt=media&token=9d8e3b0f-2db0-47ef-a215-4f64bf56b86d");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        if(mUser == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data"));


        mViewPager2 = findViewById(R.id.viewPager2);
        mBottomNavigationView = findViewById(R.id.bottomNav);

        mViewPager2.setUserInputEnabled(false);


        playPauseImg = findViewById(R.id.playPauseImg);
        nextBtn = findViewById(R.id.nextBtn);
        prevBtn = findViewById(R.id.previousBtn);
        linearLayout = findViewById(R.id.bottomBar);
        toPlayer = findViewById(R.id.toPlayer);
        nowPlaying = findViewById(R.id.nowPlaying);
        nowPlaying2 = findViewById(R.id.nowPlaying2);


        spinDis2 = findViewById(R.id.spinDis1);
        playerSeekBar = findViewById(R.id.playerSeekBar);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        mViewPager2.setAdapter(adapter);

        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mBottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        mBottomNavigationView.getMenu().findItem(R.id.localSong).setChecked(true);
                        break;
                    case 2:
                        mBottomNavigationView.getMenu().findItem(R.id.onlineSong).setChecked(true);
                        break;
                    case 3:
                        mBottomNavigationView.getMenu().findItem(R.id.accountNav).setChecked(true);
                        break;
                }
            }
        });






        mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        mViewPager2.setCurrentItem(0, false);
                        break;
                    case R.id.localSong:
                        mViewPager2.setCurrentItem(1, false);
                        break;
                    case R.id.onlineSong:
                        mViewPager2.setCurrentItem(2, false);
                        break;
                    case R.id.accountNav:
                        mViewPager2.setCurrentItem(3, false);
                        break;
                }
                return true;
            }
        });





        rotateAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotation);
        //_______________________________________//

        toPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicPlayer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition( R.anim.view_show,0);
            }
        });


        //_________________________________________//


        //Doan code nut shuffle//



        //__________________________________________________//

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    if(isPlaying) {
                        mediaPlayer.seekTo(progress);
                    }
                    else if(!isPlaying) {
                        mediaPlayer.seekTo(progress);
                    }
                    else {
                        mediaPlayer.seekTo(0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




    }



    @Override
    public void onChanged(int position) {
        startService();

        currentSongListPosition = Position.currentIndex;

        repeatIsPlaying = true;
        if(mediaPlayer != null) {

            setStatusSeekBar();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (repeatFlag == 0 && shuffleFlag == false) {
                        mediaPlayer.reset();
                        isPlaying = false;
                        playerSeekBar.setProgress(0);

                        int nextSongListPosition = currentSongListPosition + 1;
                        if(nextSongListPosition >= MainActivity.musicPlayerList.size()) {
                            nextSongListPosition = 0;
                        }
                        MainActivity.musicPlayerList.get(currentSongListPosition).setPlaying(false);
                        MainActivity.musicPlayerList.get(nextSongListPosition).setPlaying(true);
                        /*Position.getInstance().reset();*/
                        Position.currentIndex = nextSongListPosition;
                        Position.currentName = MainActivity.musicPlayerList.get(Position.currentIndex).getTitle();
                        OnlineList.musicAdapter.notifyDataSetChanged();
                        onChanged(nextSongListPosition);
                    }

                    else if (repeatFlag == 2 && shuffleFlag == false) {
                        mediaPlayer.reset();
                        isPlaying = false;
                        playerSeekBar.setProgress(0);

                        int nextSongListPosition;
                        if (repeatOneFlag == 0) {
                            nextSongListPosition =  currentSongListPosition;
                            repeatOneFlag++;
                        }
                        else {
                            nextSongListPosition = currentSongListPosition + 1;
                            repeatOneFlag = 0;
                            repeatFlag = 0;

                        }
                        if(nextSongListPosition >= MainActivity.musicPlayerList.size()) {
                            nextSongListPosition = 0;
                        }
                        MainActivity.musicPlayerList.get(currentSongListPosition).setPlaying(false);
                        MainActivity.musicPlayerList.get(nextSongListPosition).setPlaying(true);
                        /*Position.getInstance().reset();*/
                        Position.currentIndex = nextSongListPosition;
                        Position.currentName = MainActivity.musicPlayerList.get(Position.currentIndex).getTitle();
                        OnlineList.musicAdapter.notifyDataSetChanged();
                        onChanged(nextSongListPosition);
                    }

                    else if (repeatFlag == 1 && shuffleFlag == false) {
                        mediaPlayer.reset();
                        isPlaying = false;
                        playerSeekBar.setProgress(0);

                        int nextSongListPosition = currentSongListPosition;
                        MainActivity.musicPlayerList.get(currentSongListPosition).setPlaying(false);
                        MainActivity.musicPlayerList.get(nextSongListPosition).setPlaying(true);
                        /*Position.getInstance().reset();*/
                        Position.currentIndex = nextSongListPosition;
                        Position.currentName = MainActivity.musicPlayerList.get(Position.currentIndex).getTitle();
                        OnlineList.musicAdapter.notifyDataSetChanged();
                        onChanged(nextSongListPosition);
                    }
                    if(shuffleFlag && repeatFlag == 0) {
                        int i = -1;
                        i = getRandomNumber(i);
                        mediaPlayer.reset();
                        isPlaying = false;
                        playerSeekBar.setProgress(0);

                        int nextSongListPosition = i;
                        MainActivity.musicPlayerList.get(currentSongListPosition).setPlaying(false);
                        MainActivity.musicPlayerList.get(nextSongListPosition).setPlaying(true);
                        /*Position.getInstance().reset();*/
                        Position.currentIndex = nextSongListPosition;
                        Position.currentName = MainActivity.musicPlayerList.get(Position.currentIndex).getTitle();
                        OnlineList.musicAdapter.notifyDataSetChanged();
                        onChanged(nextSongListPosition);
                    }
                    else if(shuffleFlag && repeatFlag == 1) {
                        mediaPlayer.reset();
                        isPlaying = false;
                        playerSeekBar.setProgress(0);

                        int nextSongListPosition = currentSongListPosition;
                        MainActivity.musicPlayerList.get(currentSongListPosition).setPlaying(false);
                        MainActivity.musicPlayerList.get(nextSongListPosition).setPlaying(true);
                        /*Position.getInstance().reset();*/
                        Position.currentIndex = nextSongListPosition;
                        Position.currentName = MainActivity.musicPlayerList.get(Position.currentIndex).getTitle();
                        OnlineList.musicAdapter.notifyDataSetChanged();
                        onChanged(nextSongListPosition);
                    }
                    else if(shuffleFlag && repeatFlag == 2) {
                        mediaPlayer.reset();
                        isPlaying = false;
                        playerSeekBar.setProgress(0);

                        int nextSongListPosition;
                        if (repeatOneFlag == 0) {
                            nextSongListPosition =  currentSongListPosition;
                            repeatOneFlag++;
                        }
                        else {
                            int i = -1;
                            i = getRandomNumber(i);
                            nextSongListPosition = i;
                            repeatOneFlag = 0;
                            repeatFlag = 0;

                        }
                        MainActivity.musicPlayerList.get(currentSongListPosition).setPlaying(false);
                        MainActivity.musicPlayerList.get(nextSongListPosition).setPlaying(true);
                        /*Position.getInstance().reset();*/
                        Position.currentIndex = nextSongListPosition;
                        Position.currentName = MainActivity.musicPlayerList.get(Position.currentIndex).getTitle();
                        OnlineList.musicAdapter.notifyDataSetChanged();
                        onChanged(nextSongListPosition);
                    }
                }
            });
        }
    }

    private void handleLayoutMusic(int actionMusic) {
        switch(actionMusic) {
            case Service.ACTION_PAUSE:
            case Service.ACTION_RESUME:
                setStatusSong();
                break;
            case Service.ACTION_NEXT:
            case Service.ACTION_PREV:
                setInfoSong();
                setStatusSong();
                break;
            case Service.ACTION_CLEAR:

                break;
            case Service.ACTION_START:
                linearLayout.setVisibility(View.VISIBLE);
                setInfoSong();
                setStatusSong();
                break;
        }
    }

    private void setInfoSong() {

        list = MainActivity.musicPlayerList.get(Position.currentIndex);


        nowPlaying.setText(list.getTitle());
        nowPlaying2.setText(list.getArtist());

        nowPlaying.setSelected(true);

        playPauseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatIsPlaying = false;
                if(isPlaying) {
                    sendAction(Service.ACTION_PAUSE);
                }
                else {
                    sendAction(Service.ACTION_RESUME);
                }
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAction(Service.ACTION_PREV);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAction(Service.ACTION_NEXT);
            }
        });
    }

    private void startService() {
        Intent intent = new Intent(this,Service.class);
        startService(intent);
    }

    public int getRandomNumber(int a) {
        Random ran = new Random();
        while(true) {

            a = ran.nextInt(MainActivity.musicPlayerList.size());

            if(a != currentSongListPosition) {
                break;
            }
        }
        return a;
    }

    public void setStatusSeekBar() {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                final int getTotalDuration = mp.getDuration();

                String generateDuration = String.format(Locale.getDefault(),"%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(getTotalDuration),
                        TimeUnit.MILLISECONDS.toSeconds(getTotalDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getTotalDuration)));


                isPlaying = true;

                mp.start();

                playerSeekBar.setMax(getTotalDuration);


                playPauseImg.setImageResource(R.drawable.ic_pause);

            }
        });


        //timer = new Timer();
        //spinDis.startAnimation(rotateAnimation);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final int getCurrentDuration = mediaPlayer.getCurrentPosition();

                        String generateDuration = String.format(Locale.getDefault(),"%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration),
                                TimeUnit.MILLISECONDS.toSeconds(getCurrentDuration) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration)));

                        playerSeekBar.setProgress(getCurrentDuration);





                        if(mediaPlayer.isPlaying()) {

                            playPauseImg.setImageResource(R.drawable.ic_pause);

                        }
                        else {
                            playPauseImg.setImageResource(R.drawable.ic_play_arrow);


                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        Position.currentName = "";
        Position.currentIndex = -1;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

    }

    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if(fm.getBackStackEntryCount() > 0 ) {
            fm.popBackStackImmediate();
        } else {
            moveTaskToBack(true);
        }

    }

    private void setStatusSong() {
        if(isPlaying) {
            playPauseImg.setImageResource(R.drawable.ic_pause);

        }
        else {
            playPauseImg.setImageResource(R.drawable.ic_play_arrow);

        }
    }

    private void sendAction(int action) {
        Intent intent = new Intent(this, Service.class);
        intent.putExtra("action_music_service", action);
        startService(intent);
    }

    public void onPressRandomBtnOnline(List<MusicList> list) {
        musicPlayerList = new ArrayList<>(list);
        int i = 0;
        /*Position.getInstance().reset();*/
        Position.currentIndex = getRandomNumber(i);
        Position.currentName = MainActivity.musicPlayerList.get(Position.currentIndex).getTitle();
        onChanged(Position.currentIndex);
        if(!shuffleFlag && repeatFlag == 0) {
            shuffleFlag = true;
        }
        else if(!shuffleFlag && repeatFlag == 1) {
            shuffleFlag = true;

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    shuffleSwich = getRandomNumber(shuffleSwich);
                    mediaPlayer.reset();
                    timer.purge();
                    timer.cancel();
                    isPlaying = false;
                    playerSeekBar.setProgress(0);
                    int nextSongListPosition = shuffleSwich;
                    MainActivity.musicPlayerList.get(currentSongListPosition).setPlaying(false);
                    MainActivity.musicPlayerList.get(nextSongListPosition).setPlaying(true);
                    Position.currentName = MainActivity.musicPlayerList.get(Position.currentIndex).getTitle();
                    OnlineList.musicAdapter.notifyDataSetChanged();
                    onChanged(nextSongListPosition);
                }
            });
        }

        else if(!shuffleFlag && repeatFlag == 2) {
            shuffleFlag = true;
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    shuffleSwich = getRandomNumber(shuffleSwich);
                    mediaPlayer.reset();
                    timer.purge();
                    timer.cancel();
                    isPlaying = false;
                    playerSeekBar.setProgress(0);
                    int nextSongListPosition = shuffleSwich;
                    MainActivity.musicPlayerList.get(currentSongListPosition).setPlaying(false);
                    MainActivity.musicPlayerList.get(nextSongListPosition).setPlaying(true);
                    Position.currentName = MainActivity.musicPlayerList.get(Position.currentIndex).getTitle();
                    OnlineList.musicAdapter.notifyDataSetChanged();
                    onChanged(nextSongListPosition);
                }
            });
        }
        else {
            shuffleFlag = false;
        }

        OnlineList.musicAdapter.notifyDataSetChanged();
    }


}