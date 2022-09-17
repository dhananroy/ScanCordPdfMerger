package com.ucmate.scancord.helper;

import static com.ucmate.scancord.db.db.CreateTable;
import static com.ucmate.scancord.db.db.InsertFile;
import static com.ucmate.scancord.db.db.InsertFolder;

import android.content.Context;
import android.widget.Toast;

public class Folder {
    public void CreateFolder(Context  context){
        if (CreateTable(context))
        {
            Toast.makeText(context, "Table Created", Toast.LENGTH_SHORT).show();
            if (InsertFolder(context, "Folder1 ", "/sdcard/emt/0/pdf"))
                {
                    Toast.makeText(context, "Inserted Table Value", Toast.LENGTH_SHORT).show();
                }
            else {
                Toast.makeText(context, "Something wrong in Insert table", Toast.LENGTH_SHORT).show();
                }
        }
        else {
            Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void CreateFile(Context  context, int dir_id, String file_name, int file_size, String file_path){
        if (CreateTable(context))
        {
            Toast.makeText(context, "Table Created", Toast.LENGTH_SHORT).show();
            if (InsertFile(context, dir_id, file_name, file_size, file_path))
            {
                Toast.makeText(context, "Inserted File Value", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "Something wrong in Insert table", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show();
        }
    }

}
