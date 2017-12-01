package emcees.ch.labo_03;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class Barcode extends AppCompatActivity {

    private static final String TAG = Barcode.class.getSimpleName();

    // Tag used to save the result text
    private static final String RESULT_STATE_TAG = "result";

    // UI components
    private Button scanCodeButton = null;
    private TextView scanResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        scanCodeButton = (Button) findViewById(R.id.buttonScan);
        scanResult = (TextView) findViewById(R.id.resultQRCode);

        // Used to keep the result text when an orientation change occur
        if (savedInstanceState != null) {
            scanResult.setText(savedInstanceState.getCharSequence(RESULT_STATE_TAG));
        }

        // Start the scan when the button is pressed
        scanCodeButton.setOnClickListener(view -> {
            new IntentIntegrator(this).initiateScan();
        });

    }

    // Used to get the result of the scan
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Clear the result text field and notify the cancellation with a Toast
                scanResult.setText("");
                Toast.makeText(this, getResources().getText(R.string.scan_canceled), Toast.LENGTH_LONG).show();
            } else {
                // Display the result text
                scanResult.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the result text
        outState.putCharSequence(RESULT_STATE_TAG, scanResult.getText());
    }
}
