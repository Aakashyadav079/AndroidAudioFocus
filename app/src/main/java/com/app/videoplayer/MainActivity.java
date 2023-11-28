package com.app.videoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.app.videoplayer.databinding.ActivityMainBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.net.URI;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AudioManager.OnAudioFocusChangeListener {
PlayerView playerView;
Button play,pause;
SimpleExoPlayer player;
ActivityMainBinding binding;
AudioManager audioManager;
AudioFocusRequest focusRequest;
public static final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        playerView = binding.playerView;
        binding.play.setOnClickListener(this);

    }

    public void initPlayer(){
        if(player==null){
            player =  new SimpleExoPlayer.Builder(this).build();
            playerView.setPlayer(player);
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse("https://bhojpurivideo.in/siteuploads/files/sfd3/1480/Dhaniya%20Ae%20Jaan(BhojpuriVideo.in).mp4"));
            player.setMediaItem(mediaItem);
            player.prepare();
            Log.d(TAG, "initPlayer: ");


        }
    }

    public void aquireAudioFocus(){
        AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();
        focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(mPlaybackAttributes)
                .setOnAudioFocusChangeListener(this)
                .build();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.requestAudioFocus(focusRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initPlayer();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initPlayer();

    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
        abandonAudiofocus();
    }

    private void abandonAudiofocus(){
        audioManager.abandonAudioFocusRequest(focusRequest);
    }

    @Override
    public void onClick(View view) {
         if(view.getId()==R.id.play){
             playPauseVideo();
         }
    }

    private void playPauseVideo() {
        if(player.isPlaying()){
            player.pause();
            abandonAudiofocus();
        }else{
            player.play();
            aquireAudioFocus();

        }
    }

    @Override
    public void onAudioFocusChange(int i) {
        Log.d(TAG, ": "+i);
        System.out.println("on focus change: " + i);
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d(TAG, "onAudioFocusChange: Focuse Gained");
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                Log.d(TAG, "onAudioFocusChange: Focus loss");
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d(TAG, "onAudioFocusChange: Focus transient");
                 break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.d(TAG, "onAudioFocusChange: Transient Duck");
                break;
        }
    }
}