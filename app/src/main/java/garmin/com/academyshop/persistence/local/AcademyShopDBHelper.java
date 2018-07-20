package garmin.com.academyshop.persistence.local;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Octavian on 4/24/2017.
 */

public class AcademyShopDBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION  = 1;

    private static final String SQL_CREATE_TABLE = "create table "+
            AcademyShopContract.Products.TABLE_NAME +
            "(" + AcademyShopContract.Products._ID + " integer primary key, "+
            AcademyShopContract.Products.PRODUCT_NAME + " text not null, "+
            AcademyShopContract.Products.PRODUCT_QUANTITY+" integer default 0)";

    public AcademyShopDBHelper(Context context){
        super(context, AcademyShopContract.DB_NAME,null,DB_VERSION);
    }


    public AcademyShopDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public AcademyShopDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
