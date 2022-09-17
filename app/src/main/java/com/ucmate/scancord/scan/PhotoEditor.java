package com.ucmate.scancord.scan;

import static com.ucmate.scancord.db.db.InsertTempFile;
import static com.ucmate.scancord.helper.ImageHelper.EnhanceImage;
import static com.ucmate.scancord.helper.ImageHelper.UriToBitmap;
import static com.ucmate.scancord.helper.SaveFile.CreateDirectoryAndSaveFile;
import static com.ucmate.scancord.helper.SaveFile.randomName;
import static com.ucmate.scancord.helper.UiChange.Change;
import static com.ucmate.scancord.scan.ImageScanner.OpenCropActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ucmate.scancord.MainActivity;
import com.ucmate.scancord.R;
import com.ucmate.scancord.SavetoPdf;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.Objects;

public class PhotoEditor extends AppCompatActivity {
    SeekBar brt, cfx;
    ImageView preview;
    Bitmap imageBit, enhance;
    int brtProcess, cfxProcess = 1;
    TextView progress;
    Activity activity;
    Uri fileUri;
    String filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);
        Change(this, getWindow());
        activity = this;
        String html = MessageFormat.format("<font color='#000'>{0}</font>", "Image Enhancer ");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
        getSupportActionBar().setTitle(Html.fromHtml(html));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        filename = randomName();

        brt = findViewById(R.id.brightness);
        cfx = findViewById(R.id.contrast);
        preview = findViewById(R.id.preview);
        progress = findViewById(R.id.progress);
        progress.setText("0");

        Intent intent = getIntent();
        String image_path= intent.getStringExtra("imagePath");
        fileUri = Uri.parse(image_path);
        imageBit = Objects.requireNonNull(UriToBitmap(fileUri, this));
        preview.setImageBitmap(imageBit);
        enhance = imageBit;
        brt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress.setText(Html.fromHtml(String.format("Brightness: <b>%d</b> ", i)));
                brtProcess = i;
                enhance = EnhanceImage(imageBit, cfxProcess, brtProcess);
                preview.setImageBitmap(enhance);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        cfx.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress.setText(Html.fromHtml(String.format("Contrast: <b>%d</b> ", i)));
                cfxProcess = i;
                enhance = EnhanceImage(imageBit, cfxProcess, brtProcess);
                preview.setImageBitmap(enhance);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.save:
            String file_dir = CreateDirectoryAndSaveFile(enhance,"tmp_file",filename);
            if (file_dir.contains("tmp_file"))
            {
                if (InsertTempFile(getApplicationContext(), file_dir, filename )) {
                    Intent save = new Intent(Intent.ACTION_VIEW);
                    save.setClass(this, SavetoPdf.class);
                    startActivity(save);
                }
            }
            return(true);
        case R.id.crop:
            OpenCropActivity(fileUri, fileUri, activity);
            return(true);
        default:
            break;
    }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent .setClass(this,  PhotoEditor.class);
            intent.putExtra("imagePath", UCrop.getOutput(data).toString());
            startActivity(intent );
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(activity, "ERROR While Crop "+cropError, Toast.LENGTH_SHORT).show();
        }
    }
    }
