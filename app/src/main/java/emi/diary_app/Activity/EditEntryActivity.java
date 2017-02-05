package emi.diary_app.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import emi.diary_app.Note;
import emi.diary_app.PlayButton;
import emi.diary_app.PlayState;
import emi.diary_app.R;

public class EditEntryActivity extends AppCompatActivity {

    static final int PICK_IMAGE = 2;
    static final int IMAGE_CAPTURE = 3;
    final static int REQUEST_RECORD_AUDIO= 32;
    final static int REQUEST_IMAGE_CAPTURE = 35;

    private EditText
            editTitle,
            editTextContent;

    private Note
            note;

    private ImageButton
            btnSaveEntry,
            btnReadEntry,
            btnAddPicture,
            btnRecordAudio,
            btnTakePicture,
            btnDeleteText,
            btnDeletePicture,
            btnDeleteAudio;

    private ImageView
            imageView;

    private View.OnClickListener
            clickerDeleteText,
            clickerDeletePicture,
            clickerDeleteAudio;

    private Uri imageUri;

    private TextToSpeech textToSpeech;


    private boolean RECORDING = false;
    private boolean AUDIOPLAYER_VISIBLE = false;
    private boolean AUDIO_PLAYING = false;

    /* PermissionState */
    boolean PERM_RECORD_AUDIO = true;

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

    private void askForPermission(int REQUEST_CODE) {

        switch (REQUEST_CODE) {

            case (REQUEST_RECORD_AUDIO): {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
                }
                break;
            }

            case (REQUEST_IMAGE_CAPTURE): {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                }
                break;
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(this, "Bitte erlauben sie den Zugriff auf das Mikrofon, um Audio aufnehmen zu können!", Toast.LENGTH_LONG).show();
                }

