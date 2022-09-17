package com.ucmate.scancord.folder;

import static com.ucmate.scancord.db.db.FetchFolder;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ucmate.scancord.MainActivity;
import com.ucmate.scancord.R;
import com.ucmate.scancord.helper.MyAdapter;

import java.util.ArrayList;

public class FolderList {
    static RecyclerView recyclerView;
    static RecyclerView.LayoutManager layoutManager;
    static RecyclerView.Adapter adapter;
    static ArrayList<String> folderName;
    static ArrayList<Integer> folderId;
    public static void GenerateView(Context context, View view){
        Object[] arrayObjects = FetchFolder(context);
        folderName = (ArrayList<String>) arrayObjects[1];
        folderId = (ArrayList<Integer>) arrayObjects[0];
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(context, folderName, folderId);
        recyclerView.setAdapter(adapter);
    }
}
