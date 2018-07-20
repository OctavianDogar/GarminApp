package garmin.com.academyshop;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import garmin.com.academyshop.model.Product;
import garmin.com.academyshop.persistence.remote.RetrofitBridge;
import lombok.Getter;

@Getter
public class AddProductActivity extends AppCompatActivity {

    private EditText addProdName;
    public static String PROD_NAME_EXTRA ="prodName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextView prodNameLabel = (TextView) findViewById(R.id.productNameTextView);
        AssetManager am = getApplicationContext().getAssets();
        Typeface custom_font = Typeface.createFromAsset(am, "fonts/Damion-Regular.ttf");
        prodNameLabel.setTypeface(custom_font);



        addProdName = (EditText) findViewById(R.id.addProductNameTextField);
        addProdName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {

                    String prodName = addProdName.getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra(PROD_NAME_EXTRA,prodName);
                    setResult(MainActivity.RECEIVED_PRODUCT_NAME_OK,intent);
                    finish();
                }
                return false;
            }
        });

        Button doneProdNameBtn = (Button)findViewById(R.id.doneProductNameBtn);
        doneProdNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prodName = addProdName.getText().toString();
                Intent intent = new Intent();
                intent.putExtra(PROD_NAME_EXTRA,prodName);
                setResult(MainActivity.RECEIVED_PRODUCT_NAME_OK,intent);
                finish();
            }
        });
    }
}