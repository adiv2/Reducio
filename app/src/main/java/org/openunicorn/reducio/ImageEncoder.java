/**
 * Created by aditya on 21/7/17.
 */
package org.openunicorn.reducio;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import com.developers.imagezipper.ImageZipper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageEncoder extends AppCompatActivity
{
  private String imageName;
  private String imageType;
  private String filePath;
  float compressionRatio;
  ArrayList<String> images;

  private void encode() throws IOException
  {
    File imageFile = new File(filePath);
    Bitmap bitmap = new ImageZipper(ImageEncoder.this).compressToBitmap(imageFile);
  }
}
