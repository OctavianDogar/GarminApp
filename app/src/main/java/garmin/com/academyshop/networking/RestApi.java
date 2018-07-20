package garmin.com.academyshop.networking;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import garmin.com.academyshop.model.Product;

/**
 * Created by Octavian on 5/8/2017.
 */

public class RestApi {

    public static final String GET_DATA = "https://academy-90835.firebaseio.com/.json";
    private static final int MAX_STRING_LENGTH = 500;
    private static final String TAG = RestApi.class.getSimpleName();

    public void addProduct(String json) throws IOException {
        HttpsURLConnection connection = null;
        URL url = new URL(GET_DATA);
        connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(json.getBytes("UTF-8"));
        outputStream.close();

        connection.connect();

        int responseCode = connection.getResponseCode();

//        Log.d(TAG,"addProductResponse")
    }

    public List<Product> getData() throws IOException {
        List<Product> result = new ArrayList<>();

        InputStream inputStream = null;
        HttpsURLConnection connection = null;
        int responseCode;
        URL url = new URL(GET_DATA);

        connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(3000);
        connection.setConnectTimeout(3000);
        connection.setDoInput(true);

        connection.connect();

        responseCode = connection.getResponseCode();

        if(responseCode!= HttpsURLConnection.HTTP_OK){
            throw new IOException("Http error code: "+responseCode);
        }
        inputStream = connection.getInputStream();
        if(inputStream!=null){
            result = readStream(inputStream);
        }

        return result;
    }

    private List<Product> readStream(InputStream inputStream) throws IOException {


        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream,"UTF-8"));
        try{
            return readProductsArray(jsonReader);
        }finally {
            jsonReader.close();
        }
    }

    private List<Product> readProductsArray(JsonReader jsonReader) throws IOException {
        List<Product> products = new ArrayList<>();
        jsonReader.beginObject();
        while(jsonReader.hasNext()){
            products.add(readProduct(jsonReader));
        }
        jsonReader.endObject();
        return products;
    }

    private Product readProduct(JsonReader jsonReader) throws IOException {
        int id = 0;
        String prodName = "";
        jsonReader.beginObject();
        while(jsonReader.hasNext()){
            String name = jsonReader.nextName();
            if(name.equals("id")){
                id = jsonReader.nextInt();
            }else if(name.equals("productName")){
                prodName = jsonReader.nextString();
            }else{
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return new Product(id,prodName);
    }


    private String readStream(InputStream inputStream, int maxStringLength) throws IOException {

        String result = "";

        InputStreamReader streamReader = new InputStreamReader(inputStream,"UTF-8");
        char[]buffer = new char[maxStringLength];
        int numCHars = 0;
        int readSize = 0;

        while(numCHars <maxStringLength && readSize!=-1){
            numCHars+=readSize;
            readSize = streamReader.read(buffer,numCHars,buffer.length -numCHars);
        }

        if(numCHars!=-1){
            numCHars = Math.min(numCHars,maxStringLength);
            result = new String(buffer,0,numCHars);
        }

        return result;
    }

}
