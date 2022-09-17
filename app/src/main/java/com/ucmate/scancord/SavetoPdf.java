package com.ucmate.scancord;
import static com.ucmate.scancord.MainActivity.RefreshFolderView;
import static com.ucmate.scancord.db.db.DeleteImageQueue;
import static com.ucmate.scancord.db.db.FetchFolder;
import static com.ucmate.scancord.db.db.FetchImage;
import static com.ucmate.scancord.db.db.InsertFile;
import static com.ucmate.scancord.db.db.InsertFolder;
import static com.ucmate.scancord.helper.SaveFile.createPDFWithMultipleImage;
import static com.ucmate.scancord.helper.SaveFile.randomPdfName;
import static com.ucmate.scancord.helper.UiChange.Change;
import static com.ucmate.scancord.scan.ImageScanner.CreateCameraFolder;
import static com.ucmate.scancord.scan.ImageScanner.OpenCamera;
import static com.ucmate.scancord.scan.ImageScanner.OpenCropActivity;
import static com.ucmate.scancord.scan.ImageScanner.openImagesDocument;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ucmate.scancord.helper.ImageAdapter;
import com.ucmate.scancord.scan.PhotoEditor;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.util.FileUtils;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;

public class SavetoPdf extends AppCompatActivity {
    Bitmap[] mThumbIds;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLERY = 2;
    static String currentPhotoPath;
    Activity activity;
    int lengthKb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saveto_pdf);
        activity = this;
        Change(this, getWindow());
        String html = MessageFormat.format("<font color='#000'>{0}</font>", "Image to Pdf ");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
        getSupportActionBar().setTitle(Html.fromHtml(html));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        Object[] arrayObjects = FetchImage(this);
        ArrayList<String> filePath= (ArrayList<String>) arrayObjects[0];
        ArrayList<String> fileName = (ArrayList<String>) arrayObjects[1];
        mThumbIds = new Bitmap[filePath.size()+1];

        for (int i=0; i<filePath.size(); i++){
            File imgFile = new  File(filePath.get(i));
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                if (myBitmap != null)
                    mThumbIds[i] = myBitmap;
            }
        }
        mThumbIds[filePath.size()] = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.app_promo);
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, mThumbIds));
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.scanner);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChooseAgainOptions(activity);

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.saveas, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.save:
            ChooseFolder(activity);
                 return(true);
        default:
            break;
    }
        return(super.onOptionsItemSelected(item));
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public static void ChooseAgainOptions(Activity activity){
        String[] colors = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose Option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            Uri uri = Uri.parse(currentPhotoPath);
            OpenCropActivity(uri, uri, activity);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent .setClass(this,  PhotoEditor.class);
                intent.putExtra("imagePath", UCrop.getOutput(data).toString());
                startActivity(intent );
            }
            catch (Exception e){
                Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            try {
                String Final_Uri = "file:"+ FileUtils.getPath(getApplicationContext(), data.getData());
                Uri crop = Uri.parse(Final_Uri);
                OpenCropActivity(crop, crop, activity);
            }
            catch (Exception e){
                Toast.makeText(activity, "Error while crop - image URI: "+data.getData(), Toast.LENGTH_SHORT).show();
            }
        }
        else if(resultCode==UCrop.RESULT_ERROR){
            Throwable throwable=UCrop.getError(data);
            throwable.printStackTrace();
            Log.d("Error While Crop ",throwable.getMessage());
            Toast.makeText(activity, "Error while crop "+throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void ChooseFolder(Activity activity){
        String file_name = randomPdfName();
        Object[] arrayObjects = FetchFolder(activity);
        ArrayList<String> folderName = (ArrayList<String>) arrayObjects[1];
        ArrayList<Integer> folderId = (ArrayList<Integer>) arrayObjects[0];
        String[] colors = folderName.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose Folder");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int get_id = folderId.get(which);
                if (get_id==-1){
                    CreateFolderDialog(activity);
                    DeleteImageQueue(getApplicationContext());
                }
                else {
                    ProgressDialog dialogs = new ProgressDialog(SavetoPdf.this);
                    dialogs.setMessage("Please wait...");
                    dialogs.show();

                    String[] colors = {"High", "Normal", "Low"};
                    AlertDialog.Builder builders = new AlertDialog.Builder(activity);
                    builders.setTitle("Choose Option");
                    builders.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                    String file_path = createPDFWithMultipleImage(mThumbIds, file_name, 90, 1653.0F);
                                    if(file_path.contains(".pdf")){
                                        DeleteImageQueue(getApplicationContext());
                                        File file = new File(file_path);
                                        lengthKb = Math.toIntExact(file.length() / 1024);
                                        dialogs.dismiss();
                                    }
                                    else {
                                        dialogs.dismiss();
                                        Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                                    }
                                    if (InsertFile(activity, get_id, file_name, lengthKb, file_path)){
                                        dialogs.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                        }
                                    }, 300);
                                    break;
                                case 1:
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                    String file_path = createPDFWithMultipleImage(mThumbIds, file_name, 60, 827.0F);
                                    if(file_path.contains(".pdf")){
                                        DeleteImageQueue(getApplicationContext());
                                        File file = new File(file_path);
                                        lengthKb = Math.toIntExact(file.length() / 1024);
                                        dialogs.dismiss();
                                    }
                                    else {
                                        dialogs.dismiss();
                                        Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                                    }
                                    if (InsertFile(activity, get_id, file_name, lengthKb, file_path)){
                                        dialogs.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                        }
                                    }, 300);
                                    break;
                                case 2:
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                    String file_path = createPDFWithMultipleImage(mThumbIds, file_name, 30, 595.0F);
                                    if(file_path.contains(".pdf")){
                                        DeleteImageQueue(getApplicationContext());
                                        File file = new File(file_path);
                                        lengthKb = Math.toIntExact(file.length() / 1024);
                                        dialogs.dismiss();
                                    }
                                    else {
                                        dialogs.dismiss();
                                        Toast.makeText(activity, R.string.failed, Toast.LENGTH_SHORT).show();
                                    }
                                    if (InsertFile(activity, get_id, file_name, lengthKb, file_path)){
                                        dialogs.dismiss();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                            }
                        }, 300);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    builders.show();
                }
            }
        });
        builder.show();
    }

    public void CreateFolderDialog(Context context){
        EditText folderInput = new EditText(context);
        folderInput.setHint("Folder Name");
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(60, 10, 10, 15);
        folderInput.setLayoutParams(lp);
        folderInput.setGravity(Gravity.CENTER|android.view.Gravity.LEFT);
        folderInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        folderInput.setLines(1);
        folderInput.setMaxLines(1);
        container.addView(folderInput, lp);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Create Folder")
                .setView(container)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String folder_name = folderInput.getText().toString();
                        dialogInterface.dismiss();
                        if(InsertFolder(context, folder_name,"internal")){
                            RefreshFolderView();
                        }
                        else {
                            Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

}