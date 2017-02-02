package emi.diary_app.ActionBar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import emi.diary_app.Activity.EditEntryActivity;
import emi.diary_app.Activity.MainActivity;
import emi.diary_app.Activity.ShareActivity;
import emi.diary_app.ListManagement.EntryAdapter;
import emi.diary_app.Note;
import emi.diary_app.R;
import emi.diary_app.ListManagement.TableManager;
import emi.diary_app.Thread.WaitThread;

public class ActionBarCallback_MainActivity implements ActionMode.Callback {

    final static int EDIT_ENTRY = 1;
    final static int SHARE_ENTRY = 5;

    private WaitThread waitThread;

    private Note note;
    private Context context;
    private TableManager tableManager;
    private LinearLayout linLay_selectedItem;
    private EntryAdapter entryAdapter;
    private TextView textContent;


    public ActionBarCallback_MainActivity(TextView textContent, EntryAdapter entryAdapter, TableManager tableManager, Context context, Note note, LinearLayout linLay_selectedItem) {

        this.context = context;
        this.note = note;
        this.note.setSelected(true);

        this.tableManager = tableManager;
        this.linLay_selectedItem = linLay_selectedItem;
        this.entryAdapter = entryAdapter;
        this.textContent = textContent;

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        mode.getMenuInflater().inflate(R.menu.menu_entry_selected, menu);
        this.linLay_selectedItem.setBackgroundColor(context.getResources().getColor(R.color.entry_selected));
        
        /* Animate TextContent Expand */
        if (!note.getTextNote().equals("")) {

            expand(textContent, textContent.getLineHeight() * textContent.getLineCount());
        }


        entryAdapter.setActionBar(mode);

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

        } else if (id == R.id.test) {

            /* Animate TextContent Collapse */
            if (!note.getTextNote().equals("")) {

                collapse(textContent, textContent.getLineHeight() * textContent.getLineCount());
            }
        }



        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {


        this.note.setSelected(false);
        this.linLay_selectedItem.setBackgroundColor(context.getResources().getColor(R.color.entry_not_selected));

        /* Animate TextContent Collapse */
        if (!note.getTextNote().equals("")) {

            collapse(textContent, textContent.getLineHeight() * textContent.getLineCount());
        }

        entryAdapter.setActionBar(null);



        /*Handler waitHandler = new Handler() {

            public void handleMessage(Message message) {

                tableManager.notifyAdapter();
                waitThread.interrupt();
            }
        };

        waitThread = new WaitThread(2, waitHandler);
        //waitThread.run();*/



    }

    private ValueAnimator slideAnimator(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = textContent.getLayoutParams();
                layoutParams.height = value;
                textContent.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void expand(final View v, final int endHeight) {
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        v.getLayoutParams().height = textContent.getHeight();

        final int targetHeight = endHeight;

        ValueAnimator mAnimator = slideAnimator(textContent.getHeight(), targetHeight);
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v.getLayoutParams().height = targetHeight;
                v.requestLayout();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mAnimator.start();
    }

    private void collapse(final View v, final int startHeight) {
        int finalHeight = v.getHeight() + 300 - startHeight;

        System.out.println(startHeight);

        ValueAnimator mAnimator = slideAnimator(v.getHeight(), finalHeight);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }
}
