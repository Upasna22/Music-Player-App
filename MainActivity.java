package edu.upasna.cs478.playerclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import edu.upasna.cs478.musicaidl.MusicPlayer;

public class MainActivity extends AppCompatActivity {

    private MusicPlayer mMusicplayerService;
    protected static final String TAG = "MainActivity";

    // Attribute that checks is service is bound or not
    private boolean mIsBound = false;

    private ImageButton playbutton = null;
    private ImageButton stopbutton = null;
    private ImageButton pausebutton = null;
    private ImageButton resumebutton = null;
    private EditText edittext = null;
    private ListView listview =null;

    // Array list that stores the list of requests made by the user
    private ArrayList<String> requests = new ArrayList<String>();

    String pattern= "^[1-5]$";
    String status ="";
    String option ;
    ArrayAdapter<String> adapter;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Getting referenece to layout elements
        playbutton = (ImageButton) findViewById(R.id.playbtn);
        stopbutton = (ImageButton) findViewById(R.id.stopbtn);
        pausebutton = (ImageButton) findViewById(R.id.pausebtn);
        resumebutton =(ImageButton)findViewById(R.id.resumebtn);
        edittext = (EditText) findViewById(R.id.edittxt);
        listview =(ListView)findViewById(R.id.listview);

        adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.list_item,requests);

        // Associate the listview with the Array Adapter
        listview.setAdapter(adapter);

        // Setting each onclicklistener
        playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    // If the service is already bound then we can play song
                    if (mIsBound) {

                        // If entered text is not from 1-5
                        if (edittext.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "Enter a track from 1 to 5 ", Toast.LENGTH_SHORT).show();
                        }
                        //If text matches 1-5 then get the entered text and call the Service playsong method.
                        else if(edittext.getText().toString().matches(pattern)) {
                            // Making changes to the visibility of other buttons
                            playbutton.setVisibility(View.INVISIBLE);
                            pausebutton.setVisibility(View.VISIBLE);
                            stopbutton.setVisibility(View.VISIBLE);
                            resumebutton.setVisibility(View.INVISIBLE);
                            // Calling playsong method of the bound service
                            mMusicplayerService.playSong("song" + edittext.getText().toString());
                            edittext.setEnabled(false);
                            // Retrieve entered text
                            option =edittext.getText().toString();
                            // Add request made to the list of requests
                            requests.add("Song "+option+" Played");
                            // Notifying the change in listview items
                            adapter.notifyDataSetChanged();
                            status ="played";
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Enter a track from 1 to 5 ", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Log.i(TAG ,"Client -Music service is not bound");
                    }

                }catch(RemoteException e){
                    Log.e(TAG ,e.toString());
                }
            }
        });

        pausebutton.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {


                try
                {
                    // If the service is already bound then we can play song
                    if(mIsBound)
                    {

                        // Making changes to the visibility of other buttons
                        playbutton.setVisibility(View.VISIBLE);
                        pausebutton.setVisibility(View.INVISIBLE);
                        stopbutton.setVisibility(View.VISIBLE);
                        resumebutton.setVisibility(View.VISIBLE);
                        // Calling pausesong method of the bound service
                        mMusicplayerService.pauseSong();
                        option =edittext.getText().toString();
                        requests.add("Song "+option+" Paused");
                        adapter.notifyDataSetChanged();
                        edittext.setEnabled(true);
                    }
                    else {
                        Log.i(TAG ,"Client-Music Service was not bound");
                    }
                }catch(RemoteException e){
                    Log.e(TAG , e.toString());
                }
            }

        });

        resumebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                try {

                    // If the service is already bound then we can resume song
                    if (mIsBound) {

                        // Making changes to the visibility of other buttons
                        resumebutton.setVisibility(View.INVISIBLE);
                        playbutton.setVisibility(View.INVISIBLE);
                        stopbutton.setVisibility(View.VISIBLE);
                        pausebutton.setVisibility(View.VISIBLE);
                        // Calling resumesong method of the bound service
                        mMusicplayerService.resumeSong();
                        option =edittext.getText().toString();
                        requests.add("Song " +option+ " Resumed");
                        adapter.notifyDataSetChanged();
                        // If song has been played
                        if (status == "played") {
                            edittext.setEnabled(false);
                        } else {
                            edittext.setEnabled(true);
                        }
                    } else {
                        Log.i(TAG, "CLient - service was not bound");
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, e.toString());
                }
            }
        });

        stopbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                try {

                    // If the service is already bound then we can stop song
                    if (mIsBound) {

                        // Making changes to the visibility of other buttons
                        resumebutton.setVisibility(View.INVISIBLE);
                        playbutton.setVisibility(View.VISIBLE);
                        stopbutton.setVisibility(View.INVISIBLE);
                        pausebutton.setVisibility(View.INVISIBLE);
                        // Calling stopsong method of the bound service
                        mMusicplayerService.stopSong();
                        option =edittext.getText().toString();
                        requests.add("Song "+option +" Stopped");
                        adapter.notifyDataSetChanged();
                        edittext.setEnabled(true);
                    } else {
                        Log.i(TAG, "Client - Service was not bound");
                    }
                }catch(RemoteException e) {
                    Log.e(TAG, e.toString());
                }
            }
        });



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    // Create the ServiceConnection object for InterProcessCommunication

    private ServiceConnection mServiceConnection  = new ServiceConnection() {
        // Service Connected return true is binding is successful and indirectly returns IBinder object
        public void onServiceConnected(ComponentName className, IBinder iservice) {
            mMusicplayerService = MusicPlayer.Stub.asInterface(iservice);
            mIsBound = true;
        }
        //Service Disconnected is called when service is unbound
        public void onServiceDisconnected(ComponentName className) {
            mMusicplayerService = null;
            mIsBound = false;
    }
};
    protected void onResume() {
        super.onResume();

        if (!mIsBound) {
            boolean b = false;
            Intent i = new Intent(MusicPlayer.class.getName());
            // Must make intent explicit or lower target API level to 19.
            ResolveInfo info = getPackageManager().resolveService(i, Context.BIND_AUTO_CREATE);
            i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

            b = bindService(i, this.mServiceConnection, Context.BIND_AUTO_CREATE);
            if (b) {
                Log.i(TAG, "Upasna says bindService() succeeded!");
            } else {
                Log.i(TAG, "Upasna says bindService() failed!");
            }
        }
    }

    // Unbind from KeyGenerator Service
    @Override
    protected void onPause() {

        if (mIsBound) {
            super.onPause();
          //  unbindService(this.mServiceConnection);
         //   mIsBound = false;
        }
        //super.onPause();
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public void onDestroy(){
        super.onDestroy();
        // Unbind service when client is destroyed and stopsong
        try {
            mMusicplayerService.stopSong();
        }catch(RemoteException e) {
            Log.e(TAG, e.toString());
        }
        unbindService(this.mServiceConnection);
        mIsBound = false;
    }
}
