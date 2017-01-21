package emi.diary_app;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ActionBarCallback implements ActionMode.Callback {

    private Note note;

    public ActionBarCallback(Note note) {

        this.note = note;
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


            System.out.println("Programmer Skills! :D");
        }


        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}
