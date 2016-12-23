package emi.diary_app;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import static emi.diary_app.R.drawable.defqon1;

public class EntryAdapter extends BaseAdapter {

    private Context context;
    private List<Note> noteList;

    public EntryAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
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

        Note note = noteList.get(pos);

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
        View voiceEntry = View.inflate(context, R.layout.diary_entry_voice, null);
        ImageView voiceContent = (ImageView) voiceEntry.findViewById(R.id.playVoiceContent);
        ProgressBar playerProgressBar = (ProgressBar) voiceEntry.findViewById(R.id.playerProgressBar);
        // TODO: Set up Voice Content

        /* Setting up ImageContent */
        View imageEntry = View.inflate(context, R.layout.diary_entry_picture, null);
        ImageView imageContent = (ImageView) imageEntry.findViewById(R.id.imageContent);
        imageContent.setBackgroundResource(R.drawable.android_logo);
        // TODO: Set up Image Content


        entryText.addView(textEntry);
        entryVoice.addView(voiceEntry);
        entryImage.addView(imageEntry);

        return entry;


    }
}