package emi.diary_app;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

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

        /* Set View for a Text Note [see diary_entry_text.xml] */
        if (noteList.get(pos).getNoteType() == NoteType.TEXT) {

            View v = View.inflate(context, R.layout.diary_entry_text, null);

            TextView tvTitle = (TextView) v.findViewById(R.id.entry_text_tvTitle);
            TextView tvContent = (TextView) v.findViewById(R.id.entry_text_tvContent);
            TextView tvModifyDate = (TextView) v.findViewById(R.id.entry_text_tvModifyDate);

            tvTitle.setText(noteList.get(pos).getTitle());
            tvContent.setText(noteList.get(pos).getTextNote());
            tvModifyDate.setText(noteList.get(pos).getdate_last_edited());

            v.setTag(noteList.get(pos).getID());

            return v;

        /* Set View for a Text Note [see diary_entry_voice.xml] */
        } else if (noteList.get(pos).getNoteType() == NoteType.VOICE) {

            View v = View.inflate(context, R.layout.diary_entry_voice, null);

            TextView tvTitle = (TextView) v.findViewById(R.id.entry_voice_tvTitle);
            ImageView btnPlay = (ImageView) v.findViewById(R.id.entry_voice_btnPlay);
            TextView tvModifyDate = (TextView) v.findViewById(R.id.entry_voice_tvModifyDate);
            ProgressBar progBar = (ProgressBar) v.findViewById(R.id.entry_voice_progBar);

            tvTitle.setText(noteList.get(pos).getTitle());
            tvModifyDate.setText(noteList.get(pos).getdate_last_edited());

            /* Setting up Play Button */
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // TODO: Play VoiceEntry

                }
            });

            v.setTag(noteList.get(pos).getID());

            return v;

        } else if (noteList.get(pos).getNoteType() == NoteType.PICTURE) {

            // TODO: Add Picture View

            return null;

        } else {

            return null;
        }
    }
}
