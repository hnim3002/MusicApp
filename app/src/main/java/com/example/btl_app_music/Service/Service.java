package com.example.btl_app_music.Service;


//import static com.example.btl_app_music.MainActivity.mediaPlayer;
import static com.example.btl_app_music.Service.MyApplication.CHANNEL_ID;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.btl_app_music.Fragment.SubFragment.LocalList;
import com.example.btl_app_music.Fragment.SubFragment.OnlineList;
import com.example.btl_app_music.MainActivity;
import com.example.btl_app_music.Object.MusicList;
import com.example.btl_app_music.Position;
import com.example.btl_app_music.R;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;

public class Service extends android.app.Service {

    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_NEXT = 3;
    public static final int ACTION_PREV = 4;
    public static final int ACTION_CLEAR = 5;
    public static final int ACTION_START = 6;
    private boolean isPlaying = false;

    public static Timer timer = new Timer();

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    private MusicList list = new MusicList();


    @Override
    public void onCreate() {
        super.onCreate();


        Log.e("Tincode", "MyService");
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(MainActivity.onOf) {
            if(list != LocalList.musicLists.get(Position.currentIndex)) {
                list = LocalList.musicLists.get(Position.currentIndex);
                startMusic(list);
                sendNotification(list);
            }

        } else {
            if(list !=  MainActivity.musicPlayerList.get(Position.currentIndex)) {
                list =  MainActivity.musicPlayerList.get(Position.currentIndex);
                startMusic(list);
                sendNotification(list);
            }
        }

        if((MainActivity.repeatFlag == 1 || MainActivity.repeatFlag == 2) && MainActivity.repeatIsPlaying) {
            startMusic(list);
            sendNotification(list);
        }

        int actionMusic = intent.getIntExtra("action_music_service", 0);
        handleActionMusic(actionMusic);

        return START_NOT_STICKY;
    }

    private void handleActionMusic(int action) {
        switch (action) {
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
            case ACTION_NEXT:
                nextMusic();
                break;
            case ACTION_PREV:
                prevMusic();
                break;
            case ACTION_CLEAR:
                mediaPlayer.pause();
                stopSelf();
                sendAction(ACTION_CLEAR);
                break;
        }
    }


    private void prevMusic() {


        if(MainActivity.onOf) {
            int prevSongListPosition = Position.currentIndex - 1;
            if(prevSongListPosition < 0) {
                prevSongListPosition = LocalList.musicLists.size() - 1;
            }
            LocalList.musicLists.get(Position.currentIndex).setPlaying(false);
            LocalList.musicLists.get(prevSongListPosition).setPlaying(true);

            /*Position.getInstance().reset();*/
            Position.currentIndex = prevSongListPosition;

            Position.currentName = LocalList.musicLists.get(Position.currentIndex).getTitle();

            LocalList.musicAdapter.notifyDataSetChanged();
            list = LocalList.musicLists.get(Position.currentIndex);
            startMusic(LocalList.musicLists.get(Position.currentIndex));
            sendNotification(LocalList.musicLists.get(Position.currentIndex));
            sendAction(ACTION_PREV);
        } else {
            int prevSongListPosition = Position.currentIndex - 1;
            if(prevSongListPosition < 0) {
                prevSongListPosition = MainActivity.musicPlayerList.size() - 1;
            }
            MainActivity.musicPlayerList.get(Position.currentIndex).setPlaying(false);
            MainActivity.musicPlayerList.get(prevSongListPosition).setPlaying(true);

            /*Position.getInstance().reset();*/
            Position.currentIndex = prevSongListPosition;

            Position.currentName = MainActivity.musicPlayerList.get(Position.currentIndex).getTitle();

            OnlineList.musicAdapter.notifyDataSetChanged();
            list = MainActivity.musicPlayerList.get(Position.currentIndex);
            startMusic(MainActivity.musicPlayerList.get(Position.currentIndex));
            sendNotification(MainActivity.musicPlayerList.get(Position.currentIndex));
            sendAction(ACTION_PREV);
        }


    }

