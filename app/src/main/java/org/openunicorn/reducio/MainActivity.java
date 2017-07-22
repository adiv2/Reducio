/**
 * Created by aditya on 21/7/17.
 */
package org.openunicorn.reducio;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.developers.imagezipper.ImageZipper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
{
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    getStoragePermission();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    File[] imageList = readImages();
    String sdcardPath = imageList[0].toString();
    TextView textView = (TextView) findViewById(R.id.textView1);
    textView.setText(sdcardPath);
    encode();
  }

  protected void getStoragePermission()
  {
    if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
    {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
      {
        //Do stuff here if you want - Aditya
      }
      else
      {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
      }
    }
  }

  protected File[] readImages()
  {
    File sdcard = Environment.getExternalStorageDirectory();
    String cameraPath = sdcard.getAbsolutePath();
    String[] files;
    while(!cameraPath.toLowerCase().contains("testip"))
    {
      File storage = new File(cameraPath);
      files = storage.list();
      for (String file : files)
      {
        if (file.toString().equalsIgnoreCase("testip") || file.toString().equalsIgnoreCase("camera"))
        {
          cameraPath = cameraPath + "/" + file.toString();
        }
      }
    }
    File cameraFolder = new File(cameraPath);
    File[] images = cameraFolder.listFiles();
    return images;
  }

  protected void writeImages(Bitmap image,String imageName)
  {
    File sdcard  = Environment.getExternalStorageDirectory();
    File testIP  = new File(sdcard.toString()+"/"+"TestIP");
    testIP.mkdir();
    testIP.mkdirs();
    File testImage = new File(testIP,imageName);
    if(testIP.exists())
    {
      //TextView textView = (TextView) findViewById(R.id.textView1);
     // textView.setText(testImage.toString());
    }
    FileOutputStream fileOutputStream=null;
    try
    {
      fileOutputStream = new FileOutputStream(testImage);
      image.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
      fileOutputStream.close();
    }
    catch (Exception e)
    {e.printStackTrace();}
  }

  protected void encode()
  {
    File[] imageList = readImages();
    File imageFile = imageList[0];
    Bitmap bitmap=null;
    try
    {
      bitmap = new ImageZipper(MainActivity.this).compressToBitmap(imageFile);
    }
    catch (IOException e)
    {
      System.out.println(e.getLocalizedMessage());}
      writeImages(bitmap,imageFile.getName());

  }
}
