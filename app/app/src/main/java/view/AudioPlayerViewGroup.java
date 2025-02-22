/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.unfoldingword.mobile.R;

import model.AudioMarker;
import model.DownloadState;
import singletons.UWAudioPlayer;

/**
 * Created by Fechner on 9/18/15.
 */
public class AudioPlayerViewGroup implements UWAudioPlayer.UWAudioPlayerListener{

    private ViewGroup downloadViewGroup;
    private ViewGroup downloadingAudioViewGroup;
    private ViewGroup controlsViewGroup;
    private ImageButton playPauseButton;
    private TextView currentTimeTextView;
    private TextView endTimeTextView;
    private Button downloadButton;
    private Context context;

    private SeekBar seekBar;
    private AudioPlayerViewGroupListener listener;

    public AudioPlayerViewGroup(Context context, View containingView, AudioPlayerViewGroupListener listener) {
        this.context = context;
        this.listener = listener;
        getViews(containingView);
        setupListeners();
        updateViews();
    }

    public void onResume(){
        UWAudioPlayer.getInstance(context).addListener(this);
    }

    public void onPause(){
        UWAudioPlayer.getInstance(context).removeListener(this);
    }

    private void getViews(View containingView){

        playPauseButton = (ImageButton) containingView.findViewById(R.id.audio_player_play_pause_button);
        currentTimeTextView = (TextView) containingView.findViewById(R.id.audio_player_current_time_text_view);
        endTimeTextView = (TextView) containingView.findViewById(R.id.audio_player_end_time_text_view);
        seekBar = (SeekBar) containingView.findViewById(R.id.audio_player_progress);
        downloadButton = (Button) containingView.findViewById(R.id.reading_audio_download_button);

        downloadViewGroup = (ViewGroup) containingView.findViewById(R.id.download_audio_layout);
        downloadingAudioViewGroup = (ViewGroup) containingView.findViewById(R.id.downloading_audio_layout);
        controlsViewGroup = (ViewGroup) containingView.findViewById(R.id.audio_player_controls);
    }

    private void setupListeners(){

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.downloadClicked();
            }
        });
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseClicked();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private boolean wasPlaying = false;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                wasPlaying = UWAudioPlayer.getInstance(context).isPlaying();
                UWAudioPlayer.getInstance(context).pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekChange();
                if(wasPlaying) {
                    UWAudioPlayer.getInstance(context).play();
                    wasPlaying = false;
                }
            }
        });
//        seekBar.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                seekChange();
//                return false;
//            }
//        });
        UWAudioPlayer.getInstance(context).addListener(this);
    }

    private void updateViews(){
        updateSeekBar();
        updatePlayPause();
    }

    private void updateSeekBar(){
        AudioMarker marker = UWAudioPlayer.getInstance(context).getCurrentMarker();
        int progress = UWAudioPlayer.getInstance(context).getCurrentTime();
        if(marker != null && progress >= 0){
            seekBar.setMax((int) marker.getDuration());
            seekBar.setProgress(progress);
            updateLabelsForTimes(progress, marker.getDuration());
        }
    }

    private void updateSeekBar(int duration, int progress){
        seekBar.setMax(duration);
        seekBar.setProgress(progress);
        updateLabelsForTimes(progress, duration);
    }

    private void updatePlayPause(){

        boolean playing = UWAudioPlayer.getInstance(context).isPlaying();
        playPauseButton.setImageResource((playing) ? R.drawable.pause : R.drawable.play);
    }

    private void seekChange(){
        UWAudioPlayer.getInstance(context).seekTo(seekBar.getProgress());
    }

    private void playPauseClicked(){

        UWAudioPlayer.getInstance(context).togglePlay();
    }

    private void updateLabelsForTimes(long elapsedInMill, long totalInMill){

        String currentTime = getTimeStringFromSeconds(elapsedInMill / 1000);
        currentTimeTextView.setText(currentTime);
        String endTime = getTimeStringFromSeconds(totalInMill / 1000);
        endTimeTextView.setText(endTime);
    }

    private String getTimeStringFromSeconds(long seconds){
        long numOfSeconds = seconds % 60;
        String secondsText = (numOfSeconds < 10)? "0" + Long.toString(numOfSeconds) : Long.toString(numOfSeconds);
        return Long.toString((long) Math.floor(seconds / 60.0)) + ":" + secondsText;
    }

    public void resetViews(){
        controlsViewGroup.setVisibility(View.VISIBLE);
        downloadViewGroup.setVisibility(View.GONE);
    }

    public void handleDownloadState(DownloadState state){

        if(state == DownloadState.DOWNLOAD_STATE_DOWNLOADED){
            resetViews();
        }
        else{
            controlsViewGroup.setVisibility(View.GONE);
            downloadViewGroup.setVisibility(View.VISIBLE);
            downloadingAudioViewGroup.setVisibility((state == DownloadState.DOWNLOAD_STATE_DOWNLOADING)? View.VISIBLE : View.GONE);
            downloadButton.setVisibility((state == DownloadState.DOWNLOAD_STATE_DOWNLOADING)? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void paused() {
        updatePlayPause();
    }

    @Override
    public void update(long duration, long progress) {
        updateSeekBar((int)duration, (int) progress);
        updateLabelsForTimes(progress, duration);
    }

    @Override
    public void started() {
        updatePlayPause();
    }

    public interface AudioPlayerViewGroupListener{
        void downloadClicked();
    }
}
