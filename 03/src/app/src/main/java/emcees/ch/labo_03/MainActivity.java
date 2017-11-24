package emcees.ch.labo_03;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launchTransmitActivity(View view) {

        Intent intent;

        switch (view.getId()) {
            case R.id.buttonNFC:
                intent = new Intent(this, NFC.class);
                break;
            case R.id.buttonBarcode:
                intent = new Intent(this, Barcode.class);
                break;
            case R.id.buttoniBeacon:
                intent = new Intent(this, iBeacon.class);
                break;
            case R.id.buttonSensors:
                intent = new Intent(this, Sensors.class);
                break;
            default:
                return;
        }
        startActivity(intent);
    }
}
