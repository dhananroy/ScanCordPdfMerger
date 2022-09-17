package com.ucmate.scancord.folder;


import static com.ucmate.scancord.folder.FileView.GenerateFileView;
import static com.ucmate.scancord.helper.UiChange.Change;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.ucmate.scancord.R;

import java.text.MessageFormat;
import java.util.Objects;


public class FolderView extends AppCompatActivity {
    static Activity activity;
    static View appView;
    String folder_name;
    static int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_view);
//        Objects.requireNonNull(this.getSupportActionBar()).hide();
        activity = this;
        appView = getWindow().getDecorView();
        Change(this, getWindow());
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        folder_name = intent.getStringExtra("name");
        String html = MessageFormat.format("<font color='#000'>{0}</font>", folder_name);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
        getSupportActionBar().setTitle(Html.fromHtml(html));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
//        upArrow.setColorFilter(getResources().getColor(R.color.secondary_black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        RefreshFileView();

    }
    public static void RefreshFileView(){
        GenerateFileView(activity.getBaseContext(), appView,id, activity);
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}