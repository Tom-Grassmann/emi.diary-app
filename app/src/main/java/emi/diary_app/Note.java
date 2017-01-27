package emi.diary_app;

import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.widget.SeekBar;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;

import emi.diary_app.Database.Database;


public class Note implements Comparator<Note>, Comparable<Note>, Serializable {

    protected int ID;
    protected long timestamp;
    protected String title;
    protected String city;

    protected transient SeekBar seekBar;
    protected int lastPlayedDuration;

    protected String text_note = "";
    protected String voice_note_path = "";
    protected String image_note_path = "";

    protected transient Bitmap bitmap = null;

    protected int playerDuration = 0;


    public Note(int ID, String title) {

        if (title == null) {
            throw new NullPointerException("Title to set cant be NULL!");
        }

        if (ID < 0) {
            throw new IllegalArgumentException("ID should be greater than 0!");
        }

        this.ID = ID;
        this.title = title;
    }

    public int getID() {

        return this.ID;
    }

    public String getTitle() {

        return this.title;
    }

    public void setTitle(String title) {

        if (title == null) {
            throw new NullPointerException("Title to set cant be NULL");
        }

        this.title = title;
    }

    public void setTextNote(String text) {

        if (text == null) {
            throw new NullPointerException("Text to set as Note cant be NULL!");
        }

        this.text_note = text;
    }

    public void setVoiceNote(String text) {

        if (text == null) {
            throw new NullPointerException("Text to set as Note cant be NULL!");
        }

        this.voice_note_path = text;
    }

    public void setImageNote(String image_note_path) {

        this.image_note_path = image_note_path;
    }

    public String getTextNote() {

        return this.text_note;
    }

    public String getVoiceNote() {

        return this.voice_note_path;
    }

    public String getImageNote() {

        return this.image_note_path;
    }

    public void addToDatabase(Database db) {

        if (!db.insertData(this)) {
            System.out.println("ERROR adding Note to Database!");
        }
    }

    public boolean equals(Object object) {

        if(object instanceof Note) {

            Note otherNote = (Note) object;

            if (otherNote.getID() == this.ID) {

                return true;

            } else {

                return false;
            }

        } else {

            return false;
        }
    }

    public void setTimestamp(long timestamp) {

        this.timestamp = timestamp;
    }

    public long getTimestamp() {

        return this.timestamp;
    }

    public String getDate_Location() {

        String location = "";

        if (this.city != null) {
            if (!this.city.equals("NO_LOCATION")) {

                location = this.city;
            }
        }

        Calendar calendar = Calendar.getInstance(Locale.GERMAN);
        calendar.setTimeInMillis(this.timestamp);
        DateFormat dateFormat = new DateFormat();


        return dateFormat.format("hh:mm:ss, dd.MM.yyyy", calendar).toString() + " " + location;
    }


    @Override
    public int compare(Note note, Note t1) {


        return 0;
    }

    @Override
    public int compareTo(Note note) {

        if (this.timestamp == note.getTimestamp()) { return 0; }
        if (this.timestamp < note.getTimestamp()) { return 1; }
        if (this.timestamp > note.getTimestamp()) {return -1; }
        else return 0;
    }


    public SeekBar getSeekBar() {
        return seekBar;
    }

    public void setSeekBar(SeekBar seekBar) {
        this.seekBar = seekBar;
    }

    public int getLastPlayedDuration() {
        return lastPlayedDuration;
    }

    public void setLastPlayedDuration(int lastPlayedDuration) {
        this.lastPlayedDuration = lastPlayedDuration;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getPlayerDuration() {
        return playerDuration;
    }

    public void setPlayerDuration(int playerDuration) {
        this.playerDuration = playerDuration;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
