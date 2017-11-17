package emcees.ch.labo_02;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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

    private static final String TAG = DifferedTransmit.class.getSimpleName();

    public static final MediaType TEXT_PLAIN = MediaType.parse("text/plain; charset=utf-8");
    public static final String SERVER_URL = "http://sym.iict.ch/rest/txt";

    // GUI elements
    private EditText requestEditText = null;
    private Button sendButton = null;
    private TextView responseEditText = null;

    // List containing our pending requests
    private List<Request> pendingRequests = null;
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
        requestEditText = (EditText) findViewById(R.id.request);
        sendButton = (Button) findViewById(R.id.send);
        responseEditText = (TextView) findViewById(R.id.response);

        responseEditText.setMovementMethod(new ScrollingMovementMethod());

        handler = new Handler();
        differedSending = () -> {
            // Schedule another event to be run in 5s
            handler.postDelayed(differedSending, 5 * 1000);

            DifferedTransmit.OkHttpPostHandler okHttpHandler = new DifferedTransmit.OkHttpPostHandler();
            for (Request request : pendingRequests) {
                new DifferedTransmit.OkHttpPostHandler().execute(request);
            }
        };

        handler.post(differedSending);

        // Then program action associated to "Ok" button
        sendButton.setOnClickListener((v) -> {

            String data = requestEditText.getText().toString();
            RequestBody body = RequestBody.create(TEXT_PLAIN, data);
            Request request = new Request.Builder()
                    .url(SERVER_URL)
                    .post(body)
                    .build();

            pendingRequests.add(request);
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

            // Need to save the request here so we can delete it if the post is successful
            Request request = (Request) objects[0];

            try {
                Response response = client.newCall(request).execute();

                // Get the body (can only be read once)and check if the response is successful (code
                // [200 ; 300[. It is done here, because we cannot access the body in the onPostExecute()
                String text = response.body().string();
                if (response.isSuccessful()) {
                    // Remove the request from the list if it was successfully sent
                    Log.d(TAG, "Request " + request.body() + " successfully sent");
                    pendingRequests.remove(request);
                    return text;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Method that is called when a result is available.
         *
         * @param response The result computed from the background's thread.
         */
        @Override
        protected void onPostExecute(String response) {
            responseEditText.setText(response);
        }
    }
}
