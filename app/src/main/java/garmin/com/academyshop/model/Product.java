package garmin.com.academyshop.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by protiuc on 4/11/17.
 */
@Getter
@Setter
@AllArgsConstructor
public class Product implements Parcelable {
    private int mId;
    private String mProductName;
    private int quantity;

    public Product(String productName){
        mProductName = productName;
    }

    public Product(String mProductName, int quantity){
        this.mProductName = mProductName;
        this.quantity = quantity;
    }

    public Product(int mId, String mProductName) {
        this.mId = mId;
        this.mProductName = mProductName;
    }

    protected Product(Parcel in) {
        mId = in.readInt();
        mProductName = in.readString();
        quantity = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mProductName);
        dest.writeInt(quantity);
    }
}
