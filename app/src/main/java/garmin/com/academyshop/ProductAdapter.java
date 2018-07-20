package garmin.com.academyshop;

import java.util.List;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import garmin.com.academyshop.model.Product;

/**
 * Created by protiuc on 4/11/17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> mItems;
    private ProductAdapterListener mListener;


    public ProductAdapter(@NonNull List<Product> products, @Nullable ProductAdapterListener listener) {
        this.mItems = products;
        this.mListener = listener;
    }

    @NonNull
    public List<Product> getmItems() {
        return mItems;
    }

    public void addItem(Product product){
        this.mItems.add(product);
        notifyDataSetChanged();
    }

    public void setmItems(List<Product> mItems) {
        this.mItems = mItems;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product currentProduct = mItems.get(position);
        holder.mProductName.setText(currentProduct.getMProductName());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @NonNull
    public List<Product> getItems() {
        return mItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mProductName;

        public void showPopup(View v) {
            PopupMenu popup = new PopupMenu(itemView.getContext(), v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.main_item_menu, popup.getMenu());
            popup.show();

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int a=0;
                    return true;
                }
            });
        }



        public ViewHolder(View itemView) {
            super(itemView);
            mProductName = (TextView) itemView.findViewById(R.id.product_name);
            mProductName.setOnClickListener(v -> showPopup(v));

            AssetManager am = itemView.getContext().getAssets();
            Typeface custom_font = Typeface.createFromAsset(am,"fonts/Damion-Regular.ttf");

            mProductName.setTypeface(custom_font);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onProductSelected(getAdapterPosition());
                    }
                }
            });
        }
    }
}
