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
import android.view.ViewGroup;
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
    static final int REQUEST_IMAGE_CAPTURE = 3;


    private EditText
            editTitle,
            editTextContent;

    private Note
            note;

    private ImageButton
            btnSaveEntry,
            imageButton,
            btnAddPicture,
            btnRecordAudio,
            btnTakePicture;


    private boolean RECORDING = false;
    private boolean AUDIOPLAYER_VISIBLE = false;
    private boolean AUDIO_PLAYING = false;


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

        btnSaveEntry = (ImageButton) findViewById(R.id.btnSaveEntry);
        btnRecordAudio = (ImageButton) findViewById(R.id.btnRecordAudio);
        btnTakePicture = (ImageButton) findViewById(R.id.btnTakePicture);


        editTitle = (EditText) findViewById(R.id.editTitle);
        editTextContent = (EditText) findViewById(R.id.editTextContent);

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        btnAddPicture = (ImageButton) findViewById(R.id.btnAddPicture);


        /* Load Image when existing */
        if (!note.getImageNote().equals("")) {

            btnAddPicture.setVisibility(View.INVISIBLE);
            // TODO: Make sure that Buttons of Toolbar stay in place

            Bitmap image = BitmapFactory.decodeFile(note.getImageNote());
            imageButton.setImageBitmap(image);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
            imageButton.setLayoutParams(params);
            imageButton.setVisibility(View.VISIBLE);

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



        btnSaveEntry.setOnClickListener(new View.OnClickListener() {
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

        btnAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                /* Start Activity to Choose Picture from Gallery */
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});


                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* Start Activity to Choose Picture from Gallery */
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

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

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

        note.setVoiceNote(audioFile.getPath());
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

                    /* Make ImageButton visivle */
                    imageButton.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
                    imageButton.setLayoutParams(params);

                    /* Make AddPicture Button in Toolbar invisible */
                    btnAddPicture.setVisibility(View.INVISIBLE);
                    btnAddPicture.setEnabled(false);
                    LinearLayout linLay = (LinearLayout) findViewById(R.id.toolBar);
                    linLay.removeView(btnAddPicture);
                    linLay.addView(btnAddPicture, 1);

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

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {


            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageButton.setImageBitmap(imageBitmap);


            /* Make ImageButton visivle */
            imageButton.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
            imageButton.setLayoutParams(params);

            /* Make AddPhoto and AddImage button Invisible */
            // TODO: Make invisible

            /* Set Bitmap and ImagePath to Note */
            note.setBitmap(imageBitmap);

            /* Write Image to internal Storage and extract the Path of it */
            String picturePath = "";
            if (note.getImageNote() != null) {

                File internalStorage = this.getDir("Pictures", Context.MODE_PRIVATE);
                File reportFilePath = new File(internalStorage, note.getID() + ".png");
                picturePath = reportFilePath.toString();

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(reportFilePath);
                    note.getBitmap().compress(Bitmap.CompressFormat.PNG, 100 , fos);
                    fos.close();
                }
                catch (Exception ex) {
                    Log.i("EditEntry", "Problem writing Image from Camera to External Storage", ex);
                    picturePath = "";
                }

                /* Set PicturePath to Note */
                note.setImageNote(picturePath);
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
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 10, 5, 10);
        linearLayout.setLayoutParams(params);
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

                        AUDIO_PLAYING = false;


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

                AUDIO_PLAYING = true;


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
        if (AUDIOPLAYER_VISIBLE) {

            /* Remove it */
            linearLayoutActivity.removeViewAt(3);

        }
        /* Adding View to LinearLayout from Activity */
        linearLayoutActivity.addView(voiceEntry, 3);

        AUDIOPLAYER_VISIBLE = true;

    }


    private double startTime = 0;

    private Handler myHandler = new Handler();;

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {

            if (AUDIO_PLAYING) {

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