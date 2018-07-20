package garmin.com.academyshop.persistence.remote;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import garmin.com.academyshop.model.Product;
import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Octavian on 5/15/2017.
 */
@Getter
public class OverviewPresenter {

    private final static String TAG = "OverviewPresenter";

    private RemoteServiceImpl.RemoteService service;
    private RetrofitBridge bridge;

    public OverviewPresenter(RetrofitBridge bridge){
        service = RemoteServiceImpl.getInstance();
        this.bridge  = bridge;
    }

    public void createProduct(Product product){
        int mId = product.hashCode();
        product.setMId(mId);
        Call<Product> call = service.createProduct(mId,product);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                String msg = "Success: Product "+product.getMProductName()+ " was added.";
                Log.d(TAG,msg);
                getBridge().sendMessage(msg);
                getBridge().addProduct(response.body());
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                String msg = "Error: Product "+product.getMProductName()+ " was not added.";
                Log.d(TAG,msg);
                getBridge().sendMessage(msg);
            }
        });
    }
    public void createProduct(Product product, int mId){
        Call<Product> call = service.createProduct(mId,product);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                String msg = "Success: Product "+product.getMProductName()+ " was added.";
                Log.d(TAG,msg);
                getBridge().sendMessage(msg);
                getBridge().addProduct(response.body());
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                String msg = "Error: Product "+product.getMProductName()+ " was not added.";
                Log.d(TAG,msg);
                getBridge().sendMessage(msg);
            }
        });
    }

    public void getAllProducts(){
        Call<Map<String,Product>> call = service.getAllProducts();
        call.enqueue(new Callback<Map<String, Product>>() {
            @Override
            public void onResponse(Call<Map<String, Product>> call, Response<Map<String, Product>> response) {
                Map<String,Product> products = response.body();
                List<Product> productList = new ArrayList<>();

                String msg;
                if(products!=null && !products.isEmpty()){
                    for(String key:products.keySet()){
                        productList.add(products.get(key));
                    }
                    msg = "Received a product map with size: "+products.size();
                    Log.d(TAG,msg);
                    getBridge().sendMessage(msg);

                    getBridge().setReceivedProducts(productList);
                }else{
                    msg = "Received an empty products map";
                    Log.d(TAG,msg);
                    getBridge().sendMessage(msg);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Product>> call, Throwable t) {
                String msg = "Error in receiving products.";
                Log.d(TAG,msg);
                getBridge().sendMessage(msg);


            }
        });
    }

    public int deleteProduct(int mId){
        Call<Product> call = service.deleteProduct(mId);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                String msg;
                msg = "Removed one successfully.";
                Log.d(TAG,msg);
                getBridge().sendMessage(msg);
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                String msg;
                msg = "Error in deleting product.";
                Log.d(TAG,msg);
                getBridge().sendMessage(msg);
            }
        });
        return mId;

    }

}
