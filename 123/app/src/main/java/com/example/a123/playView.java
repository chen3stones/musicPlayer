package com.example.a123;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;


public class playView extends AppCompatActivity implements View.OnClickListener{
    private ImageButton beforeButton;
    private ImageButton nextButton;
    static public ImageButton playButton;
    private SeekBar seekBar;
    static public TextView playedTime;
    static public TextView totalTime;
    static public TextView song;
    static public MediaService.MyBinder mMybinder;

    private Handler mHandler = new Handler();
    static Intent MediaServiceIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_view);


      // Log.i("playView.songPosition:::::",String.valueOf(MainActivity.songPosition)+"DDD");

        initView();
        MediaServiceIntent = new Intent(this,MediaService.class);
        //获取权限
        if (ContextCompat.checkSelfPermission(playView.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(playView.this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
        }else{
            //权限已经给定，开始设置路径，准备播放
            bindService(MediaServiceIntent,mServiceConnection,BIND_AUTO_CREATE);

            startService(MediaServiceIntent);
        }


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


    /**
     * 权限回调方法
     */
    //@Override
    public  void onRequsetPermissionResult(int requsetCode, @NonNull String[]permissions,@NonNull int[] grantResults){
//        switch (requsetCode){
//            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    bindService(MediaServiceIntent,mServiceConnection,BIND_AUTO_CREATE);
                }else{
                    Toast.makeText(this,"请给予权限用于播放音乐",Toast.LENGTH_SHORT);
                }
//                break;
//            default:
//                break;
//        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMybinder = (MediaService.MyBinder) service;
            seekBar.setMax(mMybinder.getProgress());
            totalTime.setText(getTime(mMybinder.getTotoalTime()));
            song.setText(MainActivity.list.get(MainActivity.songPosition).getSong());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                @Override
                public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){
                    if(fromUser){
                        mMybinder.seekToPosition(seekBar.getProgress());
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar){

                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar){

                }
            });

            mHandler.post(mRunnable);


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void initView() {
        playButton = (ImageButton) findViewById(R.id.play);
        //pauseButton = (ImageButon) findViewById(R.id.pause);
        nextButton = (ImageButton) findViewById(R.id.next);
        beforeButton = (ImageButton) findViewById(R.id.brefore);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        playedTime = (TextView) findViewById(R.id.playedTime);
        totalTime = (TextView) findViewById(R.id.totalTime);
        song = (TextView) findViewById(R.id.song);
        playButton.setOnClickListener(this);
        //pauseButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        beforeButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                mMybinder.play_pause();
                break;
            case R.id.next:
                mMybinder.playnext();
                break;
            case R.id.brefore:
                mMybinder.before();
                break;
             default:
                 break;
        }
    }

    @Override
    protected void onDestroy() {
      super.onDestroy();
    }

    /**
     * 更新ui的runnable
    */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(mMybinder.getPlayPosition());
            playedTime.setText(getTime(mMybinder.getPlayPosition()));
            mHandler.postDelayed(mRunnable, 1000);
        }
    };

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
}
