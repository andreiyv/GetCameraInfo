package ru.andreyv.getcamerainfo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CameraManager mCameraManager = null;
    private TextView textView;
    private String[] cameraList = null;
    private String camera_props = null;
    CameraCharacteristics cc;
    private CameraDevice cameraDevice;
    private boolean cameraClosed;
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

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                mCameraManager.openCamera("0", stateCallback, null);
            }
        } catch (final CameraAccessException e) {
            Log.e("ERR", " exception occurred while opening camera " + "0", e);
        }




    }
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraClosed = false;
            Log.d("INF", "camera " + camera.getId() + " opened");
            cameraDevice = camera;
            Log.i("INF", "Taking picture from camera " + camera.getId());
            //Take the picture after some delay. It may resolve getting a black dark photos.
            //new Handler().postDelayed(() -> {

            //}
            //, 500);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.d("INF", " camera " + camera.getId() + " disconnected");
            if (cameraDevice != null && !cameraClosed) {
                cameraClosed = true;
                cameraDevice.close();
            }
        }

        @Override
        public void onClosed(@NonNull CameraDevice camera) {
            cameraClosed = true;
            Log.d("INF", "camera " + camera.getId() + " closed");
            //once the current camera has been closed, start taking another picture
//            if (!cameraIds.isEmpty()) {
        //        takeAnotherPicture();
  //          } else {
    //            capturingListener.onDoneCapturingAllPhotos(picturesTaken);
      //      }
        }


        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e("ERR", "camera in error, int code " + error);
            if (cameraDevice != null && !cameraClosed) {
                cameraDevice.close();
            }
        }
    };

}
