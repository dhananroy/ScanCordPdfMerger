package com.ucmate.pdfmerger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MergePdf {

    public static Bitmap[] PdfToBitmap(String file1) {
        File pdfFile = new File(file1);
        try {
            Bitmap[] bitmaps;

            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
            final int pageCount = renderer.getPageCount();
            bitmaps = new Bitmap[pageCount];
            for(int pageNum=0; pageNum < pageCount; pageNum++){
                PdfRenderer.Page page = renderer.openPage(pageNum);
                int width = (int) (page.getWidth());
                int height = (int) (page.getHeight());
                Bitmap bitmapData = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmapData);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(bitmapData, 0, 0, null);
                page.render(bitmapData, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
                bitmaps[pageNum] = bitmapData;
            }
           renderer.close();
            return bitmaps;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static void MergePDF(String file1, String file2, String outPutName, String outputPath){
        List<Bitmap> bitmaps1 = new ArrayList<>();
        List<Bitmap> bitmaps2 = new ArrayList<>();

        bitmaps1 = Arrays.asList(Objects.requireNonNull(PdfToBitmap(file1)));
        bitmaps2 = Arrays.asList(Objects.requireNonNull(PdfToBitmap(file2)));
        Set<Bitmap> finalBitmap = new LinkedHashSet<>(bitmaps1);
        finalBitmap.addAll(bitmaps2);
        Bitmap[] bitmaps = new Bitmap[finalBitmap.size()];

        int k = 0;
        for (Bitmap i: finalBitmap) {
            bitmaps[k++] = i;
        }
        CreatePDF(bitmaps, outputPath, outPutName,90, 590.0F);
    }

    public static String  CreatePDF(Bitmap[] bitmaps, String pdf_name, String directoryPath,  int quality, float width){
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
                paint.setColor(Color.WHITE);
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
