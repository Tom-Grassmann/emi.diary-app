package emi.diary_app.ActionBar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import emi.diary_app.Activity.EditEntryActivity;
import emi.diary_app.Activity.MainActivity;
import emi.diary_app.Activity.ShareActivity;
import emi.diary_app.Note;
import emi.diary_app.R;
import emi.diary_app.ListManagement.TableManager;

public class ActionBarCallback_MainActivity implements ActionMode.Callback {

    final static int EDIT_ENTRY = 1;
    final static int SHARE_ENTRY = 5;


    private Note note;
    private Context context;
    private TableManager tableManager;
    private LinearLayout linLay_selectedItem;


    public ActionBarCallback_MainActivity(TableManager tableManager, Context context, Note note, LinearLayout linLay_selectedItem) {

        this.context = context;
        this.note = note;
        this.tableManager = tableManager;
        this.linLay_selectedItem = linLay_selectedItem;


    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        mode.getMenuInflater().inflate(R.menu.menu_entry_selected, menu);
        this.linLay_selectedItem.setBackgroundColor(context.getResources().getColor(R.color.entry_selected));

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

            this.linLay_selectedItem.setBackgroundColor(context.getResources().getColor(R.color.entry_not_selected));
            mode.finish();

        } else if (id == R.id.MenuSelected_ShareEntry) {

            Intent i = new Intent(context, ShareActivity.class);
            i.putExtra("note", note);

            if (context instanceof MainActivity) {

                ((MainActivity) context).startActivityForResult(i, SHARE_ENTRY);
            }

            this.linLay_selectedItem.setBackgroundColor(context.getResources().getColor(R.color.entry_not_selected));
            mode.finish();

        } else if (id == R.id.MenuSelected_EditEntry) {

            Intent i = new Intent(context, EditEntryActivity.class);
            i.putExtra("note", note);
            i.putExtra("requestCode", EDIT_ENTRY);

            if (context instanceof MainActivity) {

                ((MainActivity) context).startActivityForResult(i, EDIT_ENTRY);
            }

            this.linLay_selectedItem.setBackgroundColor(context.getResources().getColor(R.color.entry_not_selected));
            mode.finish();
        }


        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

        this.linLay_selectedItem.setBackgroundColor(context.getResources().getColor(R.color.entry_not_selected));
    }
}
