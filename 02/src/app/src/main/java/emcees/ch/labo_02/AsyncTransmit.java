package emcees.ch.labo_02;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsyncTransmit extends AppCompatActivity {

    public static final MediaType TEXT_PLAIN = MediaType.parse("text/plain; charset=utf-8");
    public static final String SERVER_URL = "http://sym.iict.ch/rest/txt";

    // GUI elements
    private EditText request = null;
    private Button send = null;
    private TextView response = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_transmit);

        // Link to GUI elements
        request = (EditText) findViewById(R.id.request);
        send = (Button) findViewById(R.id.send);
        response = (TextView) findViewById(R.id.response);

        response.setMovementMethod(new ScrollingMovementMethod());

        // Then program action associated to "Ok" button
        send.setOnClickListener((v) -> {

            String data = request.getText().toString();

            OkHttpPostHandler okHttpHandler= new OkHttpPostHandler();
            okHttpHandler.execute(SERVER_URL, TEXT_PLAIN, data);
        });
    }

    private class OkHttpPostHandler extends AsyncTask<Object, Void, String> {

        // HTTP Client
        OkHttpClient client = new OkHttpClient();

        /**
         * Method executed in background until everything is done.
         * @param objects An array of objects to be used in the method.
         * @return The result of the computation.
         */
        @Override
        protected String doInBackground(Object[] objects) {

            String url = (String) objects[0];
            MediaType dataType = (MediaType) objects[1];
            String data = (String) objects[2];

            RequestBody body = RequestBody.create(dataType, data);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try {
                Response returnData = client.newCall(request).execute();
                return returnData.body().string();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Method that is called when a result is available.
         * @param result The result computed from the background's thread.
         */
        @Override
        protected void onPostExecute(String result) {
            response.setText(result);
        }
    }
}
