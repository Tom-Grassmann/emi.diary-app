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
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EditEntryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 2;

    private Button
            btnPlayAudio,
            btnRecordAudio,
            btnBack;

    private EditText
            editTitle,
            editTextContent;

    private Note
            note;

    private ImageButton
            imageButton;

    private File audioFile = null;

    private MediaPlayer mediaPlayer;

    private MediaRecorder mediaRecorder;

    private static final int EDIT_ENTRY = 1;
    private int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        this.note = (Note) getIntent().getSerializableExtra("note");
        REQUEST_CODE = (int) getIntent().getSerializableExtra("requestCode");


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        InitializeActivity();
    }

    public void InitializeActivity() {

        btnPlayAudio = (Button) findViewById(R.id.btnPlayAudio);
        btnRecordAudio = (Button) findViewById(R.id.btnRecordAudio);
        btnBack = (Button) findViewById(R.id.btnBack);

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

        btnRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btnRecordAudio.getText().toString().equals("Record")) {


                    try {
                        startRecord();
                    } catch (Exception e) {

                        String filename = "errLogBtn";
                        FileOutputStream outputStream;

                        try {
                            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                            outputStream.write(e.toString().getBytes());
                            outputStream.close();
                        } catch (Exception ex) {
                            e.printStackTrace();
                        }
                    }

                    btnRecordAudio.setText("Stop Record");

                } else if (btnRecordAudio.getText().toString().equals("Stop Record")) {

                    stopRecord();

                    btnRecordAudio.setText("Record");
                }
            }
        });

        btnPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btnPlayAudio.getText().toString().equals("Play")) {

                    startPlay();

                    btnPlayAudio.setText("Stop");

                } else if (btnPlayAudio.getText().toString().equals("Stop")) {

                    stopPlay();

                    btnPlayAudio.setText("Play");
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

            String filename = "errLog";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(e.toString().getBytes());
                outputStream.close();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }

    }




    void stopRecord() {

        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    void startPlay() {

        if (mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();

        /* Set up MediaPlayer */
        try {
            mediaPlayer.setDataSource(audioFile.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                mediaPlayer.release();
            }
        });


    }

    void stopPlay() {

        mediaPlayer.stop();
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

}
