package emi.diary_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ActionBarCallback implements ActionMode.Callback {

    final static int EDIT_ENTRY = 1;

    private Note note;
    private Context context;
    private TableManager tableManager;

    public ActionBarCallback(TableManager tableManager, Context context, Note note) {

        this.context = context;
        this.note = note;
        this.tableManager = tableManager;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        mode.getMenuInflater().inflate(R.menu.menu_entry_selected, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        mode.setTitle("");
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.MenuSelected_DeleteEntry) {

            /* Dialog before deleting */
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            tableManager.removeEntry(note);

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Der Eintrag wird unwiederruflich gelöscht. Fortfahren?").setPositiveButton("Ja", dialogClickListener)
                    .setNegativeButton("Nein", dialogClickListener).show();

        } else if (id == R.id.MenuSelected_ShareEntry) {


        } else if (id == R.id.MenuSelected_EditEntry) {

            Intent i = new Intent(context, EditEntryActivity.class);
            i.putExtra("note", note);
            i.putExtra("requestCode", EDIT_ENTRY);

            if (context instanceof MainActivity) {

                ((MainActivity) context).startActivityForResult(i, EDIT_ENTRY);
            }
        }


        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}
