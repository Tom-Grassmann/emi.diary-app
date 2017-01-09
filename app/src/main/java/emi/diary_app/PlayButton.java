package emi.diary_app;

import android.content.Context;
import android.widget.Button;


enum PlayState {

    STOPPED, PLAYING, PAUSED;
}

public class PlayButton extends Button {

    public PlayState state = PlayState.STOPPED;

    public PlayButton(Context context) {
        super(context);


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
