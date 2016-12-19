package emi.diary_app;

public class VoiceNote extends Note {

    // TODO: Data Type for voicenote!
    protected String voice_note;

    public VoiceNote(int ID, String date_last_edited, String title) {
        super(ID, date_last_edited, title);


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
        this.noteType = NoteType.VOICE;
    }

    public String getNote() {

        return "";
    }
}
