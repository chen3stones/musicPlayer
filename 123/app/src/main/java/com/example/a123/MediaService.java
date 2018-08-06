package com.example.a123;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class MediaService extends Service {
    private int isPlaying = -1;
    private MyBinder myBinder = new MyBinder();
    private static final String TAG = "mediaSer";
    //初始化MediaPlayer
    public MediaPlayer mediaPlayer = new MediaPlayer();
    static Song song = MainActivity.list.get(MainActivity.songPosition);


    public MediaService() {
      //  playMusic(MainActivity.songPosition);
    }

    public int onStartCommand(Intent intent,int flags,int startId){
        MainActivity.songInBar.setText(MainActivity.list.get(MainActivity.songPosition).getSong());
        if(MainActivity.songPosition != isPlaying){
            playMusic(MainActivity.songPosition);
        }
        return super.onStartCommand(intent,flags,startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class MyBinder extends Binder{
        /**
         * 关闭播放器
         */
        void closeMedia(){
            if(mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
        boolean is_playing(){
            return mediaPlayer == null;
        }
        /**
         * 获取歌曲长度
         */
        public int getProgress(){
            return mediaPlayer.getDuration();
        }

        /**
         * 获取播放位置
         */
        public int getPlayPosition(){
            return mediaPlayer.getCurrentPosition();
        }

        /**
         * 播放指定位置
         */
        public void seekToPosition(int msec){
            mediaPlayer.seekTo(msec);
        }

        /**
         * 获取歌曲总时间
         */
        public int getTotoalTime(){
            return mediaPlayer.getDuration();
        }
        /**
         * 播放暂停
         */
        public void play_pause(){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                playView.playButton.setImageDrawable(getResources().getDrawable(R.drawable.onpause));
            }else {
                mediaPlayer.start();
                playView.playButton.setImageDrawable(getResources().getDrawable(R.drawable.onplaying));
            }
        }

        /**
         * 下一首
         */
        public void playnext(){
            if(MainActivity.songPosition == MainActivity.list.size() - 1){
                Toast.makeText(MediaService.this,"客官，这是最后一首歌",Toast.LENGTH_SHORT).show();
            }
            else{
                MainActivity.songPosition++;
                setPlayedTime(playView.playedTime,0);
                playView.song.setText(MainActivity.list.get(MainActivity.songPosition).getSong());
                playMusic(MainActivity.songPosition);
                setTotalTime(playView.totalTime);
            }
        }

        /**
         * 上一首
         */
        public void before(){
            if(MainActivity.songPosition == 0){
                Toast.makeText(MediaService.this,"客官，这是第一首歌",Toast.LENGTH_SHORT).show();
            }
            else{
                MainActivity.songPosition--;
                setPlayedTime(playView.playedTime,0);
                playView.song.setText(MainActivity.list.get(MainActivity.songPosition).getSong());
                playMusic(MainActivity.songPosition);
                setTotalTime(playView.totalTime);
            }
        }
        /**
         * 设置总时间
         */
        public void setTotalTime(TextView view){
            view.setText(getTime(mediaPlayer.getDuration()));
        }
        /**
         * 设置播放时间
         */
        public void setPlayedTime(TextView view,int time){
            view.setText(getTime(time));
        }
        /**
         * 设置播放的歌曲
         */
        public void setSongInBar(TextView view){
            view.setText(song.getSong());
        }
    }



    /**
     * 播放音乐
     */
    public void playMusic(int position){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        try{
            MainActivity.songInBar.setText(MainActivity.list.get(MainActivity.songPosition).getSong());
            isPlaying = MainActivity.songPosition;
            mediaPlayer.setDataSource(MainActivity.list.get(position).getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            //Toast.makeText(playView.this,sourceUri.toString(),Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //时间转换
    public String getTime(int time){
        String min=  (time/(1000*60))+"";
        String second= (time%(1000*60)/1000)+"";
        if(min.length()<2){
            min=0+min;
        }
        if(second.length()<2){
            second=0+second;
        }
        return min+":"+second;

    }


    /**
     * 获取URI的方法
     */
    public Uri queryUriforAudio(String path)
    {
        File file = new File(path);
        final String where = MediaStore.Audio.Media.DATA + "='"+file.getAbsolutePath()+"'";
        Cursor cursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, where, null, null);
        if (cursor == null) {
            Log.d("uritest", "queryUriforAudio: uri为空 1");
            return null;
        }
        int id = -1;
        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                id = cursor.getInt(0);
            }
            cursor.close();
        }
        if(id==-1)
        {
            Log.d("uritest", "queryUriforAudio: uri为空 2");
            return null;
        }
        return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
    }

}
