package com.example.android.noteproject.db;

import android.database.Cursor;
import android.database.CursorWrapper;



/**
 *
 */

public class NoteCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public NoteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Note getNote(){
        int id = getInt(getColumnIndex(NoteDbSchema.NoteTable.Cols.ID));
        String url = getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.URL));
        String title = getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.TITLE));
        String content = getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.CONTENT));
        long modifytime = getLong(getColumnIndex(NoteDbSchema.NoteTable.Cols.MODIFYTIME));


        Note note = new Note(id,title,content,modifytime);
        note.setUrl(url);
        return note;
    }


}
