package emi.diary_app.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;

import emi.diary_app.Note;

import static java.lang.Math.abs;

public class Database extends SQLiteOpenHelper implements Serializable{

    private Context appContext;

    private static final String DATABASE_NAME = "entry.db";

    private static final String TABLE_NAME = "entry_table";
    private static final String ID = "ID";
    private static final String TITLE = "TITLE";
    private static final String DATE = "DATE";
    private static final String CITY = "CITY";
    private static final String CONTEXT_TEXT = "TEXT";
    private static final String CONTEXT_VOICE = "VOICE";
    private static final String CONTEXT_IMAGE = "IMAGE";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);

        this.appContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table " +TABLE_NAME+ " (ID INTEGER PRIMARY KEY, " +
                               "TITLE TEXT, DATE TEXT, CITY TEXT, TEXT TEXT, VOICE TEXT, IMAGE TEXT)");
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

        contentValues.put(ID, this.getNextFreeID());
        contentValues.put(TITLE, note.getTitle());
        contentValues.put(DATE, String.valueOf(note.getTimestamp()));
        contentValues.put(CITY, note.getCity());
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
        contentValues.put(DATE, String.valueOf(note.getTimestamp()));
        contentValues.put(CITY, note.getCity());
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

    /* For Testing Only! */
    public boolean removeAllData() {

        SQLiteDatabase db = this.getWritableDatabase();


        String[] index = new String[1];
        int i = 0;
        while (i < 100000) {
            index[0] = Integer.toString(i);
            i++;

            db.delete(TABLE_NAME, "ID = ?", index);
        }

        return true;
    }

    public boolean removeData(Note note) throws NullPointerException {

        if (note == null) {
            throw new NullPointerException("Deleted Note cant be NULL!");
        }

        /* Remove image from internalStorage */
        File internalStorage = appContext.getDir("Pictures", Context.MODE_PRIVATE);
        File FilePath = new File(internalStorage, note.getID() + ".png");
        String picturePath = FilePath.toString();

        if (picturePath != null && picturePath.length() != 0) {
            File reportFilePath = new File(picturePath);
            reportFilePath.delete();
        }

        /* Delete Audio from internal Storage */
        if (!note.getVoiceNote().equals("")) {

            File voiceNote = new File(note.getVoiceNote());
            System.out.println(voiceNote.delete());
        }

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "ID = ?", new String[] { Integer.toString(note.getID()) });

        return result != -1;
    }

    private int IDhelper(int id) {

        int x = 0;

        while (true) {

            if (x != id) {
                return x;
            }

            x++;
        }
    }

    public int getNextFreeID() {

        Cursor data = this.getAllData();
        data.moveToNext();

        if (data.getCount() == 0) {
            return 0;
        }
        if (data.getCount() == 1) {

            return IDhelper(data.getInt(0));
        }

        int id = data.getInt(0);
        int idNext;

        if (id > 0) {

            return IDhelper(id);
        }

        while (data.moveToNext()) {

            idNext = data.getInt(0);

            if (abs(idNext - id) > 1) {

                return id + 1;
            }

            id = data.getInt(0);
        }

        return id + 1;
    }
}
