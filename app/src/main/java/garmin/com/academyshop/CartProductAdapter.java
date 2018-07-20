package garmin.com.academyshop;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import garmin.com.academyshop.model.Product;

/**
 * Created by Octavian on 5/16/2017.
 */

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.ViewHolder> {

    private List<Product> mItems;
    private ProductAdapterListener mListener;


    public CartProductAdapter(@NonNull List<Product> products, @Nullable ProductAdapterListener listener) {
        this.mItems = products;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_product_row, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product currentProduct = mItems.get(position);
        holder.mProductName.setText(currentProduct.getMProductName()+" x"+currentProduct.getQuantity());
    }

    public void setmItems(List<Product> mItems) {
        this.mItems = mItems;
        notifyDataSetChanged();
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

        public ViewHolder(View itemView) {
            super(itemView);
            mProductName = (TextView) itemView.findViewById(R.id.cart_product_name);

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
