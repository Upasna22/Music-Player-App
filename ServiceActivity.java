package edu.upasna.cs478.audioserver;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Service;
import android.util.Log;

import edu.upasna.cs478.musicaidl.MusicPlayer;
public class ServiceActivity extends Service {


  protected static final String TAG = "ServiceActivity";
  String mCurrentSong ="";
    // creates a new mediaplayer instance
    private MediaPlayer mp = new MediaPlayer();

    int i =0;
    // Implement the Stub for this Object
    private final MusicPlayer.Stub mBinder = new MusicPlayer.Stub() {


         // Implement all the remote methods specifies in AIDL

        //Implementation of playSong() defined in AIDL file .It plays the specified song using the music player
        public void  playSong(String song){
            mCurrentSong = song;

            // Checking is song has been paused . If paused and played do reset
            if(i==1)
            {
                mp.reset();
                i=0;
            }
            // Get the song to be played from raw folder
            String filename = "android.resource://" + getApplicationContext().getPackageName() + "/raw/"+song;

            if(mp != null)
            {
                // If mp is playing and play is clicked again then stop it first
                if(mp.isPlaying())
                    mp.stop();
            }


            try {
                mp.setDataSource(getApplicationContext(), Uri.parse(filename));
            }catch(Exception e) {
                Log.e(TAG," IOException No input songs");
            }

            try {
                mp.prepare();
            }catch(Exception e){
                Log.e(TAG," IOException No input songs");
            }

            if(mp!= null) {
                //If mp is already playing then reset it otherwise start the song
                if (mp.isPlaying()) {
                    mp.seekTo(0);
                } else {
                    mp.seekTo(0);
                    mp.start();
                }

                Log.i(TAG , "The service was bound in the server");
            }else {
                Log.i(TAG ,"The service was not bound");
            }

        }
        //Implementation of pauseSong() defined in AIDL file .It pauses the song playing using the music player
        public void pauseSong() {

            // If musicplayer is playing then pause it
            if (mp != null) {
                if (mp.isPlaying()) {
                    mp.pause();
                    i=1;
                }
                Log.i(TAG, "The service was bound in the server");
            } else {
                Log.i(TAG, "The service was not bound");
            }
        }

        //Implementation of resumeSong() defined in AIDL file .It resumes the paused song using the music player
        public void resumeSong(){
            if (mp != null) {
                // If musicplayer is playing then do nothing otherwise start it
                if (mp.isPlaying()) {

                }
                else{
                    mp.start();
                }
                Log.i(TAG, "The service was bound in the server");
            } else {

                Log.i(TAG, "The service was not bound");
            }
        }

        //Implementation of stopSong() defined in AIDL file .It stops the playing song using the music player
        public void stopSong(){
            if (mp != null) {

                // If musicplayer is playing then stop it otherwise just reset it
                if (mp.isPlaying()) {

                    mp.stop();
                    mp.seekTo(0);//or mp.reset();
                }
                else{
                    if(mp.getCurrentPosition()!=0){
                        mp.seekTo(0);
                    }

                }
                Log.i(TAG, "The service was stopped in the server");
            } else {
                Log.i(TAG, "The service was not stopped in the server");
            }
        }

        };

    @Nullable //
    @Override

    // Return the binder object to client when onServiceConnected returns
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
