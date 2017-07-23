/**
 * Created by aditya on 21/7/17.
 */
package org.openunicorn.reducio;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
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
    //File[] imageList = newImages();
    //String sdcardPath = imageList[0].toString();
    TextView textView = (TextView) findViewById(R.id.textView1);
    //textView.setText(sdcardPath);
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
    //File[] images = cameraFolder.listFiles();
    return cameraFolder;
  }

  protected File[] newImages()
  {
    File imageList = readImages();
    String[] imageListNames = imageList.list();
    String oldImages = "";
    SharedPreferences sharedPreferences = getSharedPreferences(COMPRESSED_LIST, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    oldImages = sharedPreferences.getString("imageList","empty");
    TextView textView1 = (TextView) findViewById(R.id.textView1);
    textView1.setText(oldImages);
    String[] oldImagesArray = oldImages.split(",");
    ArrayList<String> oldImagesList = new ArrayList<>();
    ArrayList<String> newImagesList = new ArrayList<>();
    if(!oldImages.equalsIgnoreCase("empty"))
    {
      for (int i = 0; i < oldImagesArray.length; i++) {
        oldImagesList.add(oldImagesArray[i]);
      }
    }
    else
    {
      for(int i=0; i<imageListNames.length;i++)
      {
        oldImages=oldImages+","+imageListNames[i];
      }
      for (int i = 0; i < oldImagesArray.length; i++) {
        oldImagesList.add(oldImagesArray[i]);
      }
    }
    for (String imageName : imageListNames) {
      if (!oldImagesList.contains(imageName)) {
        newImagesList.add(imageName);
      }
    }
    for(String newImage: newImagesList)
    {
      oldImages = oldImages + "," + newImage;
    }
    editor.putString("imageList",oldImages);
    int i=0;
    Log.d("newI",Integer.toString(newImagesList.size()));
    File[] newImageList = new File[newImagesList.size()];
    for(String newImage:newImagesList)
    {
      newImageList[i] = new File(imageList.toString() +"/"+ newImage);
      i++;
    }
    TextView textView2 = (TextView) findViewById(R.id.textView3);
    textView2.setText(Integer.toString(newImagesList.size()));
    editor.commit();
    return newImageList;
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
    File[] imageList = newImages();
    if (imageList.length > 0) {
      File imageFile = imageList[0];
      Bitmap bitmap = null;
      try {
        bitmap = new ImageZipper(MainActivity.this).compressToBitmap(imageFile);
      } catch (IOException e) {
        System.out.println(e.getLocalizedMessage());
      }
      writeImages(bitmap, imageFile.getName());
    }
  }
}
