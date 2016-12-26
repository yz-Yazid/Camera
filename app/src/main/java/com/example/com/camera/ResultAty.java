package com.example.com.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ResultAty extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_aty);
        imageView = (ImageView) findViewById(R.id.imageView0);
        //获取到存储照片的文件路径。
        String path = getIntent().getStringExtra("filePath");
        try {
            //使用文件读入流，进行读取该照片数据。
            FileInputStream fis = new FileInputStream(new File(path));
            //定义一个矩阵,把图片调整90度.
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        Bitmap bitmap = BitmapFactory.decodeFile(path);
//        imageView.setImageBitmap(bitmap);
    }
}
