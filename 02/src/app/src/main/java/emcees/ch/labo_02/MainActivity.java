package emcees.ch.labo_02;

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
            case R.id.buttonAsync:
                intent = new Intent(this, AsyncTransmit.class);
                break;
            case R.id.buttonDiffered:
                intent = new Intent(this, DifferedTransmit.class);
                break;
            case R.id.buttonObject:
                intent = new Intent(this, ObjectTransmit.class);
                break;
            case R.id.buttonCompressed:
                intent = new Intent(this, CompressedTransmit.class);
                break;
            default:
                return;
        }
        startActivity(intent);
    }
}
