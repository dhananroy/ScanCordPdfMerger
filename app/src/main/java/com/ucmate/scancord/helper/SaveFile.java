package com.ucmate.scancord.helper;

import static com.ucmate.scancord.scan.ImageScanner.OpenCamera;
import static com.ucmate.scancord.scan.ImageScanner.openImagesDocument;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class SaveFile {
    public static String CreateDirectoryAndSaveFile(Bitmap imageToSave, String dir_name, String fileName) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/ScanCord/"+dir_name);

        if (!direct.exists()) {
            @SuppressLint("SdCardPath") File wallpaperDirectory = new File("/sdcard/ScanCord/"+dir_name);
            wallpaperDirectory.mkdirs();
        }

        @SuppressLint("SdCardPath") File file = new File("/sdcard/ScanCord/"+dir_name, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.toString();
    }
    @SuppressLint("DefaultLocale")
    public static String randomName() {
        int random = new Random().nextInt(61) + 20;
        return String.format("Scan_Image_%d.jpg", random);
    }
    @SuppressLint("DefaultLocale")
    public static String randomPdfName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return String.format("ScanCord_%s.pdf",currentDateandTime);
    }

    public static String  createPDFWithMultipleImage(Bitmap[] bitmaps, String pdf_name,  int quality, float width){
        String directoryPath = Environment.getExternalStorageDirectory() + "/ScanCord/PdfFiles/";
        File checkFile = new File(directoryPath);
        if (!checkFile.exists()) {
            checkFile.mkdirs();
        }
        File file = new File(directoryPath,pdf_name);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PdfDocument pdfDocument = new PdfDocument();
            for (int i = 0; i < bitmaps.length; i++){
                Bitmap original = bitmaps[i];

                original = compressPdf(original,quality);
                int nh = (int) ( original.getHeight() * (width / original.getWidth()) );
                Bitmap bitmap = Bitmap.createScaledBitmap(original, (int) width, nh, true);
                bitmap = compressPdf(bitmap,quality);
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), (i + 1)).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas canvas = page.getCanvas();
                Paint paint = new Paint();
                paint.setColor(Color.BLUE);
                canvas.drawPaint(paint);
                canvas.drawBitmap(bitmap, 0f, 0f, null);
                pdfDocument.finishPage(page);
                bitmap.recycle();
            }
            pdfDocument.writeTo(fileOutputStream);
            pdfDocument.close();
            return file.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return file.toString();
        }
    }
    public static Bitmap compressPdf(Bitmap bitmap_input, int quality){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap_input.compress(Bitmap.CompressFormat.JPEG, quality, out);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

    }
}
