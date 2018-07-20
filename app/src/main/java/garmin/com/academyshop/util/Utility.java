package garmin.com.academyshop.util;

import java.util.ArrayList;
import java.util.List;

import garmin.com.academyshop.model.Product;

/**
 * Created by Octavian on 5/16/2017.
 */

public class Utility {


    public static List<Product> removeNulls(List<Product>products){
        List<Product> someQuantity = new ArrayList<>();
        for(Product p:products){
            if(p.getQuantity()>0)
                someQuantity.add(p);
        }
        return someQuantity;
    }
}
