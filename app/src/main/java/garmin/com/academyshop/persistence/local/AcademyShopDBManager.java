package garmin.com.academyshop.persistence.local;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import garmin.com.academyshop.ProductsLoader;
import garmin.com.academyshop.model.Product;

/**
 * Created by Octavian on 4/24/2017.
 */

public class AcademyShopDBManager {

    private static AcademyShopDBManager instance;
    private AcademyShopDBHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;

    private AcademyShopDBManager(Context context){
        this.context = context.getApplicationContext();
        dbHelper = new AcademyShopDBHelper(context);
    }

    public static AcademyShopDBManager getInstance(Context context){
        if(instance!=null){
            return instance;
        }else{
            return new AcademyShopDBManager(context);
        }
    }

    public void openDatabase(){
        if(database == null || !database.isOpen())
            database = dbHelper.getWritableDatabase();
        checkDataBase();
    }

    public void closeDatabase(){
        if (database!=null)
            database.close();
    }

    public void insertProducts(List<Product> list){
        openDatabase();

        database.beginTransaction();
        try{
            for (Product p : list) {
                ContentValues values = new ContentValues();
                values.put(AcademyShopContract.Products.PRODUCT_NAME, p.getMProductName());
                database.insert(AcademyShopContract.Products.TABLE_NAME,null,values);
            }
            database.setTransactionSuccessful();
        }finally {
            database.endTransaction();
        }
    }



    public List<Product> getProducts(boolean ascending){
        openDatabase();
        List<Product> products = new ArrayList<>();
        String[]columns = {AcademyShopContract.Products._ID,AcademyShopContract.Products.PRODUCT_NAME};
        String selection = null;
        String[]selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = ascending? "ASC" : "DESC";
        orderBy = AcademyShopContract.Products.PRODUCT_NAME;
        int indexId=-1;
        int indexName = -1;
        Cursor cursor = database.query(AcademyShopContract.Products.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy
                );
        if(cursor!=null){
            indexId = cursor.getColumnIndex(AcademyShopContract.Products._ID);
            indexName = cursor.getColumnIndex(AcademyShopContract.Products.PRODUCT_NAME);
        }
        while(cursor.moveToNext()){
            int id = cursor.getInt(indexId);
            String name = cursor.getString(indexName);
            Product product = new Product(id,name);
            products.add(product);
        }
        cursor.close();

        return products;
    }

    public void copyDataBase() throws IOException{
        InputStream myInput = this.context.getAssets().open(AcademyShopContract.DB_NAME);
        String outFileName = AcademyShopContract.DATABASE_PATH + AcademyShopContract.DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        boolean exist = false;
        try {
            String dbPath = AcademyShopContract.DATABASE_PATH + AcademyShopContract.DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(dbPath, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.v("db log", "database does't exist");
            try {
                copyDataBase();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        if (checkDB != null) {
            exist = true;
            checkDB.close();
        }
        return exist;
    }

    public void clearDatabase(){
        openDatabase();
        database.delete(AcademyShopContract.Products.TABLE_NAME, null,null);
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(ProductsLoader.PRODUCTS_CHANGE));
    }
}
