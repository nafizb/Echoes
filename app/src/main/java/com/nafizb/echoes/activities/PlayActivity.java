package com.nafizb.echoes.activities;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.nafizb.echoes.R;
import com.nafizb.echoes.models.Records;
import com.nafizb.echoes.utils.MediaHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Nafiz on 7.12.2016.
 */

public class PlayActivity extends BaseActivity {
    public MediaHelper mediaHelper;
    public String id;
    public String title;

    @Bind(R.id.textview_title)
    TextView titleTextView;

    @Bind(R.id.button_play)
    public ImageView playButton;

    @OnClick(R.id.button_play)
    void playRecord() {
        if(mediaHelper.isPlaying()) {
            mediaHelper.stopPlaying();
            playButton.setAnimation(null);
        } else {
            mediaHelper.startPlaying(id);
            RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(700);

            playButton.startAnimation(anim);

            //mediaHelper.startPlaying(Environment.getExternalStorageDirectory().getAbsolutePath() + "play-cache.mp4");
        }
    }

    @OnClick(R.id.button_close)
    void closeActivity() {
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        super.baseInit();
        init();
    }
    public void init() {
        ButterKnife.bind(this);
        mediaHelper = new MediaHelper(this);
        Bundle bundle = getIntent().getExtras();

        String[] snippetData = bundle.getString("snippet").split("/-/");
        id = snippetData[0];
        title = snippetData[1];

        titleTextView.setText(title);
        //getRecordFile();
    }
    /* When initializing alert dialog, its crashes because of this activity is a dialog activity.
    Its disabled via overriding.
    */
    @Override
    public void initAlertDialog() {
    }

    public void getRecordFile() {
        progressDialog.setMessage(getString(R.string.text_play_loading));
        progressDialog.show();

        app.getRestService().downloadRecord(id).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public final void onCompleted() {
                        // do nothing
                    }

                    @Override
                    public final void onError(Throwable e) {
                        Log.e("EchoesError", e.getMessage());
                        progressDialog.dismiss();

                    }

                    @Override
                    public final void onNext(ResponseBody response) {
                        progressDialog.dismiss();
                        titleTextView.setText(title);
                        saveInCache(response);
                    }
                });
    }

    private boolean saveInCache(ResponseBody body) {
        try {
            File futureStudioIconFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "play-cache.mp4");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("echoes", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName("Playing");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}

