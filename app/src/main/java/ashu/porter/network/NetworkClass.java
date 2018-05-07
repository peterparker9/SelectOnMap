package ashu.porter.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ashu.porter.utils.Constants.BASE_URL;

/**
 * Created by apple on 07/05/18.
 */

public class NetworkClass {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
