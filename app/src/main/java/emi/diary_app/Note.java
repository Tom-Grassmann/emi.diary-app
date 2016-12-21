package emi.diary_app;

import java.util.Comparator;

public class Note implements Comparator<Note>, Comparable<Note>{

    protected int ID;
    protected String date_last_edited;
    protected String title;

    protected String text_note = "";
    protected String voice_note_path = "";
    protected String image_note_path = "";

    protected NoteType noteType;


    public Note(int ID, String date_last_edited, String title) {

        if (date_last_edited == null) {
            throw new NullPointerException("Date to set cant be NULL!");
        }
        if (title == null) {
            throw new NullPointerException("Title to set cant be NULL!");
        }

        if (ID < 0) {
            throw new IllegalArgumentException("ID should be greater than 0!");
        }
        if (date_last_edited.equals("")) {
            throw new IllegalArgumentException("Date should not be empty!");
        }

        this.ID = ID;
        this.date_last_edited = date_last_edited;
        this.title = title;
        this.noteType = NoteType.TEXT;
    }

    public int getID() {

        return this.ID;
    }

    public String getdate_last_edited() {

        return this.date_last_edited;
    }

    public void setdate_last_edited(String newDate){

        if (newDate == null) {
            throw new NullPointerException("Date to ste cant be NULL!");
        }

        if (newDate.equals("")) {
            throw new IllegalArgumentException("Date to set cant be empty!");
        }

        this.date_last_edited = newDate;
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

    public NoteType getNoteType() {

        return this.noteType;
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

    public void setImageNote(String text) {

        if (text == null) {
            throw new NullPointerException("Text to set as Note cant be NULL!");
        }

        this.image_note_path = text;
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

        // TODO: Context ID
        if (!db.insertData(this)) {
            System.out.println("ERROR adding Note to Database!");
        }
    }

    @Override
    public int compare(Note note, Note t1) {


        return 0;
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


    @Override
    public int compareTo(Note note) {
        return 0;
    }
}
