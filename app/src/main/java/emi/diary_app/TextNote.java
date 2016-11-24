package emi.note_app;

public class TextNote extends Note{

    protected String text_note;

    public TextNote(int ID, String date_last_edited, String title) {
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
        this.noteType = NoteType.TEXT;
    }


    public String getText_note() {

        return this.text_note;
    }

    public void setText_note(String text) {

        if (text == null) {
            throw new NullPointerException("Text to set as Note cant be NULL!");
        }

        this.text_note = text;
    }

}
