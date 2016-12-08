package com.nafizb.echoes.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.nafizb.echoes.BaseApplication;
import com.nafizb.echoes.R;
import com.nafizb.echoes.fragments.MapFragment;
import com.nafizb.echoes.models.Records;
import com.nafizb.echoes.utils.MediaHelper;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Nafiz on 30.11.2016.
 */

public class RecordActivity extends BaseActivity {
    public MediaHelper mediaHelper;

    @Bind(R.id.edittext_title)
    TextView titleEditText;

    @Bind(R.id.button_record)
    ImageView recordButton;

    double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Bundle bundle = getIntent().getExtras();
        lat = bundle.getDouble("lat");
        lon = bundle.getDouble("lon");

        ButterKnife.bind(this);
        super.baseInit();
        init();
    }
    public void init() {
        mediaHelper = new MediaHelper(this);
    }

    //When initializing alert dialog, its crashes because of this activity is a dialog activity.
    //Its disabled via overriding.
    @Override
    public void initAlertDialog() {
    }

    @OnClick(R.id.button_record)
    void recordButton() {
        if(mediaHelper.isRecording()) {
            mediaHelper.stopRecording();
            recordButton.setAnimation(null);

        } else {
            mediaHelper.startRecording();
            RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(700);

            recordButton.startAnimation(anim);
        }
    }

    @OnClick(R.id.button_send)
    void sendButton() {
        createMeta();
        //uploadFile(mediaHelper.getFileName());
    }

    void createMeta() {
        MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container);

        final Records record = new Records();

        record.title = titleEditText.getText().toString();
        record.setLocation(lat, lon);

        progressDialog.setMessage(getString(R.string.text_uploading));
        progressDialog.show();

        app.getRestService().getMeta(record).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Records>() {
                    @Override
                    public final void onCompleted() {
                        // do nothing
                    }

                    @Override
                    public final void onError(Throwable e) {
                        Log.e("EchoesError", e.getMessage());
                    }

                    @Override
                    public final void onNext(Records response) {
                        uploadFile(response.id);
                    }
                });
    }

    private void uploadFile(String metaID) {

        File file = new File(mediaHelper.getFileName());

        RequestBody record = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("record", file.getName(), requestFile);

        Log.e("metaid", metaID);
        Log.e("file", file.getAbsolutePath());
        Log.e("file", file.getName());

        // finally, execute the request
        app.getRestService().upload(body, metaID).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Records>() {
                    @Override
                    public final void onCompleted() {
                        // do nothing
                    }

                    @Override
                    public final void onError(Throwable e) {
                        Log.e("EchoesError", e.getMessage());
                    }

                    @Override
                    public final void onNext(Records response) {
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName("Recording");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
