package com.example.photogallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.photogallery.GalleryFunction.*;

public class MainActivity
        extends AppCompatActivity implements View.OnClickListener {

    public static int currentPhotoIndex = 0; // default to first picture
    public static ArrayList<String> photoGallery = new ArrayList<String>();
    public static String userLocationTag = "";
    public static String previousPath = null;
    public static String fromDate = "01/01/0001";
    public static String toDate = "01/01/9999";
    String currentPhotoPath;

    float[] latLong = new float[2];
    // 'Search' handler navigates to 'Search' activity
    private View.OnClickListener searchListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            startActivityForResult(i, RequestCode.SEARCH_ACTIVITY_REQUEST_CODE);
        }
    };

    private View.OnClickListener locationListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Create intent and send lat/long info to map activity
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            i.putExtra("lat", latLong[0]);
            i.putExtra("long", latLong[1]);
            startActivityForResult(i, RequestCode.MAP_ACTIVITY_REQUEST_CODE);
        }
    };

    private View.OnClickListener snapListener = new View.OnClickListener() {
        public void onClick(View v) {
            dispatchTakePictureIntent();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        setContentView(R.layout.activity_default);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(Color.parseColor("#3F51B5")));

        Window window = getWindow();
        window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3F51B5"));

        // Check for runtime permissions
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    RequestCode.REQUEST_READ_PHONE_STATE_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RequestCode.REQUEST_READ_PHONE_STATE_PERMISSION);
        }

        try {
            photoGallery = fillGallery(
                    this, userLocationTag, fromDate,
                    toDate); // Otherwise, permission already granted
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        if (!photoGallery.isEmpty()) {
            try {
                displayPhoto(this, photoGallery.get(currentPhotoIndex));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Button listeners
        Button btnLeft = (Button) findViewById(R.id.btnLeft);
        Button btnRight = (Button) findViewById(R.id.btnRight);
        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        Button btnSnap = (Button) findViewById(R.id.btnSnap);

        Button btnLocation = (Button) findViewById(R.id.locationShort);

        // Left and right handled by default onClick
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);

        // 'Search' and 'Snap' has custom handlers
        btnSearch.setOnClickListener(searchListener);
        btnLocation.setOnClickListener(locationListener);
        btnSnap.setOnClickListener(snapListener);

        Thread.dumpStack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                // TODO: add something for cancelled search
            } else if (resultCode == Activity.RESULT_OK) {
                userLocationTag = data.getStringExtra("SEARCH_LOCATION");
                try {
                    fromDate = data.getStringExtra("SEARCH_FROM_DATE");
                    toDate = data.getStringExtra("SEARCH_TO_DATE");

                    photoGallery = fillGallery(this, userLocationTag,
                            fromDate, toDate);
                    if (!photoGallery.isEmpty())
                        displayPhoto(this,
                                photoGallery.get(currentPhotoIndex));
                    // TODO: add empty display here
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == RequestCode.MAP_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                try {
                    photoGallery = fillGallery(this, userLocationTag,
                            fromDate, toDate);
                    if (!photoGallery.isEmpty())
                        displayPhoto(this,
                                photoGallery.get(currentPhotoIndex));
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
                // TODO: add something for cancelled search
            }
        }

        if (requestCode == RequestCode.CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    photoGallery = fillGallery(this, userLocationTag,
                            fromDate, toDate);
                    if (!photoGallery.isEmpty())
                        displayPhoto(this,
                                photoGallery.get(currentPhotoIndex));
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onClick(View v) {
        // Iterate photos
        if (v.getId() == R.id.btnLeft && currentPhotoIndex > 0)
            --currentPhotoIndex;
        else if (v.getId() == R.id.btnRight &&
                currentPhotoIndex < (photoGallery.size() - 1))
            ++currentPhotoIndex;

        // Display photos
        if (!photoGallery.isEmpty()) {
            try {
                displayPhoto(this, photoGallery.get(currentPhotoIndex));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Creates a temporary image to later fill
    public File createImageFile() throws IOException {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imName = "JPEG_" + ts + "_";
        File storageDir = new File("/storage/emulated/0/DCIM/Camera/");
        File im = File.createTempFile(imName, ".jpg", storageDir);

        currentPhotoPath = im.getAbsolutePath();
        return im;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        this, "com.example.photogallery.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent,
                        RequestCode.CAMERA_REQUEST_CODE);
            }
        }
    }
}
