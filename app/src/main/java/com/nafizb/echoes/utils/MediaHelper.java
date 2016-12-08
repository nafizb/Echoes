package com.nafizb.echoes.utils;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.nafizb.echoes.activities.PlayActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Nafiz on 5.12.2016.
 */

public class MediaHelper {
    private static final String LOG_TAG = "AudioRecordTest";
    Activity activity;

    private static String mFileName = null;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private boolean recording = false;
    private boolean playing = false;

    public MediaHelper(Activity activity) {
        this.activity = activity;

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/record-temp.mp4";
        File file = new File(mFileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startPlaying(String id) {
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mPlayer.setDataSource("http://birebirygslys.com:3000/records/" + id);
            mPlayer.prepare(); // might take long! (for buffering, etc)

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
            Log.e(LOG_TAG, e.getMessage());
        }

        mPlayer.start();
        playing = true;

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(activity != null) {
                    ((PlayActivity) activity).playButton.setAnimation(null);
                }
            }
        });

    }

    public void stopPlaying() {
        mPlayer.release();
        mPlayer = null;

        playing = false;
    }

    public void startRecording() {
        mRecorder = new MediaRecorder();

        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            recording = true;
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();

    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        recording = false;
    }

    public String getFileName() {
        return mFileName;
    }

    public boolean isRecording() {
        return recording;
    }
    public boolean isPlaying() {
        return playing;
    }
}
