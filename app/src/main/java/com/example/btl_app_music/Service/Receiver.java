package com.example.btl_app_music.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.btl_app_music.Service.Service;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int actionMusic = intent.getIntExtra("action_music", 0);

        Intent intentService = new Intent(context, Service.class);
        intentService.putExtra("action_music_service", actionMusic);

        context.startService(intentService);
    }
}
