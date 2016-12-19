package emi.diary_app;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;

public class TableManager {

    private ListView table;
    private Context context;
    private ArrayList<Note> arrayList;
    private EntryAdapter adapter;


    public TableManager(final Context context, final ListView table) throws NullPointerException {

        if (table == null) {
            throw new NullPointerException("Table darf nicht NULL sein!");
        }

        this.table = table;
        this.context = context;

        this.arrayList = new ArrayList<>();
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

        /* Adding Layout to the row */
        arrayList.add(note);
        adapter.notifyDataSetChanged();

    }
}
