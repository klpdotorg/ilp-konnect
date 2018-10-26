package in.org.klp.ilpkonnect.Retro;

import android.util.Log;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import in.org.klp.ilpkonnect.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    //public static final String BASE_URL = "http://cricscore-api.appspot.com/";
    public static final String BASE_URL = BuildConfig.HOST;
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit == null) {


            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)

                    .build();




            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)

                .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                        ;


        }
        return retrofit;
    }
}