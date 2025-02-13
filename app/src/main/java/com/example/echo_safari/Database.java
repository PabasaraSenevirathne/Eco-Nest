package com.example.echo_safari;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    // Constructor
    public Database(@Nullable Context context) {
        super(context, "eco_nest_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the "users" table
        String qry1 = "CREATE TABLE IF NOT EXISTS users (fullname TEXT, username TEXT PRIMARY KEY, email TEXT, contact TEXT, password TEXT)";
        db.execSQL(qry1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // Method to register a new user
    public void register(String fullname, String username, String email, String contact, String password) {
        ContentValues cv = new ContentValues();
        cv.put("fullname", fullname);
        cv.put("username", username);
        cv.put("email", email);
        cv.put("contact", contact);
        cv.put("password", password);

        SQLiteDatabase db = getWritableDatabase();
        db.insert("users", null, cv);
        db.close();
    }

    // Method to validate login
    public boolean login(String username, String password) {
        boolean result = false;
        String[] args = {username, password};

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", args);

        if (c.moveToFirst()) {
            result = true;
        }

        c.close();
        db.close();
        return result;
    }

    // **New method to fetch user details**
    public Cursor getUserDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
    }
}
