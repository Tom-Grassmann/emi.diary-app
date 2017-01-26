package emi.diary_app;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageButton;


enum PlayState {

    STOPPED, PLAYING, PAUSED
}

public class PlayButton extends Button {

    public PlayState state = PlayState.STOPPED;

    public PlayButton(Context context) {
        super(context);


    }

    public PlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPlaying() {

        this.state = PlayState.PLAYING;
    }

    public void setPaused() {

        this.state = PlayState.PAUSED;
    }

    public void setStopped() {

        this.state = PlayState.STOPPED;
    }



}
