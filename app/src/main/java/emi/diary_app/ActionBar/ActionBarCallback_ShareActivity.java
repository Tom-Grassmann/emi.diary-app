package emi.diary_app.ActionBar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;

import emi.diary_app.Activity.ShareActivity;
import emi.diary_app.Note;
import emi.diary_app.R;


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

            /* Share TextContent */
            if (linLay_selectedItem.getId() == R.id.LinLayText) {

                String toSend = note.getTitle() + "\n\n" + note.getTextNote() + "\n\n" + note.getDate_Location();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, toSend);
                sendIntent.setType("text/plain");
                ((ShareActivity) context).startActivityForResult(sendIntent, SHARE_TEXT);

            /* Share Image */
            } else if (linLay_selectedItem.getId() == R.id.LinLayPicture) {

                try {

                    // TODO: Noch keine ShareApp auswählbar
                    File image = new File(note.getImageNote());
                    Uri uri = Uri.fromFile(image);

                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.putExtra(Intent.EXTRA_STREAM, uri);

                    ((ShareActivity) context).startActivityForResult(Intent.createChooser(share, "Bild freigeben"), SHARE_PICTURE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

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
