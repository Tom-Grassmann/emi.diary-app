package emi.diary_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ShareActivity extends AppCompatActivity {

    private Note note;

    private LinearLayout
            LinLayText,
            LinLayPicture,
            LinLayAudioPlayer;

    private TextView
            tvTitle,
            tvTextContent;

    private ImageView
            imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_share);

        this.note = (Note) getIntent().getSerializableExtra("note");

        InitViews();
        InitializeActivity();
    }

    private void InitializeActivity() {

        /* Set Title */
        tvTitle.setText(note.getTitle());

        /* Set TextContent and enable RadButton */
        if (!note.getTextNote().equals("")) {

            tvTextContent.setText(note.getTextNote());
        }

        /* Load Image when existing and enable RadButton*/
        if (!note.getImageNote().equals("")) {

            /* Set up ImageView */
            Bitmap image = BitmapFactory.decodeFile(note.getImageNote());
            imageView.setImageBitmap(image);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300, 1);
            imageView.setLayoutParams(params);
            imageView.setVisibility(View.VISIBLE);

            /* Set up DeleteButton for Picture */

        }


        /* Load Audio when existing */
        /*if (!note.getVoiceNote().equals("")) {

            createAudioPlayer();
        }*/



    }

    private void InitViews() {

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTextContent = (TextView) findViewById(R.id.tvTextContent);

        LinLayText = (LinearLayout) findViewById(R.id.LinLayText);
        LinLayPicture = (LinearLayout) findViewById(R.id.LinLayPicture);
        LinLayAudioPlayer = (LinearLayout) findViewById(R.id.LinLayAudioPlayer);

        tvTextContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                startActionMode(new ActionBarCallback_ShareActivity(ShareActivity.this, note, LinLayText));
                return false;
            }
        });

        LinLayPicture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                startActionMode(new ActionBarCallback_ShareActivity(ShareActivity.this, note, LinLayPicture));
                return false;
            }
        });

        LinLayAudioPlayer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                startActionMode(new ActionBarCallback_ShareActivity(ShareActivity.this, note, LinLayAudioPlayer));
                return false;
            }
        });


        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);



    }
}
