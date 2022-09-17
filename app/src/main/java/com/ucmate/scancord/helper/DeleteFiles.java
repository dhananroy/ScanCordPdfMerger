package com.ucmate.scancord.helper;

import android.os.Environment;

import java.io.File;

public class DeleteFiles {
    public static void DeleteTempFolder(){
        try {
            File dir = new File(Environment.getExternalStorageDirectory() + "/ScanCord/tmp_file");
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
            }
        }
        catch (Exception ignore){}
    }

    public static void DeleteSingleFile(String path){
        File dir = new File(path);
        if (dir.exists())
        {
         dir.delete();
        }
    }
}
