package garmin.com.academyshop.multithreading;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.List;

import garmin.com.academyshop.R;
import garmin.com.academyshop.StorageUtils;
import garmin.com.academyshop.model.Product;
import garmin.com.academyshop.persistence.local.AcademyShopDBManager;

/**
 * Created by Octavian on 4/27/2017.
 */

public class SaveProductService extends IntentService {
    public static final String KEY_EXPORT_OPTION = "export_option";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SaveProductService() {
        super("SaveProductsService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String exportOptionValue = intent.getStringExtra(KEY_EXPORT_OPTION);

        List<Product> items = AcademyShopDBManager.getInstance(this).getProducts(true);

            if (getString(R.string.export_value_external_storage).equals(exportOptionValue)) {
                StorageUtils.saveProductsListToExternalStorage(items, Environment.getExternalStorageDirectory(), this);
            } else {
                // External Storage app private directory is selected, no storage permissions required
                String directory = null;
                if (getString(R.string.export_value_external_storage_private_documents).equals(exportOptionValue)) {
                    // Set Documents directory when selected
                    directory = Environment.DIRECTORY_DOCUMENTS;
                }

                // To all existing external storage paths save the product list
                File[] externalStorageDirs = getExternalFilesDirs(directory);
                for (int i = 0; i < externalStorageDirs.length; i++) {
                    if (externalStorageDirs[i] != null) {
                        StorageUtils.saveProductsListToExternalStorage(items, externalStorageDirs[i], this);
                    }
                }
            }


    }
}
