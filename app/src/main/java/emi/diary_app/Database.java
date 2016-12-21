package emi.diary_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "entry.db";

    private static final String TABLE_NAME = "entry_table";
    private static final String ID = "ID";
    private static final String TITLE = "TITLE";
    private static final String DATE = "DATE";
    private static final String CONTEXT_TEXT = "TEXT";
    private static final String CONTEXT_VOICE = "VOICE";
    private static final String CONTEXT_IMAGE = "IMAGE";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table " +TABLE_NAME+ " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                               "TITLE TEXT, DATE TEXT, TEXT TEXT, VOICE TEXT, IMAGE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(Note note) throws NullPointerException {

        if (note == null) {
            throw new NullPointerException("Inserted Note cant be NULL!");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TITLE, note.getTitle());
        contentValues.put(DATE, note.getdate_last_edited());
        contentValues.put(CONTEXT_TEXT, note.getTextNote());
        contentValues.put(CONTEXT_VOICE, note.getVoiceNote());
        contentValues.put(CONTEXT_IMAGE, note.getImageNote());


        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public boolean updateData(Note note) throws NullPointerException {

        if (note == null) {
            throw new NullPointerException("Updated Note cant be NULL!");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, note.getID());
        contentValues.put(TITLE, note.getTitle());
        contentValues.put(DATE, note.getdate_last_edited());
        contentValues.put(CONTEXT_TEXT, note.getTextNote());
        contentValues.put(CONTEXT_VOICE, note.getVoiceNote());
        contentValues.put(CONTEXT_IMAGE, note.getImageNote());

        long result = db.update(TABLE_NAME, contentValues, "ID = ?", new String[] { Integer.toString(note.getID()) });

        return result != -1;
    }

    public Cursor getAllData() {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("select * from " +TABLE_NAME, null);
    }

    public boolean removeData(Note note) throws NullPointerException {

        if (note == null) {
            throw new NullPointerException("Deleted Note cant be NULL!");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "ID = ?", new String[] { Integer.toString(note.getID()) });

        return result != -1;
    }




}
