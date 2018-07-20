package garmin.com.academyshop;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import garmin.com.academyshop.fragments.ProdQuantityFragment;
import garmin.com.academyshop.fragments.dummy.DummyContent;
import garmin.com.academyshop.model.Product;
import garmin.com.academyshop.multithreading.SaveProductService;
import garmin.com.academyshop.networking.NetworkUtils;
import garmin.com.academyshop.networking.ProductFetchTask;
import garmin.com.academyshop.persistence.local.AcademyShopDBManager;
import garmin.com.academyshop.persistence.remote.OverviewPresenter;
import garmin.com.academyshop.persistence.remote.RetrofitBridge;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ProductAdapterListener,
        LoaderManager.LoaderCallbacks<List<Product>>,
        ProdQuantityFragment.OnListFragmentInteractionListener,
        RetrofitBridge{

    private static final int WRITE_STORAGE_PERMISSION_REQUEST = 0;
    private static final int PRODUCTS_LIST_ID = 1;

    private InternetBroadcastReceiver mInternetBroadcastReceiver = new InternetBroadcastReceiver();
    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;

    private ProductFetchTask mProductsTask;

    private AcademyShopDBManager dbManager;
    private final boolean ORDER_BY = true;

    private List<Product> items;
    private boolean mConnected = false;

    private OverviewPresenter presenter;

    public static final int RECEIVED_PRODUCT_NAME_OK=1;

    ItemTouchHelper.SimpleCallback mSimpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Product targetProduct = items.get(viewHolder.getAdapterPosition());

            new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.myDialog))
                    .setTitle("Delete product")
                    .setMessage("Are you sure you want to remove "+targetProduct.getMProductName()+" " +
                            "form store?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int deleteId = targetProduct.getMId();
                            presenter.deleteProduct(deleteId);

                            mAdapter.getItems().remove(viewHolder.getAdapterPosition());
                            mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();



        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        items = new ArrayList<>();

        presenter = new OverviewPresenter(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton addFab = (FloatingActionButton)findViewById(R.id.addFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addProduct = new Intent(v.getContext(),AddProductActivity.class);
                startActivityForResult(addProduct,RECEIVED_PRODUCT_NAME_OK);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        dbManager = AcademyShopDBManager.getInstance(this);
        dbManager.clearDatabase();

        mAdapter = new ProductAdapter(items, this);
        mRecyclerView.setAdapter(mAdapter);

//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSimpleItemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(mRecyclerView);

//        getIncomingProducts().subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Product>() {
//                    @Override
//                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@io.reactivex.annotations.NonNull Product product) {
//                        mAdapter.addItem(product);
//                    }
//
//                    @Override
//                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
//                        Toast.makeText(getApplicationContext(),"Something went wrong while loading",Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Toast.makeText(getApplicationContext(),"Done loading",Toast.LENGTH_SHORT).show();
//                    }
//                });
//        getSupportLoaderManager().initLoader(PRODUCTS_LIST_ID, null,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RECEIVED_PRODUCT_NAME_OK){
            String addProdName = data.getStringExtra(AddProductActivity.PROD_NAME_EXTRA);
            Product product = new Product(addProdName);
            setReceivedProduct(product);
        }
    }

    private List<Product> getProducts() {
        return items;
    }

    @Override
    protected void onDestroy(){
        if(isFinishing())
            /*
            fur flipping loading stack
             */
            dbManager.closeDatabase();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Start settings activity when menu item is selected
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);

            return true;
        } else if (id == R.id.action_export_products) {
            exportProductsList();
        } else if( id == R.id.action_shopping_cart){
//            Fragment fr = new ProdQuantityFragment();
//            FragmentManager fm = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fm.beginTransaction();
//            fragmentTransaction.replace(R.id.prod_quant_fragment,fr);
//            fragmentTransaction.commit();
            Intent shoppingCart = new Intent(this,ShoppingCart.class);
            startActivity(shoppingCart);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            AcademyShopDBManager.getInstance(this).clearDatabase();


        } else if (id == R.id.nav_logout) {
            // Clear the preferences (deletes all data from the SharedPreferences object it is called on)
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.clear();
            editor.apply();

            // Finish this activity to return to login screen
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mConnected = checkConnectivityState(this);
        if (mInternetBroadcastReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
            registerReceiver(mInternetBroadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mInternetBroadcastReceiver != null) unregisterReceiver(mInternetBroadcastReceiver);
    }

    @Override
    public void onProductSelected(int position) {
        Toast.makeText(this, "Product selected " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<List<Product>> onCreateLoader(int id, Bundle args) {
        return new ProductsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Product>> loader, List<Product> data) {
        mAdapter.setmItems(data);
    }


    @Override
    public void onLoaderReset(Loader<List<Product>> loader) {
        mAdapter.setmItems(new ArrayList<>());
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    /*************************
    BRIDGE IMPLEMENTED METHODS
     */

    @Override
    public void setReceivedProducts(List<Product> products) {
        items = products;
        mAdapter.setmItems(items);
    }

    @Override
    public void addProduct(Product product) {
        items.add(product);
        mAdapter.setmItems(items);
    }

    public void setReceivedProduct(Product product) {
        presenter.createProduct(product);
    }

    @Override
    public void sendMessage(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT);
    }


    /*
    *************************
     */

    public class InternetBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            mConnected  =checkConnectivityState(context);
            if(mConnected){
                Snackbar.make(findViewById(R.id.content_view), "Internet connection available", Snackbar.LENGTH_LONG).setAction("Action",
                        null).show();
            }else{
                Snackbar.make(findViewById(R.id.content_view), "No internet", Snackbar.LENGTH_LONG).setAction("Action",
                        null).show();
            }


            if(NetworkUtils.isConnected(context)){
                mConnected = true;
            }else {
                mConnected = false;
            }
        }
    }
    private boolean checkConnectivityState(Context context){
        if(NetworkUtils.isConnected(context)){
            if(NetworkUtils.isConnectedToWifiNetwork(context)){
                return true;
            }else{
                if( NetworkUtils.idWifiOnlyNetworkPreferenceSet(context)){
                    return false;
                }else{
                    return true;
                }
            }
        }else{
            return false;
        }
    }


    private void exportProductsList() {
        // Get shared preferences used in settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get export option selected in settings
        String exportOption = sharedPreferences.getString(getString(R.string.key_storage_export), getString(R.string.export_value_external_storage));

        // If External Storage root directory is selected, so WRITE_EXTERNAL_STORAGE permission is required to succeed
        if (getString(R.string.export_value_external_storage).equals(exportOption) &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted it should be requested
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, WRITE_STORAGE_PERMISSION_REQUEST);
        } else {
            saveProductsToExternalStorage(exportOption);
        }
    }

    private void saveProductsToExternalStorage(String exportOptionValue) {
        Intent startIntent = new Intent(this, SaveProductService.class);
        startIntent.putExtra(SaveProductService.KEY_EXPORT_OPTION,exportOptionValue);
        startIntent.putParcelableArrayListExtra("aaa", (ArrayList<? extends Parcelable>) getProducts());
        startService(startIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_STORAGE_PERMISSION_REQUEST:
                // Try to save the product. If permission was granted will succeed, else error toast will be shown
                saveProductsToExternalStorage(getString(R.string.export_value_external_storage));
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    /*
    todo: PROVIDER TO ANOTHER
     */

    @Override
    protected void onStart() {
        super.onStart();
//        mProductsTask = new ProductFetchTask(this);
//        mProductsTask.execute();
        presenter.getAllProducts();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(mProductsTask!=null)
//           mProductsTask.cancel(true);
    }
}
