package emi.diary_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;


public class ActionBarCallback_ShareActivity implements ActionMode.Callback {

    final static int SHARE_TEXT = 6;
    final static int SHARE_PICTURE = 7;
    final static int SHARE_AUDIO = 8;

    private Note note;
    private Context context;
    private LinearLayout linLay_selectedItem;


    public ActionBarCallback_ShareActivity(Context context, Note note, LinearLayout linLay_selectedItem) {

        this.context = context;
        this.note = note;
        this.linLay_selectedItem = linLay_selectedItem;


    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        mode.getMenuInflater().inflate(R.menu.menu_entry_share, menu);
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

        if (item.getItemId() == R.id.MenuShare_Share) {

            if (linLay_selectedItem.getId() == R.id.LinLayText) {


            } else if (linLay_selectedItem.getId() == R.id.LinLayPicture) {



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
