package com.ucmate.scancord.ocr;

import static android.Manifest.permission_group.CAMERA;

import static com.ucmate.scancord.helper.DeleteFiles.DeleteSingleFile;
import static com.ucmate.scancord.helper.ImageHelper.UriToBitmap;
import static com.ucmate.scancord.helper.UiChange.Change;
import static com.ucmate.scancord.scan.ImageScanner.OpenCamera;
import static com.ucmate.scancord.scan.ImageScanner.OpenCropActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.ucmate.scancord.MainActivity;
import com.ucmate.scancord.R;
import com.ucmate.scancord.SplashActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OcrActivity extends AppCompatActivity {

    private EditText textView;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private TextRecognizer textRecognizer;
    private static final int PICK_FROM_CAMERA = 1;
    private TextToSpeech textToSpeech;
    private String stringResult = null;
    private String  currentPhotoPath;
    boolean flag=true;
    private ImageButton playStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        Change(this, getWindow());
        String html = MessageFormat.format("<font color='#000'>{0}</font>", "OCR Scanner");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
        getSupportActionBar().setTitle(Html.fromHtml(html));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        playStop = findViewById(R.id.playStop);
        textView = findViewById(R.id.textView);
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PackageManager.PERMISSION_GRANTED);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                PlayVoice();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void buttonStart(View view) {
        currentPhotoPath = OpenCamera(this);

    }

    public void SpeakAgain(String stringResult) {
       stringResult = textView.getText().toString();
        textToSpeech.speak(stringResult, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        textToSpeech.stop();
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            Uri uri = Uri.parse(currentPhotoPath);
            stringResult = detectTextBlocks(uri);
            textView.setText(stringResult);

        }
    }

    String detectTextBlocks(Uri uqi) {
        final String stringText;
        List<TextBlock> result = new ArrayList<>();
        Bitmap bitmap = UriToBitmap(uqi, this);
        if (bitmap == null) return "Text Not Detect";
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        if (!textRecognizer.isOperational()) {
            textRecognizer.release();
            return "Text Not Detect";
        }
        Frame imageFrame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<TextBlock> sparseArray = textRecognizer.detect(imageFrame);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < sparseArray.size(); ++i) {
            TextBlock textBlock = sparseArray.valueAt(i);
            if (textBlock != null && textBlock.getValue() != null) {
                stringBuilder.append(textBlock.getValue() + " ");
            }
        }
        stringText = stringBuilder.toString();
        return stringText;
    }
    public void setClipboard(View view) {
        Toast.makeText(this, "Text Copied", Toast.LENGTH_SHORT).show();
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", stringResult);
        clipboard.setPrimaryClip(clip);
    }
    private void PlayVoice(){
        playStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag){
                    playStop.setImageDrawable(ContextCompat.getDrawable(OcrActivity.this, R.drawable.stop_ic));
                    flag = false;
                    textToSpeech.stop();
                    SpeakAgain(stringResult);
                }
                else {
                    playStop.setImageDrawable(ContextCompat.getDrawable(OcrActivity.this, R.drawable.play_ic));
                    flag = true;
                    textToSpeech.stop();
                }
            }
        });

    }

    public void writeToFile(View view)
    {
        int random = new Random().nextInt(61) + 20;
        final File path = new File(Environment.getExternalStorageDirectory() + "/ScanCord/TextFile/");
        if(!path.exists())
        {
            path.mkdirs();
        }

        @SuppressLint("DefaultLocale") final File file = new File(path, String.format("ScanCord_OCR_Text_%d.txt", random));
        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(stringResult);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
            Toast.makeText(this, "Filed Saved in "+file.toString(), Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    @Override
    public void onBackPressed() {
        textToSpeech.stop();
        super.onBackPressed();
    }
}