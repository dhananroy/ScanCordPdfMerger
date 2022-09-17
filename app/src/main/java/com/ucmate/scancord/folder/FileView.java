package com.ucmate.scancord.folder;

import static com.ucmate.scancord.MainActivity.RecentItems;
import static com.ucmate.scancord.db.db.DeleteFile;
import static com.ucmate.scancord.db.db.FetchFile;
import static com.ucmate.scancord.folder.FolderView.RefreshFileView;
import static com.ucmate.scancord.helper.DeleteFiles.DeleteSingleFile;
import static com.ucmate.scancord.helper.OpenPdfFile.OpenPdf;
import static com.ucmate.scancord.scan.ImageScanner.OpenCamera;
import static com.ucmate.scancord.scan.ImageScanner.openImagesDocument;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.ucmate.scancord.BuildConfig;
import com.ucmate.scancord.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FileView {
    public static void GenerateFileView(Context context, View view, int value, Activity activity){
        Object[] arrayObjects = FetchFile(context, value);
        ArrayList<Integer> fileId = (ArrayList<Integer>) arrayObjects[0];
        ArrayList<String> fileName= (ArrayList<String>) arrayObjects[1];
        ArrayList<Integer> fileSize = (ArrayList<Integer>) arrayObjects[2];
        ArrayList<String> filePath= (ArrayList<String>) arrayObjects[3];


        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < fileId.size() ; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("id", String.valueOf(fileId.get(i)));
            hm.put("path", filePath.get(i));
            hm.put("listview_title", fileName.get(i));
            hm.put("listview_description", GetSize(fileSize.get(i)) );
            if (fileId.get(i) == -1){
                hm.put("listview_image", Integer.toString(R.drawable.ic_no_file));
            }
            else {
                hm.put("listview_image", Integer.toString(R.drawable.pdf_file));
            }
            aList.add(hm);

        ListView listView = (ListView) view.findViewById(R.id.listview);
        String[] from = {"listview_image", "listview_title", "listview_description"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};
        SimpleAdapter simpleAdapter = new SimpleAdapter(context, aList, R.layout.filelist, from, to);

            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int file_id = Integer.parseInt(Objects.requireNonNull(aList.get(position).get("id")));
                    String file_path = aList.get(position).get("path");
                    if (file_path == null) {
                        return;
                    }
                    OpenPdf(activity, file_path);
                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {
                    int file_id = Integer.parseInt(Objects.requireNonNull(aList.get(pos).get("id")));
                    String file_path = aList.get(pos).get("path");
                    if (file_id != -1)
                        ChooseOptions(file_id, file_path, activity);
                    return true;
                }
            });

        }
    }
    private static void DeleteItems(int id,String path, Activity context){
        AlertDialog.Builder alert = new AlertDialog.Builder(
                context);
        alert.setIcon(R.drawable.ic_delete);
        alert.setTitle("Alert!!");
        alert.setMessage("Are you sure you want to delete this item?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (DeleteFile(context, id))
                {
                    try {
                        DeleteSingleFile(path);
                    }
                    catch (Exception ignore){}

                    Toast.makeText(context, R.string.delete_msg, Toast.LENGTH_LONG).show();
                    RefreshFileView();
                    RecentItems();
                }
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alert.show();

    }

    public static String GetSize(int size){
        String hrSize = "";
        double m = size/1024.0;
        DecimalFormat dec = new DecimalFormat("0.00");

        if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else {
            hrSize = dec.format(size).concat(" KB");
        }
        return hrSize;
    }
    public static void ChooseOptions(int id,String path, Activity context) {
        String[] colors = {
                "Share",
                "Delete"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID.concat(".provider"), new File(path));
                        sharingIntent.setType("application/pdf");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        context.startActivity(Intent.createChooser(sharingIntent, "Share File"));
                        break;
                    case 1:
                        DeleteItems(id, path, context);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }
}
