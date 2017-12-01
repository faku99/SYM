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

public class SensorsActivity extends AppCompatActivity implements SensorEventListener {

    //opengl
    private OpenGLRenderer  opglr           = null;
    private GLSurfaceView m3DView           = null;
    private SensorManager sensorManager     = null;
    private Sensor accelerometer            = null;
    private Sensor magnetometer             = null;

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
        setContentView(R.layout.activity_compass);

        // link to GUI
        this.m3DView = (GLSurfaceView) findViewById(R.id.compass_opengl);

        //we create the 3D renderer
        this.opglr = new OpenGLRenderer(getApplicationContext());

        //init opengl surface view
        this.m3DView.setRenderer(this.opglr);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == accelerometer) {
            accelerometerData = event.values;
            gotAccelerometerData = true;
        } else if (event.sensor == magnetometer) {
            magnetometerData = event.values;
            gotMagnetometerData = true;
        }

        if (gotAccelerometerData && gotMagnetometerData) {
            SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData, magnetometerData);
            rotationMatrix = this.opglr.swapRotMatrix(rotationMatrix);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}