                break;
            }

            case REQUEST_IMAGE_CAPTURE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(this, "Bitte erlauben sie den Zugriff auf die Kamera, um Fotos aufnehmen zu können!", Toast.LENGTH_LONG).show();
                }

                break;
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.note = (Note) getIntent().getSerializableExtra("note");
        note.setLastPlayedDuration(0);
        REQUEST_CODE = (int) getIntent().getSerializableExtra("requestCode");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        this.setContentView(R.layout.activity_edit_entry);

        InitializeActivity();



    }

    public void InitializeActivity() {

        /* Initialize Views */
        initViews();


        /* Load Image when existing */
        if (!note.getImageNote().equals("")) {

            /* Set up ImageView */
            Bitmap image = BitmapFactory.decodeFile(note.getImageNote());
            imageView.setImageBitmap(image);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300, 1);
            imageView.setLayoutParams(params);
            imageView.setVisibility(View.VISIBLE);

            /* Set up DeleteButton for Picture */
            btnDeletePicture.setVisibility(View.VISIBLE);

            btnDeletePicture.setOnClickListener(clickerDeletePicture);

            note.setBitmap(image);
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
        audioFile = new File(internalStorage, note.getID() + ".3gpp");



        btnSaveEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                note.setTitle(editTitle.getText().toString());
                note.setTextNote(editTextContent.getText().toString());


                if (audioFile.exists()) {

                    note.setVoiceNote(audioFile.getPath());
                }


                /* Save Picture to Note and ExternalStorage */
                if (note.getBitmap() != null) {

                    String picturePath = "";

                    File internalStorage = getDir("Pictures", Context.MODE_PRIVATE);
                    File reportFilePath = new File(internalStorage, note.getID() + ".jpeg");
                    picturePath = reportFilePath.toString();

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(reportFilePath);
                        note.getBitmap().compress(Bitmap.CompressFormat.PNG, 100 ,fos);
                        fos.close();
                    }
                    catch (Exception ex) {
                        Log.i("EditEntry", "Problem writing Image from Camera to External Storage", ex);
                        picturePath = "";
                    }

                    /* Set PicturePath to Note */
                    note.setImageNote(picturePath);

                /* Delete Picture if there is no Bitmap */
                } else {

                    File image = new File(note.getImageNote());
                    image.delete();

                    note.setImageNote("");
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

        imageView.setOnClickListener(new View.OnClickListener() {
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

                        askForPermission(REQUEST_RECORD_AUDIO);


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

                        if (ContextCompat.checkSelfPermission(EditEntryActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

                            /* Stop Audio Record and Create Audio Player */
                            try {
                                stopRecord();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            createAudioPlayer();

                        }

                        /* Reset Record Icon */
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

                /*if (ContextCompat.checkSelfPermission(EditEntryActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    askForPermission(REQUEST_IMAGE_CAPTURE);

                } else {

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, Integer.toString(note.getID()));
                    values.put(MediaStore.Images.Media.DESCRIPTION, note.getTitle());
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, IMAGE_CAPTURE);
                }*/

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, Integer.toString(note.getID()));
                values.put(MediaStore.Images.Media.DESCRIPTION, note.getTitle());
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, IMAGE_CAPTURE);

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

                /* Decode the Bitmap */
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

                imageView.setImageBitmap(bitmap);

                /* Set Bitmap to Note */
                note.setBitmap(bitmap);

                /* Make imageView visivle */
                imageView.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300, 1);
                imageView.setLayoutParams(params);

                /* Set up DeleteButton for Picture */
                btnDeletePicture.setVisibility(View.VISIBLE);
                btnDeletePicture.setOnClickListener(clickerDeletePicture);
            }
        }

        if (requestCode == IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {

                /* Getting Bitmap */
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);

                /* Set Bitmap to ImageView and Note */
                imageView.setImageBitmap(imageBitmap);
                note.setBitmap(imageBitmap);


                /* Make imageView visivle */
                imageView.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300, 1);
                imageView.setLayoutParams(params);

                /* Set up DeleteButton for Picture */
                btnDeletePicture.setVisibility(View.VISIBLE);
                btnDeletePicture.setOnClickListener(clickerDeletePicture);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Bild konnte nicht gespeichert werden!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void createAudioPlayer() {

        /* Create View to Play Audio */
        View voiceEntry = View.inflate(this, R.layout.diary_entry_voice, null);

         /* - - - Set up PlayButton - - - - - - - - - - - - - - - - - - - - - - - */
        final PlayButton playVoiceContent = new PlayButton(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_play));

        } else {
            playVoiceContent.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_media_play));
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, (float) 4.5);
        params.setMargins(15, 15, 0, 15);

        playVoiceContent.setLayoutParams(params);
        playVoiceContent.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout linearLayout = (LinearLayout) voiceEntry.findViewById(R.id.voiceEntryLinLay);
        linearLayout.addView(playVoiceContent, 0);
        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */


        playerSeekBar = (SeekBar) voiceEntry.findViewById(R.id.playerProgressBar);

        /* -  Creating MediaPlayer to get Duration for SeekBar - - - - */
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(note.getVoiceNote());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerSeekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */


        /* Creating Listener for moving SeekBar */
        View.OnTouchListener touchSeek = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        view.setPressed(true);

                        if (playVoiceContent.state == PlayState.PLAYING || playVoiceContent.state == PlayState.PAUSED) {

                            mediaPlayer.pause();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_play));

                            } else {
                                playVoiceContent.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_media_play));
                            }

                            AUDIO_PLAYING = false;

                        } else if (playVoiceContent.state == PlayState.STOPPED) {

                            /* Do noting... */
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_CANCEL:
                        view.setPressed(false);

                        if (playVoiceContent.state == PlayState.PLAYING) {

                            mediaPlayer.seekTo(playerSeekBar.getProgress());

                            /* Set up "Play" Icon */
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_pause));

                            } else {
                                playVoiceContent.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_media_pause));
                            }
                            mediaPlayer.start();

                             /* SeekBar updation */
                            myHandler.postDelayed(UpdateSongTime,100);


                            AUDIO_PLAYING = true;

                        } else if (playVoiceContent.state == PlayState.STOPPED) {

                            note.setLastPlayedDuration(playerSeekBar.getProgress());

                        } else if (playVoiceContent.state == PlayState.PAUSED) {

                            /* Seek to Duration*/
                            mediaPlayer.seekTo(playerSeekBar.getProgress());

                            AUDIO_PLAYING = true;
                        }

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
        };

        playerSeekBar.setOnTouchListener(touchSeek);

        playVoiceContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            /* If Audio is playing */
            if (playVoiceContent.state == PlayState.PLAYING) {

                /* Set up "Pause" Icon */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_play));

                } else {
                    playVoiceContent.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_media_play));
                }

                /* Pause Audio */
                mediaPlayer.pause();

                playVoiceContent.setPaused();

             /* If Audio is stopped */
            } else if (playVoiceContent.state == PlayState.STOPPED){

                /* Set up "Play" Icon */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_pause));

                } else {
                    playVoiceContent.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_media_pause));
                }

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
                        mediaPlayer = null;

                        /* Set State of PlayButton to "Stopped" */
                        playVoiceContent.setStopped();

                        /* Stop SeekBar-Updation */
                        playerSeekBar.setProgress(0);

                        /* Set up "Pause" Icon */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_play));

                        } else {
                            playVoiceContent.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_media_play));
                        }

                        /* Reset LastPlayedDuration in Note */
                        note.setLastPlayedDuration(0);
                    }
                });

                /* Start Audio */
                mediaPlayer.start();

                /* If LastPlayedDuration not 0 -> Seek to Duration*/
                if (note.getLastPlayedDuration() > 0) {

                    mediaPlayer.seekTo(note.getLastPlayedDuration());
                }

                AUDIO_PLAYING = true;

                /* SeekBar updation */
                myHandler.postDelayed(UpdateSongTime,100);

                /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

                playVoiceContent.setPlaying();

            /* If Audio is paused */
            } else if (playVoiceContent.state == PlayState.PAUSED) {

                /* Set up "Play" Icon */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_pause));

                } else {
                    playVoiceContent.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_media_pause));
                }
                mediaPlayer.start();

                /* SeekBar updation */
                myHandler.postDelayed(UpdateSongTime,100);

                playVoiceContent.setPlaying();
            }
            }
        });


        final LinearLayout linLayAudioPlayer = (LinearLayout) findViewById(R.id.LinLayAudioPlayer);

        /* If there is an Audio Player */
        if (AUDIOPLAYER_VISIBLE) {

            /* Remove it */
            linLayAudioPlayer.removeViewAt(0);

        }

        /* Adding LayoutWeight to AudioPlayer */
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        voiceEntry.setLayoutParams(params);

        /* Adding View to LinearLayout from Activity */
        linLayAudioPlayer.addView(voiceEntry, 0);

        /* Set up AudioPlayer DeleteButton */
        btnDeleteAudio.setVisibility(View.VISIBLE);
        btnDeleteAudio.setOnClickListener(clickerDeleteAudio);


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
                myHandler.postDelayed(this, 5);
            }
        }
    };

    private void initViews() {

        btnSaveEntry = (ImageButton) findViewById(R.id.btnSaveEntry);
        btnRecordAudio = (ImageButton) findViewById(R.id.btnRecordAudio);
        btnTakePicture = (ImageButton) findViewById(R.id.btnTakePicture);

        btnReadEntry = (ImageButton) findViewById(R.id.btnReadEntry);
        btnReadEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textToSpeech = new TextToSpeech(EditEntryActivity.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {

                        textToSpeech.setLanguage(Locale.GERMAN);
                        textToSpeech.speak(editTitle.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                        textToSpeech.speak(editTextContent.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                    }
                });
            }
        });

        btnDeleteText = (ImageButton) findViewById(R.id.btnDeleteText);
        btnDeletePicture = (ImageButton) findViewById(R.id.btnDeletePicture);
        btnDeleteAudio = (ImageButton) findViewById(R.id.btnDeleteAudio);

        /* Make DeleteButtons invisible */
        btnDeletePicture.setVisibility(View.INVISIBLE);
        btnDeleteAudio.setVisibility(View.INVISIBLE);


        editTitle = (EditText) findViewById(R.id.editTitle);
        editTextContent = (EditText) findViewById(R.id.editTextContent);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);
        btnAddPicture = (ImageButton) findViewById(R.id.btnAddPicture);

        /* - - - Set up OnClickListener of Delete Buttons - - - - - - - - - - - */

        /* Delete Text */
        clickerDeleteText = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* Dialog before deleting */
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                /* In case of Yes Delete Text */
                                editTextContent.setText("");

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(EditEntryActivity.this);
                builder.setMessage("Text unwiderruflich Löschen?").setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Nein", dialogClickListener).show();
            }
        };

        /* Delete Picture */
        clickerDeletePicture = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* Dialog before deleting */
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                note.setBitmap(null);

                                imageView.setImageDrawable(null);
                                imageView.setVisibility(View.INVISIBLE);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
                                imageView.setLayoutParams(params);

                                btnDeletePicture.setVisibility(View.INVISIBLE);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(EditEntryActivity.this);
                builder.setMessage("Bild unwiderruflich Löschen?").setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Nein", dialogClickListener).show();
            }
        };

        /* Delete AudioPlayer */
        clickerDeleteAudio = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* Dialog before deleting */
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                /* In case of Yes Delete Audio */
                                LinearLayout linLayAudioPlayer = (LinearLayout) findViewById(R.id.LinLayAudioPlayer);

                                linLayAudioPlayer.removeViewAt(0);

                                Space space = new Space(EditEntryActivity.this);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                                space.setLayoutParams(params);

                                linLayAudioPlayer.addView(space, 0);

                                note.setVoiceNote("");

                                audioFile.delete();

                                btnDeleteAudio.setVisibility(View.INVISIBLE);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(EditEntryActivity.this);
                builder.setMessage("Audiowiedergabe unwiderruflich Löschen?").setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Nein", dialogClickListener).show();
            }
        };
        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

        /* Set Clicker for DeleteText */
        btnDeleteText.setOnClickListener(clickerDeleteText);

    }

}