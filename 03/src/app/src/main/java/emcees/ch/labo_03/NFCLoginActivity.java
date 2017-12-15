package emcees.ch.labo_03;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class NFCLoginActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    public static final String MIME_TEXT_PLAIN = "text/plain";

    private NfcAdapter mNfcAdapter = null;

    private boolean NFChasBeenChecked = false;

    private String password = "kek";

    private String NFCtagPassword = "test";

    // UI components
    private EditText passwordField = null;
    private CheckBox pswdPlusNfcCheckbox = null;
    private Button loginButton = null;
    private TextView nfcChecked = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_login);

        passwordField = (EditText) findViewById(R.id.password);
        pswdPlusNfcCheckbox = (CheckBox) findViewById(R.id.checkboxloginNFC);
        loginButton = (Button) findViewById(R.id.login);
        nfcChecked = (TextView) findViewById(R.id.nfc_checked);


        nfcChecked.setText(getResources().getString(R.string.nfc__not_OK));
        nfcChecked.setTextColor(getResources().getColor(R.color.nfcNotOK));

        loginButton.setOnClickListener(v -> tryToLogin());

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is disable.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }


    void tryToLogin() {

        boolean pswdIsCorrect = password.equals(passwordField.getText().toString());
        if (pswdPlusNfcCheckbox.isChecked()) {
            if (NFChasBeenChecked && pswdIsCorrect) {
                login();
            }
        } else {
            if (NFChasBeenChecked || pswdIsCorrect) {
                login();
            }
        }
    }

    void login() {
        passwordField.setText("");
        NFChasBeenChecked = false;
        nfcChecked.setTextColor(getResources().getColor(R.color.nfcNotOK));
        nfcChecked.setText(getResources().getString(R.string.nfc__not_OK));
        startActivity(new Intent(this, NFCActivity.class));
    }


    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopForegroundDispatch();
    }


    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        String actionType = intent.getAction();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(actionType)) {
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        }
    }

    // Copy paste from the lab instruction
    private void setupForegroundDispatch() {
        if (mNfcAdapter == null) return;
        final Intent intent = new Intent(this.getApplicationContext(), this.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};
        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e(TAG, "MalformedMimeTypeException", e);
        }
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techList);
    }

    // called in onPause()
    private void stopForegroundDispatch() {
        if (mNfcAdapter != null) mNfcAdapter.disableForegroundDispatch(this);
    }

    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (result.equals(NFCtagPassword)) {
                    NFChasBeenChecked = true;
                    nfcChecked.setText(getResources().getString(R.string.nfc_OK));
                    nfcChecked.setTextColor(getResources().getColor(R.color.nfcOK));
                    tryToLogin();
                } else {
                    NFChasBeenChecked = false;
                    nfcChecked.setText(getResources().getString(R.string.nfc__not_OK));
                    nfcChecked.setTextColor(getResources().getColor(R.color.nfcNotOK));
                }
            }
        }
    }
}
