package garmin.com.academyshop.persistence.remote;

import java.util.Map;

import garmin.com.academyshop.model.Product;
import garmin.com.academyshop.persistence.local.AcademyShopContract;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Octavian on 5/15/2017.
 */

public class RemoteServiceImpl {

    private static RemoteService service = null;

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(AcademyShopContract.FIREBASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    /*
    Interface for managing REST calls
     */
    public interface RemoteService{

        @PUT("/products/{mId}.json")
        Call<Product> createProduct(
                @Path("mId") int mId,
                @Body Product product);

        @GET("/products/{mId}.json")
        Call<Product> getProduct(
                @Path("mId") int mId);

        @GET("/products/.json")
        Call<Map<String, Product>> getAllProducts();

        @DELETE("/products/{mId}.json")
        Call<Product> deleteProduct(@Path("mId") int mId);
    }

    /*
    Singleton getter
     */
    public static RemoteService getInstance(){
        if(service == null)
            service = retrofit.create(RemoteService.class);
        return service;
    }

}
