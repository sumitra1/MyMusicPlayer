package com.sumitrakumarishaw.mymusicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> songname;
    ArrayList<String> songpath;
    MediaPlayer mP;


    ListView lv;
    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0&&grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                getMusic();

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songname=new ArrayList<>();
        songpath=new ArrayList<>();

        lv=findViewById(R.id.songslist);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);

        }
        else{
            getMusic();
        }
        ArrayAdapter<String> arrayadapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,songname);
lv.setAdapter(arrayadapter);

lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(mP!=null){
            mP.stop();
            mP.release();
            mP=null;
            Toast.makeText(getApplicationContext(),"music stopped",Toast.LENGTH_SHORT).show();
        }
        return true;
    }
});
lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mP!=null) {
            if (mP.isPlaying()) {
                mP.pause();
                Toast.makeText(getApplicationContext(),"music paused",Toast.LENGTH_SHORT).show();
            } else {
                mP.start();
                Toast.makeText(getApplicationContext(),"music played",Toast.LENGTH_SHORT).show();
            }
        }
        if(mP==null){
            mP=MediaPlayer.create(getApplicationContext(), Uri.parse(songpath.get(position)));
            mP.start();
            Toast.makeText(getApplicationContext(),"music started",Toast.LENGTH_SHORT).show();
        }
    }
});

    }
    public  void getMusic()
    {
        ContentResolver contentresolver=getContentResolver();
        Cursor cursor=contentresolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,null);

        if (cursor != null) {
            if(!cursor.moveToFirst()){
                Log.i("Error","no media on this device");
            }
            else
            {
                int displayname=cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                int fullpath=cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do{
                String display=cursor.getString(displayname);
                String path=cursor.getString(fullpath);


                songname.add(display);
                songpath.add(path);
            }while (cursor.moveToNext());

            }


        }
        else{
            Log.i("Error","No data returned");
        }

    }
}
