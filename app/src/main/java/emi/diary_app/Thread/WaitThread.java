package emi.diary_app.Thread;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import emi.diary_app.Activity.MainActivity;

public class WaitThread extends Thread {

    private static final String TAG = "WaitThread";
    private final static int DELAY = 1000;
    private int duration;

    private Handler waitHandler;

    public WaitThread(int duration, final Handler waitHandler) {

        this.waitHandler = waitHandler;
        this.duration = duration;
    }

    public void run() {

        int count = 0;

        while (count < duration) {

            Message message = new Message();
            message.arg1 = 1;

            waitHandler.sendMessage(message);

            count++;

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                Log.d(TAG, "Interrupting and stopping the Wait Thread");
                return;
            }
        }



    }


}
