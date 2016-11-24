package emi.note_app;

public abstract class Note {

    protected int ID;
    protected String date_last_edited;
    protected String title;
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


}
