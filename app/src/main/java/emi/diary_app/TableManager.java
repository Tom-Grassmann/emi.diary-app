package emi.diary_app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;

public class TableManager {

    private ListView table;
    private Context context;
    private ArrayList<Note> arrayList;
    private EntryAdapter adapter;
    private Database database;
    private int actualID = 0;


    public TableManager(final Context context, final ListView table, Database database) throws NullPointerException {

        if (table == null) {
            throw new NullPointerException("Table darf nicht NULL sein!");
        }

        if (database == null) {
            throw new NullPointerException("Datenbank darf nicht NULL sein!");
        }

        this.table = table;
        this.context = context;

        this.database = database;
        this.arrayList = new ArrayList<>();

        Cursor data = this.database.getAllData();

        while (data.moveToNext()) {

            Note note = new Note(data.getInt(0), data.getString(1));
            note.setTimestamp(data.getLong(2));
            note.setTextNote(data.getString(3));
            note.setVoiceNote(data.getString(4));

            /* Decode Image from path */
            String picturePath = data.getString(5);
            if (picturePath == null || picturePath.length() == 0)
                note.setImageNote(null);

            Bitmap image = BitmapFactory.decodeFile(picturePath);
            note.setImageNote(image);

            if (this.actualID <= note.getID()) {
                this.actualID = note.getID();
            }

            this.arrayList.add(note);
        }

        Collections.sort(this.arrayList);

        this.adapter = new EntryAdapter(this.context, this.arrayList, this);
        this.table.setAdapter(this.adapter);

    }

    public void addEntry(Note note) throws NullPointerException {

        if (note == null) {
            throw new NullPointerException("Note darf nicht NULL sein!");
        }

        /* Update the Timestamp of the Note */
        long tsLong = System.currentTimeMillis();
        note.setTimestamp(tsLong);

        /* When new Entry -> Add to Database */
        if (!this.arrayList.contains(note)) {

            this.database.insertData(note);
        }

        /* Add to List and Adapter */
        this.arrayList.add(0, note);
        this.adapter.notifyDataSetChanged();

    }

    public void removeEntry(Note note) throws NullPointerException {

        if (note == null) {
            throw new NullPointerException("Note darf nicht NULL sein!");
        }

        if (!database.removeData(note)) {

            System.out.println("Fehler beim löschen aus Datenbank!");
        };

        this.arrayList.remove(note);
        this.adapter.notifyDataSetChanged();
    }

    public boolean updateEntry(Note note) throws NullPointerException {

        if (note == null) {
            throw new NullPointerException("Note darf nicht NULL sein!");
        }

        if (!this.arrayList.contains(note)) {
            return false;
        }

        /* Update the Timestamp of the Note */
        long tsLong = System.currentTimeMillis();
        note.setTimestamp(tsLong);

        /* Update the Entry in List */
        int posInList = this.arrayList.indexOf(note);
        this.arrayList.remove(posInList);
        this.arrayList.add(posInList, note);
        this.adapter.notifyDataSetChanged();

        /* Update Item in Database */
        if (!this.database.updateData(note)) {
            System.out.println("Fehler beim updaten des Entry!");
            return false;
        }

        return true;
    }

    public int getNextFreeID() {

        this.actualID++;
        return this.actualID;
    }
}


