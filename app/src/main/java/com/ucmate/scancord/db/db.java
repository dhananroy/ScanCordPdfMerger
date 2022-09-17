package com.ucmate.scancord.db;

import static android.content.Context.MODE_PRIVATE;

import static com.ucmate.scancord.helper.DeleteFiles.DeleteTempFolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;

public class db {
    final static String dbname = "ScDB";
    public static boolean CreateTable(Context context){
        try {
            String folder_sql = "CREATE TABLE IF NOT EXISTS Folder(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, dir_name VARCHAR(92), dir_path VARCHAR(92))";
            String file_sql = "CREATE TABLE IF NOT EXISTS FileList(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, dir_id INTEGER NOT NULL, file_name VARCHAR(92), file_size INTEGER, file_path VARCHAR(92))";
            String image_queue_sql = "CREATE TABLE IF NOT EXISTS ImageQueue(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, file_name VARCHAR(92), file_path VARCHAR(92))";
            SQLiteDatabase myDB= null;
            myDB = context.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
            myDB.execSQL(folder_sql);
            myDB.execSQL(file_sql);
            myDB.execSQL(image_queue_sql);
            myDB.close();
            return true;

        }
        catch (Exception e){
            return false;
        }

    }
    public static boolean InsertFolder( Context context, String name, String path){
        CreateTable(context);
        if (name.length()<2){
            return false;
        }
        SQLiteDatabase myDB= null;
        myDB = context.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
        try {
            myDB.execSQL(String.format("INSERT INTO Folder(dir_name, dir_path) VALUES ('%s','%s');", name, path));
            myDB.close();
            return true;
        }
        catch (Exception e){
            myDB.close();
            return false;
        }
    }
    
    public static boolean InsertFile(Context context, int dir_id, String file_name, int file_size, String file_path){
        CreateTable(context);
        SQLiteDatabase myDB= null;
        myDB = context.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
        try {
            myDB.execSQL(String.format("INSERT INTO FileList(dir_id, file_name, file_size, file_path) VALUES ('%d','%s','%d','%s');", dir_id, file_name, file_size, file_path));
            myDB.close();
            return true;
        }
        catch (Exception e){
            return false;
        }

    }

    public static boolean DeleteFolder(Context context, int id){
        SQLiteDatabase myDB= null;
        myDB = context.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
        try {
            myDB.execSQL(String.format("DELETE FROM Folder WHERE id = '%d'", id));
            myDB.close();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }


    @SuppressLint("Range")
    public static Object[] FetchFolder(Context context){
        ArrayList<Integer> folder_id =new ArrayList<Integer>();
        ArrayList<String> folder_name =new ArrayList<String>();
        Object[] arrayObjects = new Object[2];
        try {
            SQLiteDatabase myDB= null;
            myDB = context.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
            Cursor c = myDB.rawQuery("SELECT * FROM Folder" , null);
            c.moveToFirst();
            do
            {
                folder_id.add(Integer.valueOf(c.getString(c.getColumnIndex("id"))));
                folder_name.add(c.getString(c.getColumnIndex("dir_name")));
            } while(c.moveToNext());
            c.close();
            myDB.close();
            folder_id.add(-1);
            folder_name.add("Create Folder");
            arrayObjects [0] = folder_id;
            arrayObjects [1] = folder_name;
            return arrayObjects;
        }
        catch (Exception e){
            folder_id.add(-1);
            folder_name.add("Create Folder");
            arrayObjects [0] = folder_id;
            arrayObjects [1] = folder_name;
            return arrayObjects;
        }

    }


    @SuppressLint("Range")
    public static Object[] FetchFile(Context context, int id){
        ArrayList<Integer> file_id =new ArrayList<Integer>();
        ArrayList<String> file_name =new ArrayList<String>();
        ArrayList<String> file_path =new ArrayList<String>();
        ArrayList<Integer> file_size =new ArrayList<Integer>();
        Object[] arrayObjects = new Object[4];
        try {
            SQLiteDatabase myDB= null;
            myDB = context.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
            Cursor c;
            if (id == -1){
                c = myDB.rawQuery("SELECT * FROM FileList ORDER BY id DESC LIMIT 6" , null);

            }
            else {
                c = myDB.rawQuery(String.format("SELECT * FROM FileList where dir_id = %d  ORDER BY id DESC", id) , null);
            }
            c.moveToFirst();
            do
            {
                file_id.add(Integer.valueOf(c.getString(c.getColumnIndex("id"))));
                file_size.add(Integer.valueOf(c.getString(c.getColumnIndex("file_size"))));
                file_name.add(c.getString(c.getColumnIndex("file_name")));
                file_path.add(c.getString(c.getColumnIndex("file_path")));

            } while(c.moveToNext());
            c.close();
            myDB.close();
            arrayObjects [0] = file_id;
            arrayObjects [1] = file_name;
            arrayObjects [2] = file_size;
            arrayObjects [3] = file_path;
            return arrayObjects;
        }
        catch (Exception e){
            file_id.add(-1);
            file_name.add("No File");
            file_size.add(0);
            file_path.add(null);
            arrayObjects [0] = file_id;
            arrayObjects [1] = file_name;
            arrayObjects [2] = file_size;
            arrayObjects [3] = file_path;
            return arrayObjects;
        }

    }
    public static boolean DeleteFile(Context context, int id){
        SQLiteDatabase myDB= null;
        myDB = context.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
        try {
            myDB.execSQL(String.format("DELETE FROM FileList WHERE id = '%d'", id));
            myDB.close();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }


    public static boolean CheckIsDataAlreadyInDBorNot(String TableName, String dbfield, String fieldValue,Context context) {
        SQLiteDatabase myDB= null;
        myDB = context.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
        String Query = "Select * from " + TableName + " where " + dbfield + " = '"+fieldValue+"'";
        Cursor cursor = myDB.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        myDB.close();
        return true;
    }
    public static boolean InsertTempFile(Context context, String file_name, String file_path){
        CreateTable(context);
        SQLiteDatabase myDB= null;
        myDB = context.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
        try {
            myDB.execSQL(String.format("INSERT INTO ImageQueue(file_name, file_path) VALUES ('%s','%s');", file_name, file_path));
            myDB.close();
            return true;
        }
        catch (Exception e){
            return false;
        }

    }
    @SuppressLint("Range")
    public static Object[] FetchImage(Context context){
        ArrayList<String> file_name =new ArrayList<String>();
        ArrayList<String> file_path =new ArrayList<String>();
        Object[] arrayObjects = new Object[2];
        try {
            SQLiteDatabase myDB= null;
            myDB = context.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
            Cursor c = myDB.rawQuery(String.format("SELECT * FROM ImageQueue") , null);
            c.moveToFirst();
            do
            {
                file_name.add(c.getString(c.getColumnIndex("file_name")));
                file_path.add(c.getString(c.getColumnIndex("file_path")));
            } while(c.moveToNext());
            c.close();
            myDB.close();
            arrayObjects [0] = file_name;
            arrayObjects [1] = file_path;
            return arrayObjects;
        }
        catch (Exception e){
            arrayObjects [0] = file_name;
            arrayObjects [1] = file_path;
            return arrayObjects;
        }

    }
    public static boolean DeleteImageQueue(Context context){
        SQLiteDatabase myDB= null;
        myDB = context.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
        try {
            myDB.execSQL("DELETE FROM ImageQueue");
            myDB.close();
            DeleteTempFolder();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
