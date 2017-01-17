package emi.diary_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class EditEntryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 2;

    private Button
            btnBack;

    private EditText
            editTitle,
            editTextContent;

    private Note
            note;

    private ImageButton
            imageButton,
            btnAddPicture,
            btnRecordAudio;


    private boolean RECORDING = false;


    private File audioFile = null;

    private MediaPlayer
            mediaPlayer;

    private MediaRecorder
            mediaRecorder;

    private SeekBar
            playerSeekBar;

    private static final int EDIT_ENTRY = 1;
    private int REQUEST_CODE = 0;

    public EditEntryActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.note = (Note) getIntent().getSerializableExtra("note");
        REQUEST_CODE = (int) getIntent().getSerializableExtra("requestCode");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        this.setContentView(R.layout.activity_edit_entry);

        InitializeActivity();
    }

    public void InitializeActivity() {

        btnBack = (Button) findViewById(R.id.btnBack);
        btnRecordAudio = (ImageButton) findViewById(R.id.btnRecordAudio);


        editTitle = (EditText) findViewById(R.id.editTitle);
        editTextContent = (EditText) findViewById(R.id.editTextContent);

        imageButton = (ImageButton) findViewById(R.id.imageButton);


        /* Load Image when existing */
        if (!note.getImageNote().equals("")) {

            Bitmap image = BitmapFactory.decodeFile(note.getImageNote());
            imageButton.setImageBitmap(image);
        }

        if (REQUEST_CODE == EDIT_ENTRY) {

            editTitle.setText(note.getTitle());
            editTextContent.setText(note.getTextNote());

            /* Create View to Play Audio */
            if (!note.getVoiceNote().equals("")) {

                createAudioPlayer();
            }
        }

        File internalStorage = this.getDir("Audio", Context.MODE_PRIVATE);
        audioFile = new File(internalStorage, note.getID() + ".3gp");



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                note.setTitle(editTitle.getText().toString());
                note.setTextNote(editTextContent.getText().toString());


                if (audioFile.exists()) {

                    note.setVoiceNote(audioFile.getPath());
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("note", note);
                setResult(Activity.RESULT_OK, resultIntent);

                finish();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });

        btnRecordAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        view.setPressed(true);

                        /* Start Audio Record */
                        try {
                            startRecord();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        /* Set up OnRecord Icon */
                        btnRecordAudio.setImageDrawable(getResources().getDrawable(R.drawable.ic_microfphone_recording));

                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_CANCEL:
                        view.setPressed(false);

                        /* Stop Audio Record and Create Audio Player */
                        try {
                            stopRecord();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        createAudioPlayer();

                        /* Set up OnRecord Icon */
                        btnRecordAudio.setImageDrawable(getResources().getDrawable(R.drawable.ic_microfphone));

                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                }

                return false;
            }
        });

    }

    void startRecord() throws Exception {

        try {

            if (mediaRecorder != null) {

                mediaRecorder.release();
            }


        /* Override existing File */
            if (audioFile != null) {

                audioFile.delete();
            }


            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);



            mediaRecorder.setOutputFile(audioFile.getPath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                Log.e("AudioRecord", "prepare() failed");
                e.printStackTrace();
            }
            mediaRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void stopRecord() {

        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data == null) {

                Toast.makeText(this, "Failed loading Image!", Toast.LENGTH_SHORT).show();

            } else {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                imageButton.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                imageButton.setMaxHeight(500);

                /* Write Image to internal Storage and extract the Path of it */
                if (BitmapFactory.decodeFile(picturePath) != null) {

                    File internalStorage = this.getDir("Pictures", Context.MODE_PRIVATE);
                    File reportFilePath = new File(internalStorage, note.getID() + ".png");
                    note.setImageNote(reportFilePath.toString());
                    System.out.println(note.getImageNote());

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(reportFilePath);
                        BitmapFactory.decodeFile(picturePath).compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                    } catch (Exception ex) {
                        Log.i("EditEntryActivity", "Problem save picture", ex);
                        note.setImageNote("");
                    }
                }
            }
        }
    }

    private void createAudioPlayer() {

            /* Create View to Play Audio */
            View voiceEntry = View.inflate(this, R.layout.diary_entry_voice, null);

                 /* - - - Set up PlayButton - - - - - - - - - - - - - - - - - - - - - - - */
            final PlayButton playVoiceContent = new PlayButton(this);
            playVoiceContent.setBackground(this.getResources().getDrawable(R.drawable.ic_media_play));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
            params.setMargins(5, 0, 0, 0);
            params.height = 110;
            params.width = 110;

            playVoiceContent.setLayoutParams(params);

                /* Add Button to Linear Layout */
            LinearLayout linearLayout = (LinearLayout) voiceEntry.findViewById(R.id.voiceEntryLinLay);
            linearLayout.addView(playVoiceContent, 0);
                /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

            playerSeekBar = (SeekBar) voiceEntry.findViewById(R.id.playerProgressBar);

            playVoiceContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /* If Audio is playing */
                if (playVoiceContent.state == PlayState.PLAYING) {

                        /* Set up "Pause" Icon */
                    playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_play));

                        /* Pause Audio */
                    mediaPlayer.pause();

                    playVoiceContent.setPaused();

                     /* If Audio is stopped */
                } else if (playVoiceContent.state == PlayState.STOPPED){

                        /* Set up "Play" Icon */
                    playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_pause));


                        /* - - - Preparing MediaPlayer - - - - - - - - - - - - - - - - - - - - - */
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(note.getVoiceNote());
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                        /* Set OnCompletitionListener to MediaPlayer */
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {

                                /* Re-Preparing MediaPlayer */
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();

                                /* Set State of PlayButton to "Stopped" */
                            playVoiceContent.setStopped();

                                /* Stop SeekBar-Updation */
                            playerSeekBar.setProgress(0);

                                /* Set up "Pause" Icon */
                            playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_play));
                        }
                    });

                        /* Start Audio */
                    mediaPlayer.start();


                        /* SeekBar updation */
                    playerSeekBar.setMax(mediaPlayer.getDuration());
                    playerSeekBar.setProgress((int)startTime);
                    myHandler.postDelayed(UpdateSongTime,100);

                        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */


                    playVoiceContent.setPlaying();

                    /* If Audio is paused */
                } else if (playVoiceContent.state == PlayState.PAUSED) {

                        /* Set up "Play" Icon */
                    playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_pause));

                    mediaPlayer.start();

                        /* SeekBar updation */
                    playerSeekBar.setMax(mediaPlayer.getDuration());
                    playerSeekBar.setProgress((int)startTime);
                    myHandler.postDelayed(UpdateSongTime,100);

                    playVoiceContent.setPlaying();
                }
            }
        });


        LinearLayout linearLayoutActivity = (LinearLayout) findViewById(R.id.LinearLayoutEditEntry);

        /* If there is an Audio Player */
        if (linearLayoutActivity.getChildCount() < 5) {

            /* Remove it */
            linearLayoutActivity.removeViewAt(4);

        }
        /* Adding View to LinearLayout from Activity */
        linearLayoutActivity.addView(voiceEntry, 4);

    }


    private double startTime = 0;

    private Handler myHandler = new Handler();;

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {

            if (true) {

                startTime = mediaPlayer.getCurrentPosition();

                TextView tx1 = new TextView(EditEntryActivity.this);

                tx1.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime)))
                );

                playerSeekBar.setProgress((int)startTime);
                myHandler.postDelayed(this, 100);
            }
        }
    };

}