package emi.diary_app.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import emi.diary_app.ActionBar.ActionBarCallback_ShareActivity;
import emi.diary_app.Note;
import emi.diary_app.PlayButton;
import emi.diary_app.PlayState;
import emi.diary_app.R;

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

    private SeekBar playerSeekBar;
    private MediaPlayer mediaPlayer;
    private boolean AUDIO_PLAYING = false;
    private boolean AUDIOPLAYER_VISIBLE = false;

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
            note.setBitmap(image);
            imageView.setImageBitmap(image);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300, 1);
            imageView.setLayoutParams(params);
            imageView.setVisibility(View.VISIBLE);
        }


        /* Load Audio when existing */
        if (!note.getVoiceNote().equals("")) {

            createAudioPlayer();
        }



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

    private void createAudioPlayer() {

        /* Create View to Play Audio */
        View voiceEntry = View.inflate(this, R.layout.diary_entry_voice, null);

         /* - - - Set up PlayButton - - - - - - - - - - - - - - - - - - - - - - - */
        final PlayButton playVoiceContent = new PlayButton(this);
        playVoiceContent.setBackground(this.getResources().getDrawable(R.drawable.ic_media_play));

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

                            playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_play));

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
                            playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_pause));

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
                            mediaPlayer = null;

                        /* Set State of PlayButton to "Stopped" */
                            playVoiceContent.setStopped();

                        /* Stop SeekBar-Updation */
                            playerSeekBar.setProgress(0);

                        /* Set up "Pause" Icon */
                            playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_play));

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
                    playVoiceContent.setBackground(getResources().getDrawable(R.drawable.ic_media_pause));

                    mediaPlayer.start();

                /* SeekBar updation */
                    myHandler.postDelayed(UpdateSongTime,100);

                    playVoiceContent.setPlaying();
                }
            }
        });


        final LinearLayout linLayAudioPlayer = (LinearLayout) findViewById(R.id.LinLayAudioPlayer);



        /* Adding LayoutWeight to AudioPlayer */
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        voiceEntry.setLayoutParams(params);

        /* Adding View to LinearLayout from Activity */
        linLayAudioPlayer.addView(voiceEntry, 0);


        AUDIOPLAYER_VISIBLE = true;

    }


    private double startTime = 0;

    private Handler myHandler = new Handler();;

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {

            if (AUDIO_PLAYING) {

                startTime = mediaPlayer.getCurrentPosition();

                TextView tx1 = new TextView(ShareActivity.this);

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

}
