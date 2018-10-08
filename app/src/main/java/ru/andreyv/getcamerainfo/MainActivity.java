package ru.andreyv.getcamerainfo;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CameraManager mCameraManager = null;
    private TextView textView;
    private String[] cameraList = null;
    private String camera_props = null;
    CameraCharacteristics cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraList = mCameraManager.getCameraIdList();
            for (String CameraID : cameraList) {
                camera_props = camera_props + "camera " + CameraID + "\n";
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        textView = findViewById(R.id.cameraInfo);

        try {
            cc = mCameraManager.getCameraCharacteristics("0");
            StreamConfigurationMap configurationMap = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            System.out.print("");
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


        textView.setText(camera_props);

    }


}
