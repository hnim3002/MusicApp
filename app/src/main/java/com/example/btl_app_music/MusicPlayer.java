package com.example.btl_app_music;


import static com.example.btl_app_music.MainActivity.onOf;
import static com.example.btl_app_music.MainActivity.repeatFlag;
import static com.example.btl_app_music.MainActivity.repeatOneFlag;
import static com.example.btl_app_music.MainActivity.shuffleFlag;
import static com.example.btl_app_music.MainActivity.shuffleSwich;
import static com.example.btl_app_music.Service.Service.mediaPlayer;
import static com.example.btl_app_music.Service.Service.timer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.btl_app_music.Fragment.SubFragment.LocalList;
import com.example.btl_app_music.Fragment.SubFragment.OnlineList;
import com.example.btl_app_music.Object.MusicList;
import com.example.btl_app_music.Object.MusicListOnline;
import com.example.btl_app_music.Service.Service;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MusicPlayer extends AppCompatActivity {

    private ArrayList<MusicList> newList;
    private TextView textView;
    private SeekBar playerSeekBar;

    private TextView playerName;
    private TextView playerArtis;

    private FirebaseAuth mAuth;

    private TextView endTime2, startTime2;
    boolean isPlaying = false;
    private ImageView repeatBtn2, shuffleBtn2, playPauseImg2, nextBtn2, prevBtn2;

    private int currentSongListPosition = 0;

    private ImageView likeIcon;

    private boolean isLike = false;

    MusicList list;

    private ImageView backBtn;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
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
        setContentView(R.layout.activity_music_player);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        endTime2 = findViewById(R.id.playerEndTime);
        startTime2 = findViewById(R.id.playerStartTime);


        repeatBtn2 = findViewById(R.id.playerRepeatBtn);
        shuffleBtn2 = findViewById(R.id.playerSuffleBtn);
        playPauseImg2 = findViewById(R.id.playerStop);
        nextBtn2 = findViewById(R.id.playerNextBtn);
        prevBtn2 = findViewById(R.id.playerPreviousBtn);

        likeIcon = findViewById(R.id.likeIcon);


        playerName = findViewById(R.id.playerSong);
        playerArtis = findViewById(R.id.playerArtis);

        playerSeekBar =(SeekBar) findViewById(R.id.playerSeekBar2);


        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send_data"));



        playerSeekBar.setProgress(mediaPlayer.getCurrentPosition());

        playerSeekBar.setMax(mediaPlayer.getDuration());


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
                        startTime2.setText(generateDuration);


                    }
                });
            }
        }, 1000, 1000);


        if(onOf) {
            list = LocalList.musicLists.get(Position.currentIndex);
        } else {
            list = MainActivity.musicPlayerList.get(Position.currentIndex);
        }

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    if(MainActivity.isPlaying) {
                        mediaPlayer.seekTo(progress);
                    }
                    else if(!MainActivity.isPlaying) {
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

        repeatBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatFlag == 0) {
                    repeatFlag = 1;
                    repeatBtn2.setImageResource(R.drawable.ic_repeatblack_adobe_express);
                }

                else if (repeatFlag == 1) {
                    repeatFlag = 2;
                    repeatBtn2.setImageResource(R.drawable.ic_repeat1_adobe_express);
                }

                else if (repeatFlag == 2) {
                    repeatFlag = 0;
                    repeatBtn2.setImageResource(R.drawable.ic_repeat_adobe_express);
                }
            }
        });

        if(shuffleFlag) {
            shuffleBtn2.setImageResource(R.drawable.ic_shuffleblack_adobe_express);
        }
        else {
            shuffleBtn2.setImageResource(R.drawable.ic_shuffle_adobe_express);
        }

        if(onOf) {
            shuffleBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!shuffleFlag && repeatFlag == 0) {
                        shuffleFlag = true;
                        shuffleBtn2.setImageResource(R.drawable.ic_shuffleblack_adobe_express);
                    }

                    else if(!shuffleFlag && repeatFlag == 1) {
                        shuffleFlag = true;
                        shuffleBtn2.setImageResource(R.drawable.ic_shuffleblack_adobe_express);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                shuffleSwich = getRandomNumber(shuffleSwich);
                                mediaPlayer.reset();
                                isPlaying = false;
                                playerSeekBar.setProgress(0);
                                int nextSongListPosition = shuffleSwich;
                                LocalList.musicLists.get(currentSongListPosition).setPlaying(false);
                                LocalList.musicLists.get(nextSongListPosition).setPlaying(true);
                                LocalList.musicAdapter.notifyDataSetChanged();
                                onChanged(nextSongListPosition);
                            }
                        });
                    }

                    else if(!shuffleFlag && repeatFlag == 2) {
                        shuffleFlag = true;
                        shuffleBtn2.setImageResource(R.drawable.ic_shuffleblack_adobe_express);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                shuffleSwich = getRandomNumber(shuffleSwich);
                                mediaPlayer.reset();
                                isPlaying = false;
                                playerSeekBar.setProgress(0);
                                int nextSongListPosition = shuffleSwich;
                                LocalList.musicLists.get(currentSongListPosition).setPlaying(false);
                                LocalList.musicLists.get(nextSongListPosition).setPlaying(true);
                                LocalList.musicAdapter.notifyDataSetChanged();
                                onChanged(nextSongListPosition);
                            }
                        });
                    }
                    else {
                        shuffleFlag = false;
                        shuffleBtn2.setImageResource(R.drawable.ic_shuffle_adobe_express);
                    }
                }
            });
        } else {
            shuffleBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!shuffleFlag && repeatFlag == 0) {
                        shuffleFlag = true;
                        shuffleBtn2.setImageResource(R.drawable.ic_shuffleblack_adobe_express);
                    }

                    else if(!shuffleFlag && repeatFlag == 1) {
                        shuffleFlag = true;
                        shuffleBtn2.setImageResource(R.drawable.ic_shuffleblack_adobe_express);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                shuffleSwich = getRandomNumber(shuffleSwich);
                                mediaPlayer.reset();

                                isPlaying = false;

                                playerSeekBar.setProgress(0);

                                int nextSongListPosition = shuffleSwich;


                                MainActivity.musicPlayerList.get(currentSongListPosition).setPlaying(false);
                                MainActivity.musicPlayerList.get(nextSongListPosition).setPlaying(true);
                                OnlineList.musicAdapter.notifyDataSetChanged();


                                onChanged(nextSongListPosition);
                            }
                        });
                    }

                    else if(!shuffleFlag && repeatFlag == 2) {
                        shuffleFlag = true;
                        shuffleBtn2.setImageResource(R.drawable.ic_shuffleblack_adobe_express);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                shuffleSwich = getRandomNumber(shuffleSwich);
                                mediaPlayer.reset();
                                isPlaying = false;
                                playerSeekBar.setProgress(0);
                                int nextSongListPosition = shuffleSwich;
                                MainActivity.musicPlayerList.get(currentSongListPosition).setPlaying(false);
                                MainActivity.musicPlayerList.get(nextSongListPosition).setPlaying(true);
                                OnlineList.musicAdapter.notifyDataSetChanged();
                                onChanged(nextSongListPosition);
                            }
                        });
                    }
                    else {
                        shuffleFlag = false;
                        shuffleBtn2.setImageResource(R.drawable.ic_shuffle_adobe_express);
                    }
                }
            });
        }



        onChanged(Position.currentIndex);
        if(onOf) {
            endTime2.setText(convertToMMSS(list.getDuration()));
        } else {
            endTime2.setText(list.getDuration());
        }

        if(mediaPlayer.isPlaying()) {
            playPauseImg2.setImageResource(R.drawable.ic_stop_adobe_express);
        } else {
            playPauseImg2.setImageResource(R.drawable.ic_player_adobe_express);
        }
        setStatusLikeIcon();

        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLike) {
                    isLike = false;
                    deleteLikeSong();
                    likeIcon.setImageResource(R.drawable.heart_icon);
                }
                else {
                    isLike = true;
                    sendLikeSong();
                    likeIcon.setImageResource(R.drawable.heart_icon_fill);
                }
            }
        });


    }

    private void aa() {
        Intent intent = new Intent(this, Service.class);
        startService(intent);
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
        }
    }

    private void setInfoSong() {

        if(onOf) {
            list = LocalList.musicLists.get(Position.currentIndex);
        } else {
            list = MainActivity.musicPlayerList.get(Position.currentIndex);
        }
        playerName.setText(list.getTitle());
        playerArtis.setText(list.getArtist());
        playerName.setSelected(true);
        if(onOf) {
            endTime2.setText(convertToMMSS(list.getDuration()));
        } else {
            endTime2.setText(list.getDuration());
        }

        playPauseImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying) {
                    sendAction(Service.ACTION_PAUSE);
                }
                else {
                    sendAction(Service.ACTION_RESUME);
                }
            }
        });

        prevBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAction(Service.ACTION_PREV);
            }
        });

        nextBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAction(Service.ACTION_NEXT);
            }
        });

    }

    private void sendAction(int action) {
        Intent intent = new Intent(this, Service.class);
        intent.putExtra("action_music_service", action);
        startService(intent);
    }
    private void setStatusSong() {
        if(isPlaying) {
            playPauseImg2.setImageResource(R.drawable.ic_stop_adobe_express);
        }
        else {
            playPauseImg2.setImageResource(R.drawable.ic_player_adobe_express);
        }
    }

    public void sendLikeSong() {

        mAuth = FirebaseAuth.getInstance();

        String s = String.valueOf(mAuth.getUid());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference mDatabase = database.getReference("user/" + s + "/Your Like Song         /songList");


        mDatabase.child(MainActivity.musicPlayerList.get(Position.currentIndex).getTitle()).setValue(MainActivity.musicPlayerList.get(Position.currentIndex));
    }

    public void deleteLikeSong() {
        mAuth = FirebaseAuth.getInstance();

        String s = String.valueOf(mAuth.getUid());

        String a = String.valueOf(MainActivity.musicPlayerList.get(Position.currentIndex).getTitle());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference mDatabase = database.getReference("user/" + s + "/Your Like Song         /songList/" + a);

        mDatabase.removeValue();
    }

    public void setStatusLikeIcon() {
        mAuth = FirebaseAuth.getInstance();

        String s = String.valueOf(mAuth.getUid());

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference mDatabase = database.getReference("user/" + s + "/Your Like Song         /songList");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MusicListOnline musicList = dataSnapshot.getValue(MusicListOnline.class);
                    assert musicList != null;
                    if(musicList.getTitle().equals(MainActivity.musicPlayerList.get(Position.currentIndex).getTitle())) {
                        likeIcon.setImageResource(R.drawable.heart_icon_fill);
                        isLike = true;
                    }
                }
                if(!isLike) {
                    likeIcon.setImageResource(R.drawable.heart_icon);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void onChanged(int position) {
        aa();


        currentSongListPosition = position;

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                final int getTotalDuration = mp.getDuration();

                String generateDuration = String.format(Locale.getDefault(),"%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(getTotalDuration),
                        TimeUnit.MILLISECONDS.toSeconds(getTotalDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getTotalDuration)));

                endTime2.setText(generateDuration);
                isPlaying = true;

                mp.start();

                playerSeekBar.setMax(getTotalDuration);


                playPauseImg2.setImageResource(R.drawable.ic_stop_adobe_express);
            }
        });

        if(onOf) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (repeatFlag == 0 && shuffleFlag == false) {
                        mediaPlayer.reset();
                        isPlaying = false;
                        playerSeekBar.setProgress(0);
                        startTime2.setText("00:00");
                        int nextSongListPosition = currentSongListPosition + 1;
                        if(nextSongListPosition >= LocalList.musicLists.size()) {
                            nextSongListPosition = 0;
                        }
                        LocalList.musicLists.get(currentSongListPosition).setPlaying(false);
                        LocalList.musicLists.get(nextSongListPosition).setPlaying(true);
                        /*Position.getInstance().reset();*/
                        Position.currentIndex = nextSongListPosition;
                        Position.currentName = LocalList.musicLists.get(Position.currentIndex).getTitle();
                        LocalList.musicAdapter.updateList(LocalList.musicLists);
                        onChanged(nextSongListPosition);
                    }

                    else if (repeatFlag == 2 && shuffleFlag == false) {
                        mediaPlayer.reset();
                        isPlaying = false;
                        playerSeekBar.setProgress(0);
                        startTime2.setText("00:00");
                        int nextSongListPosition;
                        if (repeatOneFlag == 0) {
                            nextSongListPosition =  currentSongListPosition;
                            repeatOneFlag++;
                        }
                        else {
                            nextSongListPosition = currentSongListPosition + 1;
                            repeatOneFlag = 0;
                            repeatFlag = 0;
                            repeatBtn2.setImageResource(R.drawable.ic_repeat_adobe_express);
                        }
                        if(nextSongListPosition >= LocalList.musicLists.size()) {
                            nextSongListPosition = 0;
                        }
                        LocalList.musicLists.get(currentSongListPosition).setPlaying(false);
                        LocalList.musicLists.get(nextSongListPosition).setPlaying(true);
                        /*Position.getInstance().reset();*/
                        Position.currentIndex = nextSongListPosition;
                        Position.currentName = LocalList.musicLists.get(Position.currentIndex).getTitle();
                        LocalList.musicAdapter.updateList(LocalList.musicLists);
                        onChanged(nextSongListPosition);
                    }

                    else if (repeatFlag == 1 && shuffleFlag == false) {
                        mediaPlayer.reset();
                        isPlaying = false;
                        playerSeekBar.setProgress(0);
                        startTime2.setText("00:00");
                        int nextSongListPosition = currentSongListPosition;
                        LocalList.musicLists.get(currentSongListPosition).setPlaying(false);
                        LocalList.musicLists.get(nextSongListPosition).setPlaying(true);
                        /*Position.getInstance().reset();*/
                        Position.currentIndex = nextSongListPosition;
                        Position.currentName = LocalList.musicLists.get(Position.currentIndex).getTitle();
                        LocalList.musicAdapter.updateList(LocalList.musicLists);
                        onChanged(nextSongListPosition);
                    }
                    if(shuffleFlag && repeatFlag == 0) {
                        int i = -1;
                        i = getRandomNumber(i);
                        mediaPlayer.reset();
                        isPlaying = false;
                        playerSeekBar.setProgress(0);
                        startTime2.setText("00:00");
                        int nextSongListPosition = i;
                        LocalList.musicLists.get(currentSongListPosition).setPlaying(false);
                        LocalList.musicLists.get(nextSongListPosition).setPlaying(true);
                        /*Position.getInstance().reset();*/
                        Position.currentIndex = nextSongListPosition;
                        Position.currentName = LocalList.musicLists.get(Position.currentIndex).getTitle();
                        LocalList.musicAdapter.updateList(LocalList.musicLists);
                        onChanged(nextSongListPosition);
                    }
                    else if(shuffleFlag && repeatFlag == 1) {
                        mediaPlayer.reset();
                        isPlaying = false;
                        playerSeekBar.setProgress(0);
                        startTime2.setText("00:00");
                        int nextSongListPosition = currentSongListPosition;
                        LocalList.musicLists.get(currentSongListPosition).setPlaying(false);
                        LocalList.musicLists.get(nextSongListPosition).setPlaying(true);
                        /*Position.getInstance().reset();*/
                        Position.currentIndex = nextSongListPosition;
                        Position.currentName = LocalList.musicLists.get(Position.currentIndex).getTitle();
                        LocalList.musicAdapter.updateList(LocalList.musicLists);
                        onChanged(nextSongListPosition);
                    }
                    else if(shuffleFlag && repeatFlag == 2) {
                        mediaPlayer.reset();
                        isPlaying = false;
                        playerSeekBar.setProgress(0);
                        startTime2.setText("00:00");
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
                            repeatBtn2.setImageResource(R.drawable.ic_repeat_adobe_express);
                        }
                        LocalList.musicLists.get(currentSongListPosition).setPlaying(false);
                        LocalList.musicLists.get(nextSongListPosition).setPlaying(true);
                        /*Position.getInstance().reset();*/
                        Position.currentIndex = nextSongListPosition;
                        Position.currentName = LocalList.musicLists.get(Position.currentIndex).getTitle();
                        LocalList.musicAdapter.updateList(LocalList.musicLists);
                        onChanged(nextSongListPosition);
                    }
                }
            });
        } else {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (repeatFlag == 0 && shuffleFlag == false) {
                        mediaPlayer.reset();
                        isPlaying = false;
                        playerSeekBar.setProgress(0);
                        startTime2.setText("00:00");
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
                        startTime2.setText("00:00");
                        int nextSongListPosition;
                        if (repeatOneFlag == 0) {
                            nextSongListPosition =  currentSongListPosition;
                            repeatOneFlag++;
                        }
                        else {
                            nextSongListPosition = currentSongListPosition + 1;
                            repeatOneFlag = 0;
                            repeatFlag = 0;
                            repeatBtn2.setImageResource(R.drawable.ic_repeat_adobe_express);
                        }
                        if(nextSongListPosition >= LocalList.musicLists.size()) {
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
                        startTime2.setText("00:00");
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
                        startTime2.setText("00:00");
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
                        startTime2.setText("00:00");
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
                        startTime2.setText("00:00");
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
                            repeatBtn2.setImageResource(R.drawable.ic_repeat_adobe_express);
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

        setInfoSong();

    }

    public int getRandomNumber(int a) {
        Random ran = new Random();
        while(true) {
            if(onOf) {
                a = ran.nextInt(LocalList.musicLists.size());
            } else {
                a = ran.nextInt(MainActivity.musicPlayerList.size());
            }
            if(a != currentSongListPosition) {
                break;
            }
        }
        return a;
    }


    public String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return  String.format(Locale.getDefault(),"%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    @Override
    public void onBackPressed() {
        //Intent intent = new Intent(MusicPlayer.this, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //startActivity(intent);
        moveTaskToBack(true);
        overridePendingTransition(0, R.anim.view_hide);
        //super.onBackPressed();

    }

    @Override
    protected void onResume() {
        isLike = false;
        setStatusLikeIcon();
        playerSeekBar.setMax(mediaPlayer.getDuration());
        if(onOf) {
            list = LocalList.musicLists.get(Position.currentIndex);
        } else {
            list = MainActivity.musicPlayerList.get(Position.currentIndex);
        }
        playerName.setText(list.getTitle());
        playerArtis.setText(list.getArtist());
        playerName.setSelected(true);
        if(onOf) {
            endTime2.setText(convertToMMSS(list.getDuration()));
        } else {
            endTime2.setText(list.getDuration());
        }
        super.onResume();
    }
}