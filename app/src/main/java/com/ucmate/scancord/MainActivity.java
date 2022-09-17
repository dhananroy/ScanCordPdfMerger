package com.ucmate.scancord;

import static com.ucmate.pdfmerger.MergePdf.MergePDF;
import static com.ucmate.scancord.db.db.CreateTable;
import static com.ucmate.scancord.db.db.DeleteImageQueue;
import static com.ucmate.scancord.db.db.FetchFile;
import static com.ucmate.scancord.folder.FolderList.GenerateView;
import static com.ucmate.scancord.helper.ImageHelper.pdfToBitmap;
import static com.ucmate.scancord.helper.Permissions.AllPermission;
import static com.ucmate.scancord.helper.UiChange.Change;
import static com.ucmate.scancord.scan.ImageScanner.CreateCameraFolder;
import static com.ucmate.scancord.scan.ImageScanner.OpenCamera;
import static com.ucmate.scancord.scan.ImageScanner.OpenCropActivity;
import static com.ucmate.scancord.scan.ImageScanner.openImagesDocument;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ucmate.scancord.scan.PhotoEditor;
import com.ucmate.scancord.tool.Tools;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;

    static Activity activity;
    static View appView;
    static String currentPhotoPath;
    private RelativeLayout ocrPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        activity = this;
        appView = getWindow().getDecorView();
        RecentItems();
        // Permissions
        AllPermission(activity);
        CreateTable(this);
        // View & Folder List
        Change(this, getWindow());
        GenerateView(this, getWindow().getDecorView());
        // Clean ImageQueue
        DeleteImageQueue(getApplicationContext());
        // Scanner
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.scanner);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CreateCameraFolder();
                ChooseOptions(activity);
            }
        });

        ocrPage = findViewById(R.id.ocrPage);
        ocrPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent .setClass(MainActivity.this,  Tools.class);
                startActivity(intent );

//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent .setClass(MainActivity.this,  OcrActivity.class);
//                startActivity(intent );
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            Uri uri = Uri.parse(currentPhotoPath);
            OpenCropActivity(uri, uri, activity);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setClass(this, PhotoEditor.class);
                intent.putExtra("imagePath", UCrop.getOutput(data).toString());
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            try {
                String Final_Uri = "file:" + FileUtils.getPath(getApplicationContext(), data.getData());
                Uri crop = Uri.parse(Final_Uri);
                OpenCropActivity(crop, crop, activity);
            } catch (Exception e) {
                Toast.makeText(activity, "Error while crop - image URI: " + data.getData(), Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable throwable = UCrop.getError(data);
            throwable.printStackTrace();
            Log.d("Error While Crop ", throwable.getMessage());
            Toast.makeText(activity, "Error while crop " + throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void RefreshFolderView() {
        GenerateView(activity, appView);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            AllPermission(activity);
        }
    }

    public static void ChooseOptions(Activity activity) {
        String[] colors = {
                "Camera",
                "Gallery"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose Option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        currentPhotoPath = OpenCamera(activity);
                        break;
                    case 1:
                        openImagesDocument(activity);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }
    public static void RecentItems() {

        Object[] arrayObjects = FetchFile(activity, -1);
        ArrayList < Integer > fileId = (ArrayList < Integer > ) arrayObjects[0];
        ArrayList < String > filePath = (ArrayList < String > ) arrayObjects[3];
        Bitmap[] mThumbIds = new Bitmap[fileId.size() + 1];
        for (int i = 0; i < fileId.size(); i++) {
            try {
                File newFile = new File(filePath.get(i));
                mThumbIds[i] = pdfToBitmap(newFile);
            } catch (Exception e) {
                mThumbIds[i] = BitmapFactory.decodeResource(activity.getResources(),
                        R.drawable.no_files);
            }

        }

        GridView gridview = (GridView) activity.findViewById(R.id.gridview);
        ViewCompat.setNestedScrollingEnabled(gridview, true);
        gridview.setAdapter(new GridViewAdapter(activity, mThumbIds));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView < ? > adapterView, View view, int i, long l) {
                String outputpath = Environment.getExternalStorageDirectory() + "/ScanCord/PdfFiles/";
                if (fileId.get(i) != -1)
                    Log.d("FILEPath ===>> ",filePath.get(i));
                    MergePDF(filePath.get(i), filePath.get(i),outputpath, "output.pdf");

//                OpenPdf(activity, filePath.get(i));
            }
        });
    }

}