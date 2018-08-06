package com.example.a123;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.util.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView mListView;
    static public List<Song> list;  //为了播放界面可以访问
    static public int songPosition;
    static  public TextView songInBar;
    private MyAdapter adapter;
    static Intent MediaServiceIntent;
    private MediaService.MyBinder myBinder;
    private boolean scan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //侧边栏按钮部分的功能
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //侧边栏完毕
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            initView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requsetCode,String[] permissions,int []grantResults){
        switch (requsetCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initView();
                }else{
                    Toast.makeText(this,"你TM的倒是给权限啊",Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
    }
    /*
    初始化view
     */
    private void initView(){
        mListView = (ListView)findViewById(R.id.main_listview);
        songInBar = (TextView) findViewById(R.id.song_in_bar);


        list = new ArrayList<>();
        //把扫描到的音乐赋给list
        if(!scan){
            list = MusicUtils.getMusicData(this);
            scan = true;
        }
        adapter = new MyAdapter(this,list);
        mListView.setAdapter(adapter);
        //mListView.setSelector(R.color.colorClicked);
        //mListView.

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                songPosition = position;
                //Intent intent = new Intent(MainActivity.this,playView.class);
                //songInBar.setText(list.get(position).getSong());
                //startActivity(intent);
                MediaServiceIntent = new Intent(MainActivity.this,MediaService.class);
                bindService(MediaServiceIntent,mServiceConnection,BIND_AUTO_CREATE);

                startService(MediaServiceIntent);
            }
        });

        songInBar.setClickable(true);
        songInBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,playView.class);
                startActivity(intent);
            }
        });



    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected (MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_music) {
            // Handle the camera action
        } else if (id == R.id.my_favorite) {

        } else if (id == R.id.my_music_list) {

        }else if(id == R.id.recent_play){

        } else if (id == R.id.setting) {

        } else if (id == R.id.quit) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MediaService.MyBinder) service;
            myBinder.setSongInBar(songInBar);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

}
