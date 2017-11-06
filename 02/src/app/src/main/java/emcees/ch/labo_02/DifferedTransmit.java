package emcees.ch.labo_02;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DifferedTransmit extends AppCompatActivity {


    public static final MediaType TEXT_PLAIN = MediaType.parse("text/plain; charset=utf-8");
    public static final String SERVER_URL = "http://sym.iict.ch/rest/txt";

    // GUI elements
    private EditText request = null;
    private Button send = null;
    private TextView response = null;

    // List containing our pending requests
    private List<String> pendingRequests = null;
    // Handler used to do the background task periodically
    private Handler handler;
    // Runnable used by our handler
    private Runnable differedSending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_transmit);

        pendingRequests = new ArrayList<>();

        // Link to GUI elements
        request = (EditText) findViewById(R.id.request);
        send = (Button) findViewById(R.id.send);
        response = (TextView) findViewById(R.id.response);

        handler = new Handler();
        differedSending = () -> {
            // Schedule another event to be run in 5s
            handler.postDelayed(differedSending, 5 * 1000);

            for (String data : pendingRequests) {
                DifferedTransmit.OkHttpPostHandler okHttpHandler = new DifferedTransmit.OkHttpPostHandler();
                okHttpHandler.execute(SERVER_URL, TEXT_PLAIN, data);
            }
        };

        handler.post(differedSending);

        // Then program action associated to "Ok" button
        send.setOnClickListener((v) -> {

            String data = request.getText().toString();

            pendingRequests.add(data);
        });
    }

    private class OkHttpPostHandler extends AsyncTask<Object, Void, String> {

        // HTTP Client
        OkHttpClient client = new OkHttpClient();

        /**
         * Method executed in background until everything is done.
         *
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
                if (pendingRequests.contains(data)) {
                    pendingRequests.remove(data);
                }

                return returnData.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Method that is called when a result is available.
         *
         * @param result The result computed from the background's thread.
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                response.setText(result);
            }
        }
    }
}
