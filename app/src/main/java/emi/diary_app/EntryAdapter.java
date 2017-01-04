package emi.diary_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.text.AndroidCharacter;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.Handler;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class EntryAdapter extends BaseAdapter implements Serializable{

    private Context context;
    private ArrayList<Note> noteList;
    private TableManager tableManager;

    private Handler seekHandler = new Handler();
    private SeekBar playerSeekBar = null;
    private MediaPlayer mediaPlayer = null;

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


    private Runnable run = new Runnable() {
        @Override
        public void run() {

            seekUpdation();
        }
    };

    private void seekUpdation() {

        playerSeekBar.setProgress(mediaPlayer.getCurrentPosition());
        seekHandler.postDelayed(run, 1000);
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
        textContent.setText(note.getTextNote());

        /* Setting up VoiceEntry */
        View voiceEntry = null;
        if (!note.getVoiceNote().equals("")) {

            voiceEntry = View.inflate(context, R.layout.diary_entry_voice, null);

            /* - - - Set up PlayButton - - - - - - - - - - - - - - - - - - - - - - - */
            final PlayButton playVoiceContent = new PlayButton(context);
            playVoiceContent.setBackground(context.getResources().getDrawable(R.drawable.ic_media_play));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
            params.setMargins(5, 0, 0, 0);

            playVoiceContent.setLayoutParams(params);

            /* Add Button to Linear Layout */
            LinearLayout linearLayout = (LinearLayout) voiceEntry.findViewById(R.id.voiceEntryLinLay);
            linearLayout.addView(playVoiceContent, 0);
            /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */


            /* - - - Set up SeekBar - - - - - - - - - - - - - - - - - - - - - - - -  */
            playerSeekBar = (SeekBar) voiceEntry.findViewById(R.id.playerProgressBar);

            /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */



            /* - - - Set up PlayButton and MediaPlayer - - - - -  - - - - - - - - -  */

            /* Check if Audio is playing
             * and Set the right Symbol and State
             * of the Button */
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {

                    playVoiceContent.IS_PLAYING = true;
                    playVoiceContent.setBackground(context.getResources().getDrawable(R.drawable.ic_media_pause));
                }
            }
            playVoiceContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /* If Audio is playing */
                    if (playVoiceContent.IS_PLAYING) {

                        /* Set up "Pause" Icon */
                        playVoiceContent.setBackground(context.getResources().getDrawable(R.drawable.ic_media_play));

                        /* Pause Audio */
                        mediaPlayer.pause();

                        playVoiceContent.IS_PLAYING = false;

                    /* If Audio is not playing */
                    } else {

                        /* Set up "Play" Icon */
                        playVoiceContent.setBackground(context.getResources().getDrawable(R.drawable.ic_media_pause));



                        /* - - - Preparing MediaPlayer - - - - - - - - - - - - - - - - - - - - - */
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(note.getVoiceNote());
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        /* Set OnCompletitionListener to MediaPlayer */
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {

                                /* Re-Preparing MediaPlayer */
                                try {
                                    mediaPlayer.setDataSource(note.getVoiceNote());
                                    mediaPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        /* Start Audio */
                        mediaPlayer.start();

                        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */


                        playVoiceContent.IS_PLAYING = true;
                    }
                }
            });
            /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */


        }


        /* Setting up ImageContent */
        View imageEntry = View.inflate(context, R.layout.diary_entry_picture, null);
        ImageView imageContent = (ImageView) imageEntry.findViewById(R.id.imageContent);
        if (!note.getImageNote().equals("")) {

            /* Decode Image from path */
            Bitmap image = BitmapFactory.decodeFile(note.getImageNote());

            imageContent.setImageBitmap(image);
            imageContent.setMaxHeight(350);
        }

        /* Adding LinLayouts to the Entry */
        entryText.addView(textEntry);
        if (voiceEntry != null) {

            entryVoice.addView(voiceEntry);
        }
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

                            Intent i = new Intent(context, EditEntryActivity.class);
                            i.putExtra("note", note);

                            context.startActivity(i);

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