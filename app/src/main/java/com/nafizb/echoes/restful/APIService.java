package com.nafizb.echoes.restful;

import com.nafizb.echoes.BuildConfig;
import com.nafizb.echoes.models.Records;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Nafiz on 3.12.2016.
 */

public interface APIService {
    @GET("records")
    Observable<List<Records>> getRecords();

    @GET("records/{id}")
    Observable<ResponseBody> downloadRecord(@Path("id") String id);

    @POST("records/meta")
    Observable<Records> getMeta(@Body Records record);

    @Multipart
    @POST("records/upload/{id}")
    Observable<Records> upload(@Part MultipartBody.Part file, @Path("id") String id);

}
