package com.ucmate.scancord.helper;

import static com.ucmate.scancord.MainActivity.RefreshFolderView;
import static com.ucmate.scancord.db.db.DeleteFolder;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ucmate.scancord.MainActivity;
import com.ucmate.scancord.R;

class MenuClick implements PopupMenu.OnMenuItemClickListener {

    public int position;
    private Context context;
    public MenuClick(int positon, Context context) {
        this.position=positon;
        this.context=context;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.delete_items:
                if (DeleteFolder(context, position)){
                    Toast.makeText(context, R.string.delete_msg, Toast.LENGTH_LONG).show();
                    RefreshFolderView();
                     }
                return true;
           case R.id.favorite:
                Toast.makeText(context, "Marked as Favorite", Toast.LENGTH_LONG).show();
                return true;
           default:
               break;
        }
        return false;
    }
}