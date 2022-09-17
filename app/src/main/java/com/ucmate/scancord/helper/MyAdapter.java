package com.ucmate.scancord.helper;

import static com.ucmate.scancord.MainActivity.RefreshFolderView;
import static com.ucmate.scancord.db.db.InsertFolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ucmate.scancord.R;
import com.ucmate.scancord.folder.FolderView;

public class MyAdapter extends RecyclerView.Adapter <MyAdapter.ViewHolder>{
    private ArrayList<String>folderName;
    private ArrayList<Integer> folderId;
    private Context context;
    public MyAdapter(Context context, ArrayList<String> folderName, ArrayList<Integer> folderId) {
        super();
        this.context = context;
        this.folderName = folderName;
        this.folderId = folderId;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.folders, viewGroup, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.textView.setText(folderName.get(i));
        if (folderId.get(i)==-1){
            viewHolder.topLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.round_corner));
            viewHolder.imgThumbnail.setBackgroundResource(R.drawable.create_folder);
           viewHolder.textView.setTextColor(ContextCompat.getColor(context, R.color.secondary_black));
        }
        else {
            viewHolder.imgThumbnail.setBackgroundResource(R.drawable.folder);
        }
        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                switch (view.getId()) {
                    case R.id.options:
                        showPopupMenu(viewHolder.options, folderId.get(position));
                        break;
                    default:
                        if (isLongClick) {
                            Toast.makeText(context, "Long Click#" + position + " - " + folderName.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                        } else {
                            if (folderId.get(position) != -1) {
                                Intent myIntent = new Intent(context, FolderView.class);
                                myIntent.putExtra("id", folderId.get(position)); //Optional parameters
                                myIntent.putExtra("name", folderName.get(position));
                                context.startActivity(myIntent);
                            }
                        }
                        break;
                }



                if (folderId.get(position)==-1 && !isLongClick){
                    CreateFolderDialog(context);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return folderName.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        TextView textView;
        RelativeLayout topLayout;
        ImageView imgThumbnail;
        ImageView options;
        private ItemClickListener clickListener;
        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            topLayout = itemView.findViewById(R.id.topLayout);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            options = itemView.findViewById(R.id.options);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            options.setOnClickListener(this);
            options.setOnLongClickListener(this);
        }
        void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }
        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }
    public void CreateFolderDialog(Context context){
        EditText folderInput = new EditText(context);
        folderInput.setHint("Folder Name");
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(60, 10, 10, 15);
        folderInput.setLayoutParams(lp);
        folderInput.setGravity(Gravity.CENTER|android.view.Gravity.LEFT);
        folderInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        folderInput.setLines(1);
        folderInput.setMaxLines(1);
        container.addView(folderInput, lp);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Create Folder")
                .setView(container)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String folder_name = folderInput.getText().toString();
                        dialogInterface.dismiss();
                        if(InsertFolder(context, folder_name,"internal")){
                            Toast.makeText(context, context.getString(R.string.successfully_created), Toast.LENGTH_SHORT).show();
                            RefreshFolderView();
                        }
                        else {
                            Toast.makeText(context, context.getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
    private void showPopupMenu(View view,int position) {
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.options, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuClick(position, context));
        popup.show();
    }


}
