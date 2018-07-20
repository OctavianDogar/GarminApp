package garmin.com.academyshop.persistence.remote;

import java.util.List;

import garmin.com.academyshop.model.Product;

/**
 * Created by Octavian on 5/15/2017.
 */

public interface RetrofitBridge {

    void setReceivedProducts(List<Product>products);

    void addProduct(Product product);

    void sendMessage(String msg);



}
