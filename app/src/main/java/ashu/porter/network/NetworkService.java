package ashu.porter.network;

import ashu.porter.model.Cost;
import ashu.porter.model.Serviceable;
import ashu.porter.model.Time;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by apple on 07/05/18.
 */

public interface NetworkService {

    @GET("users/serviceability")
    Observable<Serviceable> getServiceability();

    @GET("vehicles/cost")
    Observable<Cost> getCost(@Query("lat") double lat, @Query("lng") double lng);

    @GET("/vehicles/eta")
    Observable<Time> getETA(@Query("lat") double lat, @Query("lng") double lng);


}
