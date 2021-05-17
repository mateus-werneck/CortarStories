package com.example.Picotador;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import static com.example.Picotador.Constant.allMediaList;
import static com.example.Picotador.RecyclerFolderAdapter.allMedia;

public class DialogCreate extends DialogFragment {

    private String title;
    private String message;
    private int icon;
    private RecyclerFolderAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;


    public DialogCreate(String title, String message, int icon, @Nullable RecyclerFolderAdapter recyclerViewAdapter, @Nullable RecyclerView recyclerView) {
        this.title = title;
        this.message = message;
        this.icon = icon;
        this.recyclerViewAdapter = recyclerViewAdapter;
        this.recyclerView = recyclerView;
    }


    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder Dialog = new AlertDialog.Builder(getActivity());
        Dialog.setTitle(title);
        Dialog.setMessage(message);
        Dialog.setIcon(icon);
        Dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (title.equals("Aguarde")) {
                Toast.makeText(getContext(), "Processando...", Toast.LENGTH_SHORT).show();
                } else if (title.equals("Pronto")) {Toast.makeText(getContext(), "Pronto", Toast.LENGTH_SHORT).show();
                        File directory = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/CortarStories");
                        allMediaList.clear();
                        allMedia.clear();
                        ListFolder.load_Directory_Files(directory);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        SystemClock.sleep(1000);

                        recyclerViewAdapter.notifyDataSetChanged();






                }
            }
        });
    return Dialog.show();
    }
}




