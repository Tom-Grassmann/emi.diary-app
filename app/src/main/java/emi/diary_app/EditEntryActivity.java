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

    private MediaPlayer mediaPlayer;

    private MediaRecorder mediaRecorder;
    private String audiopath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        this.note = (Note) getIntent().getSerializableExtra("note");


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

        editTitle.setText(note.getTitle());
        editTextContent.setText(note.getTextNote());

        /* Load Image when existing */
        if (!note.getImageNote().equals("")) {

            Bitmap image = BitmapFactory.decodeFile(note.getImageNote());
            imageButton.setImageBitmap(image);
        }



        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                note.setTitle(editTitle.getText().toString());
                note.setTextNote(editTextContent.getText().toString());

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

                    /* Get App Directory Path */
                    PackageManager m = getPackageManager();
                    String appDir = getPackageName();
                    PackageInfo p = null;
                    try {
                        p = m.getPackageInfo(appDir, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    appDir = p.applicationInfo.dataDir;

                    /* Add AudioDir and DataName to the Path */
                    audiopath = appDir + "/app_Audio/" + note.getID() + ".3pp";
                    System.out.println(audiopath);

                    //startRecord();

                    btnRecordAudio.setText("Stop Record");

                } else if (btnRecordAudio.getText().toString().equals("Stop Record")) {

                    //stopRecord();

                    btnRecordAudio.setText("Record");
                }
            }
        });

        btnPlayAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btnPlayAudio.getText().toString().equals("Play")) {

                    //startPlay();

                    btnPlayAudio.setText("Stop");

                } else if (btnPlayAudio.getText().toString().equals("Stop")) {

                    //stopPlay();

                    btnPlayAudio.setText("Play");
                }
            }
        });

    }

    void startRecord() {

        if (mediaRecorder != null) {

            mediaRecorder.release();
        }

        File audioRecord = new File(audiopath);

        /* Override existing File */
        if (audioRecord != null) {

            audioRecord.delete();
        }

        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        /*mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(mFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);*/

        MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(mFileName);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();


        /*try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();*/
    }

    void stopRecord() {

        mediaRecorder.stop();
        mediaRecorder.release();
    }

    void startPlay() {

        if (mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();

        /* Set up MediaPlayer */
        try {
            mediaPlayer.setDataSource(audiopath);
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
