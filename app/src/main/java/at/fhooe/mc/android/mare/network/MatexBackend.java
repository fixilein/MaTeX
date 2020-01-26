package at.fhooe.mc.android.mare.network;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MatexBackend {

    @GET("upload/new")
    Call<String> getId();


    @Multipart
    @POST("upload/{id}/md")
    Call<ResponseBody> uploadMdFile(
            @Path("id") String id,
            @Part MultipartBody.Part file);


    @Multipart
    @POST("upload/{id}/img")
    Call<String> uploadImage(
            @Path("id") String id,
            @Part MultipartBody.Part file);


    @GET("pdf/{id}")
    Call<ResponseBody> downloadPdf(
            @Path("id") String id);
}
