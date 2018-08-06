package com.example.a123;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by user on 2016/6/24.
 * 放置音乐
 */
public class Song {
    /**
     * 歌手
     */
    public String singer;
    /**
     * 歌曲名
     */
    public String song;
    /**
     * 歌曲的地址
     */
    public String path;
    /**
     * 歌曲长度
     */
    public int duration;
    /**
     * 歌曲的大小
     */
    public long size;
    /**
     歌曲的图片
     */
    public Bitmap pic;


    public Song(){

    }
    public Song(String song,String singer,String path,int duration,long size){
        this.song = song;
        this.singer = singer;
        this.path = path;
        this.duration = duration;
        this.size = size;
    }

    public void setSinger(String singer){
        this.singer = singer;
    }
    public  void setSong(String song){
        this.song = song;
    }
    public void setPath(String path){
        this.path = path;
    }
    public  void setDuration(int duration){
        this.duration = duration;
    }
    public  void setSize(long size){
        this.size = size;
    }




    public String getSong(){
        return song;
    }
    public String getSinger(){
        return singer;
    }
    public String getPath(){
        return path;
    }
    public  int getDuration(){
        return duration;
    }
    public  long getSize(){
        return size;
    }

}
