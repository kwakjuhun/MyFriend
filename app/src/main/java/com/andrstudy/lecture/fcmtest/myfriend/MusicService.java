package com.andrstudy.lecture.fcmtest.myfriend;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.widget.Toast;

public class MusicService extends Service {
    MediaPlayer media;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("뮤직 플레이어");
        builder.setContentText("음악 재생 중");
        startForeground(1, builder.build());
        try{
            String[] musics = getAllAudio();
            media = new MediaPlayer();
            media.reset();
            int r = (int)Math.floor(Math.random()*musics.length);
            media.setDataSource(musics[r]);
            media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try{
                        mp.reset();
                        String[] musics = getAllAudio();
                        int r =  (int) Math.floor(Math.random()*musics.length);
                        mp.setDataSource(musics[r]);
                        mp.prepare();
                        mp.start();
                    }
                    catch(Exception e){
                        toast(e.toString());
                    }
                }
            });
            media.prepare();
            media.start();
        }catch(Exception e){
            toast(e.toString());
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String[] getAllAudio() {
        String[] result = null;
        try {
            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media.DATA},null,null,null);
            if (cursor.getCount() == 0) return null;
            result = new String[cursor.getCount()];
            cursor.moveToFirst();
            result[cursor.getPosition()] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            while (cursor.moveToNext()){
                result[cursor.getPosition()] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            }
            cursor.close();
        } catch(Exception e){
            toast(e.toString());
        }
        return result;
    }
    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        media.reset();
    }



}
