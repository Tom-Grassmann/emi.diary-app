package emi.diary_app;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Comparator;

public class TableManager {

    private ListView table;
    private Context context;
    private ArrayList<Note> arrayList;
    private EntryAdapter adapter;
    private Database database;


    public TableManager(final Context context, final ListView table, Database database) throws NullPointerException {

        if (table == null) {
            throw new NullPointerException("Table darf nicht NULL sein!");
        }

        this.table = table;
        this.context = context;

        this.database = database;
        this.arrayList = new ArrayList<>();

        Cursor data = this.database.getAllData();

        while (data.moveToNext()) {

            String x = data.getString(3);

            Note note = new Note(data.getInt(0), data.getString(2), data.getString(1));
            note.setTextNote(data.getString(3));
            note.setVoiceNote(data.getString(4));
            note.setImageNote(data.getString(5));

            this.arrayList.add(note);
        }

        this.adapter = new EntryAdapter(this.context, this.arrayList);
        this.table.setAdapter(this.adapter);


        this.table.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {

                PopupMenu popup = new PopupMenu(TableManager.this.context, view);
                popup.getMenuInflater().inflate(R.menu.menu_click_on_entry, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().toString().equals("showEntry")) {

                            /* SHOW ENTRY */
                        }

                        if (item.getTitle().toString().equals("editEntry")) {

                            /* EDIT ENTRY */
                        }

                        return true;
                    }
                });

                popup.show();

                return false;
            }
        });

    }

    public void addEntry(Note note) throws NullPointerException {

        if (note == null) {
            throw new NullPointerException("Note darf nicht NULL sein!");
        }

        this.arrayList.add(note);
        this.adapter.notifyDataSetChanged();

    }

    public void removeEntry(Note note) throws NullPointerException {

        if (note == null) {
            throw new NullPointerException("Note darf nicht NULL sein!");
        }

        database.removeData(note);

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

        return this.arrayList.size() + 1;
    }
}


