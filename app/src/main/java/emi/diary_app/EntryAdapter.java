package emi.diary_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static emi.diary_app.R.drawable.defqon1;

public class EntryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Note> noteList;
    private TableManager tableManager;

    public EntryAdapter(Context context, ArrayList<Note> noteList, TableManager tableManager) {
        this.context = context;
        this.noteList = noteList;
        this.tableManager = tableManager;
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int i) {
        return noteList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {

        final Note note = noteList.get(pos);

        /* Create Main view */
        View entry = View.inflate(context, R.layout.entry, null);

        TextView tvTitle = (TextView) entry.findViewById(R.id.tvTitle);
        TextView tvDate = (TextView) entry.findViewById(R.id.tvDate);

        /* Set Title and Date */
        tvTitle.setText(note.getTitle());
        tvDate.setText(note.getDate());


        LinearLayout entryText = (LinearLayout) entry.findViewById(R.id.entryText);
        LinearLayout entryVoice = (LinearLayout) entry.findViewById(R.id.entryVoice);
        LinearLayout entryImage = (LinearLayout) entry.findViewById(R.id.entryImage);

        /* Setting up TextEntry */
        View textEntry = View.inflate(context, R.layout.diary_entry_text, null);
        TextView textContent = (TextView) textEntry.findViewById(R.id.textContent);
        textContent.setText(note.getTextNote() + " " + Integer.toString(note.getID()));

        /* Setting up VoiceEntry */
        View voiceEntry = View.inflate(context, R.layout.diary_entry_voice, null);
        Button playVoiceContent = (Button) voiceEntry.findViewById(R.id.playVoiceContent);
        ProgressBar playerProgressBar = (ProgressBar) voiceEntry.findViewById(R.id.playerProgressBar);
        // TODO: Set up Voice Content

        /* Setting up ImageContent */
        View imageEntry = View.inflate(context, R.layout.diary_entry_picture, null);
        ImageView imageContent = (ImageView) imageEntry.findViewById(R.id.imageContent);
        if (note.getImageNote() != null) {

            imageContent.setImageBitmap(note.getImageNote());
            imageContent.setMaxHeight(350);
        }

        /* Adding LinLayouts to the Entry */
        entryText.addView(textEntry);
        entryVoice.addView(voiceEntry);
        entryImage.addView(imageEntry);


        Button btnEntryMenu = (Button) entry.findViewById(R.id.btnEntryMenu);

        btnEntryMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                PopupMenu popup = new PopupMenu(context, view);
                popup.getMenuInflater().inflate(R.menu.menu_click_on_entry, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().toString().equals("Anzeigen")) {

                            /* SHOW ENTRY */
                        }

                        if (item.getTitle().toString().equals("Löschen")) {


                            /* Dialog before deleting */
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:

                                            EntryAdapter.this.tableManager.removeEntry(note);

                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:

                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Der Eintrag wird unwiederruflich gelöscht. Fortfahren?").setPositiveButton("Ja", dialogClickListener)
                                    .setNegativeButton("Nein", dialogClickListener).show();

                        }

                        return false;
                    }
                });

                popup.show();
            }
        });


        return entry;


    }
}