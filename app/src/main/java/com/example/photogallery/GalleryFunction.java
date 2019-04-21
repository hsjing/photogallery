package com.example.photogallery;

import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GalleryFunction {
    // Display photo
    static void displayPhoto(MainActivity mainActivity, String path)
            throws IOException {
        if (path != MainActivity.previousPath) {
            ImageView iv = mainActivity.findViewById(R.id.selectedImage);
            iv.setImageBitmap((BitmapFactory.decodeFile(path)));

            TextView locationShort = mainActivity.findViewById(R.id.locationShort);

            // Get the path
            String currentPhotoPath =
                    MainActivity.photoGallery.get(MainActivity.currentPhotoIndex);
            ExifInterface currentExif = new ExifInterface(currentPhotoPath);

            // Get latLong
            currentExif.getLatLong(mainActivity.latLong);

            // Translate into address
            Geocoder geo = new Geocoder(mainActivity.getApplicationContext(),
                    Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(mainActivity.latLong[0],
                    mainActivity.latLong[1], 1);

            locationShort.setText(
                    addresses.get(0).getLocality() + ", " +
                            addresses.get(0).getCountryName() + " (" +
                            currentExif.getAttribute(ExifInterface.TAG_DATETIME) + ")");
            MainActivity.previousPath = path;
        }
    }

    // TODO: add date arguments
    static ArrayList<String> fillGallery(MainActivity mainActivity,
                                         String userLocTag, String fromDate,
                                         String toDate)
            throws IOException, ParseException {
        // Storage path
        File galleryPath = new File("/storage/emulated/0/DCIM/Camera");
        ArrayList<String> filledGallery = new ArrayList<>();

        ExifInterface currentExif;
        Geocoder geo;
        List<Address> addresses;
        Date date;

        Boolean include;

        float[] fillLatLong = new float[2];

        // All parameters default, display all images
        if (userLocTag.equals("") && fromDate.equals("01/01/0001") &&
                toDate.equals("01/01/9999")) {
            for (File photo : galleryPath.listFiles()) {
                filledGallery.add(photo.getPath());
            }
        } else {
            Date tempFromDate = new SimpleDateFormat("dd/MM/yyyy").parse(fromDate);
            Date tempToDate = new SimpleDateFormat("dd/MM/yyyy").parse(toDate);

            for (File photo : galleryPath.listFiles()) {
                currentExif = new ExifInterface(photo.getPath());
                currentExif.getLatLong(fillLatLong);
                // TODO: refractor Geocoder object to another scope, free up s
                geo = new Geocoder(mainActivity.getApplicationContext(),
                        Locale.getDefault());
                addresses = geo.getFromLocation(fillLatLong[0], fillLatLong[1], 1);
                date = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
                        .parse(currentExif.getAttribute(ExifInterface.TAG_DATETIME));

                if ((!userLocTag.equals("") &&
                        !(addresses.get(0).getCountryName().equalsIgnoreCase(userLocTag) ||
                                addresses.get(0).getLocality().equalsIgnoreCase(userLocTag))) ||
                        date.after(tempToDate) || date.before(tempFromDate)) {
                    // TODO: if any test fails, do something here (maybe warn)
                } else {
                    filledGallery.add(photo.getPath());
                }
            }
        }
        return filledGallery;
    }
}
