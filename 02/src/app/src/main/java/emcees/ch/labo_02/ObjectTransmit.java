package emcees.ch.labo_02;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ObjectTransmit extends AppCompatActivity {

    // Widgets.
    private RadioGroup radioTypeGroup;
    private Button sendButton;
    private TextView responseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_object_transmit);

        // Add button click listener.
        _addButtonListener();
    }

    private void _addButtonListener() {
        // Retrieve widgets.
        radioTypeGroup = (RadioGroup) findViewById(R.id.radioType);
        sendButton = (Button) findViewById(R.id.sendButton);
        responseTextView = (TextView) findViewById(R.id.responseTextView);

        // Setup button listener.
        sendButton.setOnClickListener(view -> {
            // Retrieve selected type.
            int selectedId = radioTypeGroup.getCheckedRadioButtonId();
            String type = ((RadioButton) findViewById(selectedId)).getText().toString();

            String response = "Response";

            if (type.equals("JSON")) {
                _sendJSONRequest();
            } else if (type.equals("XML")) {
                _sendXMLRequest();
            }

            // Display response with text view.
            responseTextView.setText(response);
        });
    }

    private void _sendJSONRequest() {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        InputStream is = null;
        try {
            is = getAssets().open("lorem.json");

            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            String content = new String(data);
            String url = "http://sym.iict.ch/rest/json";

            OkHttpPostHandler okHttpHandler = new OkHttpPostHandler();
            okHttpHandler.execute(url, JSON, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void _sendXMLRequest() {
        MediaType XML = MediaType.parse("application/xml; charset=utf-8");

        try {
            InputStream is = getAssets().open("annuaire.xml");
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            String content = new String(data);
            String url = "http://sym.iict.ch/rest/xml";

            OkHttpPostHandler okHttpHandler = new OkHttpPostHandler();
            okHttpHandler.execute(url, XML, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class OkHttpPostHandler extends AsyncTask<Object, Void, String> {

        // HTTP Client
        // We set the timeout to 30 seconds because the server takes time to send a response for XML
        // content.
        OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

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
            responseTextView.setText(result);
        }
    }

}
