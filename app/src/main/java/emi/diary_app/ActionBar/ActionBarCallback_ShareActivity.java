package emi.diary_app.ActionBar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import emi.diary_app.Activity.EditEntryActivity;
import emi.diary_app.Activity.ShareActivity;
import emi.diary_app.Note;
import emi.diary_app.PlayButton;
import emi.diary_app.PlayState;
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


                Bitmap icon = note.getBitmap();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
                context.startActivity(Intent.createChooser(share, "Share Image"));

            } else if (linLay_selectedItem.getId() == R.id.LinLayAudioPlayer) {

                File fa = new File(note.getVoiceNote());
                Uri audioUri = FileProvider.getUriForFile(context, "emi.diary_app.fileProvider", fa);

                Intent shareIntenta = new Intent();
                shareIntenta.setAction(Intent.ACTION_SEND);
                shareIntenta.setType("audio/*");
                shareIntenta.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntenta.setDataAndType(audioUri, context.getContentResolver().getType(audioUri));
                shareIntenta.putExtra(Intent.EXTRA_STREAM, audioUri);
                context.startActivity(Intent.createChooser(shareIntenta, "Audio senden mit"));

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
