package com.ucmate.scancord.scan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.ucmate.scancord.BuildConfig;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

public class ImageScanner {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    static String currentPhotoPath = "";
    public static String OpenCamera(Activity activity) {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getImageFile();
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID.concat(".provider"), file);
        else
            uri = Uri.fromFile(file);
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(pictureIntent, PICK_FROM_CAMERA);
        return currentPhotoPath;
    }

    private static File getImageFile() {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        File file = null;
        file = new File(storageDir, imageFileName + ".jpg");
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }


    public static void OpenCropActivity(Uri sourceUri, Uri destinationUri, Activity activity) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(100);
        options.setFreeStyleCropEnabled(true);
        options.setAllowedGestures(
                UCropActivity.ALL,
                UCropActivity.ALL,
                UCropActivity.ALL
        );
        try {
            UCrop.of(sourceUri, destinationUri).withOptions(options)
                    .start(activity);
        }
        catch (Exception e){
            Toast.makeText(activity, "Error while crop image please report, msg - "+e, Toast.LENGTH_SHORT).show();
        }
    }

    public static void openImagesDocument(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()){
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s",activity.getApplicationContext().getPackageName())));
                    activity.startActivityForResult(intent, 2296);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    activity.startActivityForResult(intent, 2296);
                }}
        }
        Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pictureIntent.setType("image/*");
        pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = new String[]{"image/jpeg", "image/png"};
        pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        activity.startActivityForResult(Intent.createChooser(pictureIntent,"Select Picture"), PICK_FROM_GALLERY);  // 4
    }

    public static File getImageFileName(String imageFileName) {
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        File file = null;
        file = new File(storageDir, imageFileName);
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    public static void CreateCameraFolder() {
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
    }

}