    private void nextMusic() {


        if(MainActivity.onOf) {
            int nextSongListPosition = Position.currentIndex + 1;
            if(nextSongListPosition >= LocalList.musicLists.size()) {
                nextSongListPosition = 0;
            }
            LocalList.musicLists.get(Position.currentIndex).setPlaying(false);
            LocalList.musicLists.get(nextSongListPosition).setPlaying(true);
            /*Position.getInstance().reset();*/
            Position.currentIndex = nextSongListPosition;
            Position.currentName = LocalList.musicLists.get(Position.currentIndex).getTitle();
            LocalList.musicAdapter.notifyDataSetChanged();
            list = LocalList.musicLists.get(Position.currentIndex);
            startMusic(LocalList.musicLists.get(Position.currentIndex));
            sendNotification(LocalList.musicLists.get(Position.currentIndex));
            sendAction(ACTION_NEXT);
        } else {
            int nextSongListPosition = Position.currentIndex + 1;
            if(nextSongListPosition >= MainActivity.musicPlayerList.size()) {
                nextSongListPosition = 0;
            }
            MainActivity.musicPlayerList.get(Position.currentIndex).setPlaying(false);
            MainActivity.musicPlayerList.get(nextSongListPosition).setPlaying(true);
            /*Position.getInstance().reset();*/
            Position.currentIndex = nextSongListPosition;
            Position.currentName = MainActivity.musicPlayerList.get(Position.currentIndex).getTitle();
            OnlineList.musicAdapter.notifyDataSetChanged();
            list = MainActivity.musicPlayerList.get(Position.currentIndex);
            startMusic(MainActivity.musicPlayerList.get(Position.currentIndex));
            sendNotification(MainActivity.musicPlayerList.get(Position.currentIndex));
            sendAction(ACTION_NEXT);
        }


    }

    private void resumeMusic() {
        if(!isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            if(MainActivity.onOf) {
                sendNotification(LocalList.musicLists.get(Position.currentIndex));
            } else {
                sendNotification(MainActivity.musicPlayerList.get(Position.currentIndex));
            }

        }
        sendAction(ACTION_RESUME);
    }

    private void pauseMusic() {
        if(isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            if(MainActivity.onOf) {
                sendNotification(LocalList.musicLists.get(Position.currentIndex));
            } else {
                sendNotification(MainActivity.musicPlayerList.get(Position.currentIndex));
            }
        }
        sendAction(ACTION_PAUSE);
    }

    private void sendNotification(MusicList list) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        /*Intent notifyIntent = new Intent(this,  MusicPlayer.class);
// Set the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// Create the PendingIntent
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );*/


      /*  // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, MusicPlayer.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(getNotificationId(),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);*/

        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "tag");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.ic_music)
                .setSubText("Sound")
                .setContentTitle(list.getTitle())
                .setContentText(list.getArtist())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1,2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()));

        if(isPlaying) {
            notificationBuilder
                    .addAction(R.drawable.ic_back_adobe_express, "Prev", getPendingIntent(this, ACTION_PREV))
                    .addAction(R.drawable.ic_pause, "pause", getPendingIntent(this, ACTION_PAUSE))
                    .addAction(R.drawable.ic_next_adobe_express, "Next", getPendingIntent(this, ACTION_NEXT))
                    .addAction(R.drawable.ic_close, "close", getPendingIntent(this, ACTION_CLEAR));
        }
        else {
            notificationBuilder
                    .addAction(R.drawable.ic_back_adobe_express, "Prev", getPendingIntent(this, ACTION_PREV))
                    .addAction(R.drawable.ic_play_arrow, "pause", getPendingIntent(this, ACTION_RESUME))
                    .addAction(R.drawable.ic_next_adobe_express, "Next", getPendingIntent(this, ACTION_NEXT))
                    .addAction(R.drawable.ic_close, "close", getPendingIntent(this, ACTION_CLEAR));
        }
        Notification notification = notificationBuilder.build();

        startForeground(1,notification);
    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, Receiver.class);
        intent.putExtra("action_music", action);


        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void startMusic(MusicList list) {

        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(list.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendAction(ACTION_START);
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Tincode", "stop");

    }

    private void sendAction(int action) {
        Intent intent = new Intent("send_data");
        Bundle bundle = new Bundle();
        bundle.putBoolean("isplaying", isPlaying);
        bundle.putInt("action", action);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        mediaPlayer.stop();
        stopSelf();
        sendAction(ACTION_CLEAR);
    }
}
