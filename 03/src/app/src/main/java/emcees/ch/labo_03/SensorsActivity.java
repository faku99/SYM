package emcees.ch.labo_03;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SensorsActivity extends AppCompatActivity implements SensorEventListener {

    //opengl
    private OpenGLRenderer opglr = null;
    private GLSurfaceView m3DView = null;
    private SensorManager sensorManager = null;
    private Sensor accelerometer = null;
    private Sensor magnetometer = null;

    private float[] rotationMatrix = new float[16];
    private float[] accelerometerData = new float[4];
    private float[] magnetometerData = new float[4];

    private boolean gotAccelerometerData = false;
    private boolean gotMagnetometerData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // we need fullscreen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // we initiate the view
        setContentView(R.layout.activity_sensors);

        // link to GUI
        this.m3DView = (GLSurfaceView) findViewById(R.id.compass_opengl);

        //we create the 3D renderer
        this.opglr = new OpenGLRenderer(getApplicationContext());

        //init opengl surface view
        this.m3DView.setRenderer(this.opglr);

        // Create the sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometer == null || magnetometer == null) {
            Toast.makeText(this, getResources().getText(R.string.sensor_not_detected), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    protected void onResume() {
        super.onResume();

        // Register the sensors
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();

        // Unregister the sensors to improve battery life
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        // Get the data for the required sensor
        if (event.sensor == accelerometer) {
            accelerometerData = event.values;
        } else if (event.sensor == magnetometer) {
            magnetometerData = event.values;
        }

        // Using the sensors data, get the rotation matrix and send it to the view
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData, magnetometerData);
        this.opglr.swapRotMatrix(rotationMatrix);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}