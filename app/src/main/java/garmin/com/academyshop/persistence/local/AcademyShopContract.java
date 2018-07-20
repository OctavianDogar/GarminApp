package garmin.com.academyshop.persistence.local;

import android.provider.BaseColumns;

/**
 * Created by Octavian on 4/24/2017.
 */

public interface AcademyShopContract {

    String DB_NAME  = "shop.db";
    String DATABASE_PATH = "/data/data/garmin.com.academyshop/databases/";
    String FIREBASE_URL = "https://academyshop-6992c.firebaseio.com/";

    interface Products extends BaseColumns{
        String TABLE_NAME = "products";
        String PRODUCT_NAME = "product_name";
        String PRODUCT_QUANTITY = "product_quantity";
    }

}
