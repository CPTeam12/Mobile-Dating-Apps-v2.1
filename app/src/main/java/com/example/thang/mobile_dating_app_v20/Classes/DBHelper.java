package com.example.thang.mobile_dating_app_v20.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thang on 6/2/2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LocalUser.db";
    private static final String USER_TABLE_NAME = "User";

    private static final String USER_COL_ID = "username";
    private static final String USER_COL_FULLNAME = "fullname";
    private static final String USER_COL_EMAIL = "email";
    private static final String USER_COL_AGE = "age";
    private static final String USER_COL_GENDER = "gender";
    private static final String USER_COL_AVATAR = "avatar";
    private static final String USER_COL_PHONE = "phone";
    private static final String USER_COL_ADDRESS = "address";

    //flag for separate between localuser and their friends
    //flag = localuser for current account
    //flag = friend for their friend
    private static final String USER_FLAG = "flag";
    public static final String USER_FLAG_CURRENT = "localuser";
    public static final String USER_FLAG_FRIENDS = "friend";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String databaseCreate = "CREATE TABLE " + USER_TABLE_NAME + "("
                + USER_COL_ID + " text primary key, "
                + USER_COL_FULLNAME + " text not null, "
                + USER_COL_EMAIL + " text not null, "
                + USER_COL_AGE + " integer, "
                + USER_COL_AVATAR + " text, "
                + USER_COL_ADDRESS + " text, "
                + USER_COL_GENDER + " text, "
                + USER_COL_PHONE + " text,"
                + USER_FLAG + " text not null"
                + ");";
        db.execSQL(databaseCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        onCreate(db);
    }

    public void insertPerson(Person person, String flag){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL_ADDRESS,person.getAddress());
        contentValues.put(USER_COL_AGE,person.getAge());
        contentValues.put(USER_COL_AVATAR,person.getAvatar());
        contentValues.put(USER_COL_EMAIL,person.getEmail());
        contentValues.put(USER_COL_FULLNAME,person.getFullName());
        contentValues.put(USER_COL_GENDER,person.getGender());
        contentValues.put(USER_COL_ID,person.getUsername());
        contentValues.put(USER_COL_PHONE,person.getPhone());
        contentValues.put(USER_FLAG, flag);

        db.insert(USER_TABLE_NAME, null, contentValues);
    }

    public List<Person> getAllFriends(){
        List<Person> persons = new ArrayList<>();
        String query = "SELECT * FROM " + USER_TABLE_NAME + " WHERE "
                    + USER_FLAG + " = '" + USER_FLAG_FRIENDS + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(query, null);
        res.moveToFirst();

        while (res.isAfterLast() == false){
            Person person = new Person();
            person.setAddress(res.getString(res.getColumnIndex(USER_COL_ADDRESS)));
            person.setFullName(res.getString(res.getColumnIndex(USER_COL_FULLNAME)));
            person.setAge(Integer.parseInt(res.getString(res.getColumnIndex(USER_COL_AGE))));
            person.setAvatar(res.getString(res.getColumnIndex(USER_COL_AVATAR)));
            person.setEmail(res.getString(res.getColumnIndex(USER_COL_EMAIL)));
            person.setGender(res.getString(res.getColumnIndex(USER_COL_GENDER)));
            person.setPhone(Integer.parseInt(res.getString(res.getColumnIndex(USER_COL_PHONE))));
            persons.add(person);
        }
        return persons;
    }

    public Person getCurrentUser(){
        Person person = new Person();

        String query = "SELECT * FROM " + USER_TABLE_NAME + " WHERE "
                + USER_FLAG + " = '" + USER_FLAG_CURRENT + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(query, null);
        res.moveToFirst();
        int a = res.getCount();
        if (res.getCount() != 0){
            person.setAddress(res.getString(res.getColumnIndex(USER_COL_ADDRESS)));
            person.setFullName(res.getString(res.getColumnIndex(USER_COL_FULLNAME)));
            person.setAge(Integer.parseInt(res.getString(res.getColumnIndex(USER_COL_AGE))));
            person.setAvatar(res.getString(res.getColumnIndex(USER_COL_AVATAR)));
            person.setEmail(res.getString(res.getColumnIndex(USER_COL_EMAIL)));
            person.setGender(res.getString(res.getColumnIndex(USER_COL_GENDER)));
            person.setPhone(Integer.parseInt(res.getString(res.getColumnIndex(USER_COL_PHONE))));
        }

        if (!res.isClosed()){
            res.close();
        }

        return person;
    }

}
