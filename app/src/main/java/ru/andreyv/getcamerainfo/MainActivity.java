package ru.andreyv.getcamerainfo;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CameraManager mCameraManager = null;
    private TextView textView;
    private String[] cameraList = null;
    private String camera_props = null;
    CameraCharacteristics cc;
    private CameraDevice cameraDevice;
    private boolean cameraClosed;
    private static final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
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
                    ) {
                mCameraManager.openCamera("0", stateCallback, null);
                Log.d("INF", "camera opened");
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
    /**
     * checking  permissions at Runtime.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        final List<String> neededPermissions = new ArrayList<>();
        for (final String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermissions();
                }
            }
        }
    }
}
