package emcees.ch.labo_03;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class NFCActivity extends AppCompatActivity {


    private static final int NB_SEC_DECR_AUTHENTICATE_LEVEL = 30;

    private static final int AUTHENTICATE_MAX = 10;
    private static final int AUTHENTICATE_MEDIUM = 5;
    private static final int AUTHENTICATE_MIN = 0;

    private int currentAuthenticate = AUTHENTICATE_MAX;

    // Handler used to do the background task periodically
    private Handler handler;

    private TextView authenticateLevelText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        authenticateLevelText = (TextView) findViewById(R.id.authenticateLevelCounter);

        refreshAuthenticateLevel();

        handler = new Handler();
       /* differedSending = () -> {
            // Schedule another event to be run in 5s
            handler.postDelayed(differedSending, 30 * 1000);

            if (--currentAuthenticate != AUTHENTICATE_MIN) {
                authenticateLevelText.setText(currentAuthenticate);
            }
        };*/


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Schedule another event to be run in 5s
                handler.postDelayed(this, NB_SEC_DECR_AUTHENTICATE_LEVEL * 1000);

                if (currentAuthenticate != AUTHENTICATE_MIN) {
                    currentAuthenticate--;
                    refreshAuthenticateLevel();
                }

            }
        }, NB_SEC_DECR_AUTHENTICATE_LEVEL * 1000);

    }

    void refreshAuthenticateLevel() {
        int textColor;
        if (currentAuthenticate == AUTHENTICATE_MAX) {
            textColor = getResources().getColor(R.color.authentMax);
        } else if (currentAuthenticate > AUTHENTICATE_MEDIUM) {
            textColor = getResources().getColor(R.color.authentMedium);
        } else if (currentAuthenticate > AUTHENTICATE_MIN) {
            textColor = getResources().getColor(R.color.authentLow);
        } else {
            textColor = getResources().getColor(R.color.authentZero);
        }
        authenticateLevelText.setTextColor(textColor);
        authenticateLevelText.setText("" + currentAuthenticate);
    }

}
