package garmin.com.academyshop;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

import garmin.com.academyshop.model.Product;
import garmin.com.academyshop.persistence.remote.OverviewPresenter;
import garmin.com.academyshop.persistence.remote.RetrofitBridge;
import garmin.com.academyshop.util.Utility;

public class ShoppingCart extends AppCompatActivity implements
        ProductAdapterListener,
        RetrofitBridge{

    private RecyclerView mRecyclerView;
    private CartProductAdapter mAdapter;
    private List<Product> items;

    private OverviewPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter = new OverviewPresenter(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.cart_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        items = new ArrayList<>();
        mAdapter = new CartProductAdapter(items, this);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback mSimpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Product targetProduct = items.get(viewHolder.getAdapterPosition());

                new AlertDialog.Builder(new ContextThemeWrapper(ShoppingCart.this, R.style.myDialog))
                        .setTitle("Delete product")
                        .setMessage("Are you sure you want to remove one "+targetProduct.getMProductName()+"?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int deleteId = targetProduct.getMId();
                                targetProduct.setQuantity(targetProduct.getQuantity()-1);
                                presenter.createProduct(targetProduct,deleteId);
                                flushScreen(viewHolder);
                                if(targetProduct.getQuantity()==0){
                                    presenter.getAllProducts();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();



                //TODO add real remove from retrofit
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSimpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        
    }

    private void flushScreen(RecyclerView.ViewHolder viewHolder) {
        mAdapter.getItems().remove(viewHolder.getAdapterPosition());
        mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
    }

    @Override
    public void onProductSelected(int position) {
    }


    @Override
    protected void onStart() {
        super.onStart();
        presenter.getAllProducts();
    }

    @Override
    public void setReceivedProducts(List<Product> products) {
        items = Utility.removeNulls(products);
        mAdapter.setmItems(items);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addProduct(Product product) {
        items.add(product);
        mAdapter.notifyDataSetChanged();
//        mAdapter.setmItems(items);
    }

    @Override
    public void sendMessage(String msg) {

    }
}
