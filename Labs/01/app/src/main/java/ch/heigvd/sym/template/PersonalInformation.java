package ch.heigvd.sym.template;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.List;

public class PersonalInformation extends AppCompatActivity {


    private ImageView facePicture = null;

    private TextView imei = null;

    private TextView email = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        Intent intent = getIntent();

        email = findViewById(R.id.email_shown);
        imei = findViewById(R.id.imei);

        email.setText(intent.getStringExtra(MainActivity.EMAIL_ENTERED));

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
                    switch (response.getPermissionName()) {
                        case Manifest.permission.READ_EXTERNAL_STORAGE:
                            File pictureAsFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/0fd.jpg");
                            facePicture = findViewById(R.id.facePicture);
                            Bitmap facePictureBmp = BitmapFactory.decodeFile(pictureAsFile.getAbsolutePath());
                            facePicture.setImageBitmap(facePictureBmp);
                            break;
                        case Manifest.permission.READ_PHONE_STATE:
                            TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            imei.setText(mTelephonyManager.getDeviceId());
                            // For API lvl 26 and more use the line bellow
                            //imei.setText(mTelephonyManager.getImei());
                    }
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

    }
}
