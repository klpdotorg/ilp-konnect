package in.org.klp.ilpkonnect.Retro;

import java.util.concurrent.TimeUnit;

import in.org.klp.ilpkonnect.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    //public static final String BASE_URL = "http://cricscore-api.appspot.com/";
    public static final String BASE_URL = BuildConfig.HOST;
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit == null) {

          /*  HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor).build();*/

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS).build();

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