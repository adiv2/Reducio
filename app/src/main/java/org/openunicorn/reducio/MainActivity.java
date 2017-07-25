/**
 * Created by aditya on 21/7/17.
 */
package org.openunicorn.reducio;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ExifInterface;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
  public static final String COMPRESSED_LIST = "compressedList";
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    getStoragePermission();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
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

  protected File readImages()
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
    return cameraFolder;
  }

  protected File[] newImages()
  {
    File imageList = readImages();
    File[] allImages = imageList.listFiles();
    ArrayList<File> newImageArrayList = new ArrayList<>();
    for(File image:allImages)
    {
      try
      {
        Log.d("Path",image.getAbsolutePath().toString());
        ExifInterface exifInterface = new ExifInterface(image.getAbsolutePath().toString());
        if(exifInterface.getAttribute(ExifInterface.TAG_ARTIST)==null)
        {
          newImageArrayList.add(image);
        }
        else if(!exifInterface.getAttribute(ExifInterface.TAG_ARTIST).equalsIgnoreCase("reducio"))
        {
          Log.d("newArt",exifInterface.getAttribute(ExifInterface.TAG_ARTIST));
          newImageArrayList.add(image);
        }
      }
      catch (IOException e)
      {
        Log.d("IOE",e.getLocalizedMessage());
      }
    }
    File[] newImageArray = new File[newImageArrayList.size()];
    for(int i=0;i<newImageArray.length;i++)
    {
      newImageArray[i] = newImageArrayList.get(i);
    }
    TextView textView = (TextView) findViewById(R.id.textView1);
    textView.setText(Integer.toString(newImageArray.length));
    return newImageArray;
  }

  protected void writeImages(Bitmap image,String imageName)
  {
    File sdcard  = Environment.getExternalStorageDirectory();
    File testIP  = new File(sdcard.toString()+"/"+"TestIP");
    testIP.mkdir();
    testIP.mkdirs();
    File testImage = new File(testIP,imageName);
    FileOutputStream fileOutputStream=null;
    try
    {
      fileOutputStream = new FileOutputStream(testImage);
      image.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
      fileOutputStream.close();
    }
    catch (Exception e)
    {e.printStackTrace();}
    try
    {
      ExifInterface exifInterface = new ExifInterface(testImage.getAbsolutePath().toString());
      exifInterface.setAttribute(ExifInterface.TAG_ARTIST,"Reducio");
      exifInterface.saveAttributes();
      Log.d("Artist",exifInterface.getAttribute(ExifInterface.TAG_ARTIST));
    }
    catch (IOException e)
    {
      Log.d("IOE",e.getLocalizedMessage());
    }
  }

  protected void encode()
  {
    File[] imageList = newImages();
    if (imageList.length > 0) {
      for (File image : imageList)
      {
        File imageFile = image;
        Bitmap bitmap = null;
        try
        {
          bitmap = new ImageZipper(MainActivity.this).compressToBitmap(imageFile);
        } catch (IOException e) {
          System.out.println(e.getLocalizedMessage());
        }
        writeImages(bitmap, imageFile.getName());
      }
    }
  }
}
