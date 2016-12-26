package com.example.com.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CustomCamera extends Activity implements View.OnClickListener, SurfaceHolder.Callback {

    private Button button;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //新创建一个文件。
            File file = new File("/sdcard/text.png");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                try {
                    //把拍照的数据存储在文件中。
                    fos.write(data);
                    fos.close();
                    Intent i = new Intent(CustomCamera.this,ResultAty.class);
                    i.putExtra("filePath",file.getAbsolutePath());
                    startActivity(i);
                    CustomCamera.this.finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);
        /**
         * 检查android 6 版本请求允许.
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i("TEST", "Granted");
            //init(barcodeScannerView, getIntent(), null);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);//1 can be another integer
        }

        button = (Button) findViewById(R.id.button4);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        if (mSurfaceView != null) {
            mHolder = mSurfaceView.getHolder();
        }
        //为SurfaceHolder设置Callback事件。
        mHolder.addCallback(this);
        //为button设置点击事件。
        if (button != null) {
            button.setOnClickListener(this);
        }
        //为SurfaceView设置点击事件，可以聚焦。
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //使相机聚焦。
                mCamera.autoFocus(null);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button4:
                //获取相机的参数。
                final Camera.Parameters parameters = mCamera.getParameters();
                //设置拍照的格式。
                parameters.setPictureFormat(ImageFormat.JPEG);
                //设置拍照的的范围.
                parameters.setPictureSize(800,400);
                //设置相机的对焦模式.
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                //设置Camera的对焦，if成功，则进行拍照功能。
                mCamera.autoFocus(new Camera.AutoFocusCallback(){
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success){
                            mCamera.takePicture(null,null,pictureCallback);
                        }
                    }
                });
                break;
        }

    }
    //当该Activity被创建时，获取Camera对象，并且绑定视图。
    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera();
            if (mHolder != null) {
                bindSurfaceView(mCamera, mHolder);
            }
        }
    }
    //当该Activity退出时，释放Camera资源。
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }


    private Camera getCamera() {
        Camera camera;
        try {
            //返回一个Camera对象，后置摄像头0。
            camera = Camera.open(0);
        } catch (Exception e) {
            //返回一个Camera对象，后置的摄像头1.
            camera = Camera.open(1);
            e.printStackTrace();
        }
        return camera;
    }

    //Camera和视图绑定。
    private void bindSurfaceView(Camera camera, SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            //将系统Camera的预览角度进行调整。
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //释放Camera资源。
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
    //实现的SurfaceHolder.Callback接口。
    //当视图创建时，就和Camera绑定视图。
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bindSurfaceView(mCamera, mHolder);
    }
    //当视图改变时，Camera先停止捕捉视图，然后Change后，再绑定视图。
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        bindSurfaceView(mCamera, mHolder);
    }
    //当视图被销毁时，释放Camera的资源。
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }
}
