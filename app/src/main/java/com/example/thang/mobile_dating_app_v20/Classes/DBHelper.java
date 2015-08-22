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
    private static DBHelper instance = null;

    private static final String DATABASE_NAME = "LocalUser.db";

    //User DB
    private static final String USER_TABLE = "User";

    private static final String USER_COL_FULLNAME = "fullname";
    private static final String USER_COL_EMAIL = "email";
    private static final String USER_COL_AGE = "age";
    private static final String USER_COL_GENDER = "gender";
    private static final String USER_COL_AVATAR = "avatar";
    private static final String USER_COL_PHONE = "phone";
    private static final String USER_COL_ADDRESS = "address";
    private static final String USER_COL_HOBBIES = "hobbies";

    private static final String USER_COL_LATITUDE = "latitude";
    private static final String USER_COL_LONGITUDE = "longitude";

    private static final String USER_COL_ISDATINGMEN = "isdatingmen";
    private static final String USER_COL_ISDATINGWOMEN = "isdatingwomen";
    private static final String USER_COL_DATINGAGE = "datingage";

    private static final String USER_COL_REGISID = "registrationid";

    //flag for separate between localuser and their friends
    //flag = localuser for current account
    //flag = friend for their friend
    private static final String USER_COL_FLAG = "flag";
    public static final String USER_FLAG_CURRENT = "localuser";
    public static final String USER_FLAG_FRIENDS = "friend";

    //Conversation table
    private final static String CONVERSATION_TABLE = "Conversation";
    private final static String CONVERSATION_COL_ID = "id";
    private final static String CONVERSATION_COL_USER1 = "u_email1";
    private final static String CONVERSATION_COL_USER2 = "u_email2";
    private final static String CONVERSATION_COL_TIME = "time";

    //Conversation detail
    private final static String CONVERSATION_DETAIL_TABLE = "Conversation_Detail";
    private final static String CONVERSATION_DETAIL_COL_ID = "id";
    private final static String CONVERSATION_DETAIL_COL_EMAIL = "u_email";
    private final static String CONVERSATION_DETAIL_COL_MESSAGE = "message";
    private final static String CONVERSATION_DETAIL_COL_TIME = "time";
    private final static String CONVERSATION_DETAIL_COL_C_ID = "c_id";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String userDB = "CREATE TABLE " + USER_TABLE + "("
                + USER_COL_EMAIL + " text primary key not null, "
                + USER_COL_FULLNAME + " text, "
                + USER_COL_AGE + " INTEGER, "
                + USER_COL_AVATAR + " text, "
                + USER_COL_ADDRESS + " text, "
                + USER_COL_GENDER + " text, "
                + USER_COL_PHONE + " text, "
                + USER_COL_REGISID + " text, "
                + USER_COL_LATITUDE + " text, "
                + USER_COL_LONGITUDE + " text, "
                + USER_COL_ISDATINGMEN + " INTEGER default 0, "
                + USER_COL_ISDATINGWOMEN + " INTEGER default 0, "
                + USER_COL_DATINGAGE + " INTEGER default 18, "
                + USER_COL_HOBBIES + " text, "
                + USER_COL_FLAG + " text not null"
                + ");";
        db.execSQL(userDB);

        String conversationDB = "CREATE TABLE " + CONVERSATION_TABLE + "("
                + CONVERSATION_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CONVERSATION_COL_USER1 + " text not null, "
                + CONVERSATION_COL_USER2 + " text not null, "
                + CONVERSATION_COL_TIME + " text"
                + ");";
        db.execSQL(conversationDB);

        String conversationDetailDB = "CREATE TABLE " + CONVERSATION_DETAIL_TABLE + "("
                + CONVERSATION_DETAIL_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CONVERSATION_DETAIL_COL_EMAIL + " text not null, "
                + CONVERSATION_DETAIL_COL_MESSAGE + " text, "
                + CONVERSATION_DETAIL_COL_C_ID + " INTEGER not null, "
                + CONVERSATION_DETAIL_COL_TIME + " text"
                + ");";
        db.execSQL(conversationDetailDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    public void deleteData() {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + USER_TABLE;
        db.execSQL(query);

        String query1 = "DELETE FROM " + CONVERSATION_TABLE;
        db.execSQL(query1);

        String query2 = "DELETE FROM " + CONVERSATION_DETAIL_TABLE;
        db.execSQL(query2);

        db.close();
    }

    public void deleteCurrentUser() {
        String query = "DELETE FROM " + USER_TABLE + " WHERE "
                + USER_COL_FLAG + " = '" + USER_FLAG_CURRENT + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void insertPerson(Person person, String flag) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL_ADDRESS, person.getAddress());
        contentValues.put(USER_COL_AGE, person.getAge());
        contentValues.put(USER_COL_AVATAR, person.getAvatar());
        contentValues.put(USER_COL_EMAIL, person.getEmail());
        contentValues.put(USER_COL_FULLNAME, person.getFullName());
        contentValues.put(USER_COL_GENDER, person.getGender());
        contentValues.put(USER_COL_PHONE, person.getPhone());
        contentValues.put(USER_COL_HOBBIES, person.getHobbies());
        contentValues.put(USER_COL_LATITUDE, person.getLatitude());
        contentValues.put(USER_COL_LONGITUDE, person.getLongitude());
        contentValues.put(USER_COL_REGISID, person.getRegistrationID());
        int isDatingMen = person.isDatingMen() ? 1 : 0;
        contentValues.put(USER_COL_ISDATINGMEN, isDatingMen);
        int isDatingWomean = person.isDatingWomen() ? 1 : 0;
        contentValues.put(USER_COL_ISDATINGWOMEN, isDatingWomean);
        contentValues.put(USER_COL_DATINGAGE,person.getDatingAge());
        contentValues.put(USER_COL_FLAG, flag);

        db.insert(USER_TABLE, null, contentValues);
        db.close();
    }

    public void updatePerson(Person person) {
        String query = "DELETE FROM " + USER_TABLE + " WHERE "
                + USER_COL_FLAG + " = '" + USER_FLAG_CURRENT + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
        insertPerson(person, USER_FLAG_CURRENT);
    }

    public void deletePerson(String email) {
        String query = "DELETE FROM " + USER_TABLE +
                " WHERE " + USER_COL_EMAIL + " = '" + email + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void deleteAllFriend() {
        String query = "DELETE FROM " + USER_TABLE + " WHERE "
                + USER_COL_FLAG + " = '" + USER_FLAG_FRIENDS + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public List<Person> getAllFriends() {
        List<Person> persons = new ArrayList<>();
        String query = "SELECT * FROM " + USER_TABLE + " WHERE "
                + USER_COL_FLAG + " = '" + USER_FLAG_FRIENDS + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, null);

        while (res.moveToNext()) {
            Person person = new Person();
            person.setAddress(res.getString(res.getColumnIndex(USER_COL_ADDRESS)));
            person.setFullName(res.getString(res.getColumnIndex(USER_COL_FULLNAME)));
            person.setAge(Integer.parseInt(res.getString(res.getColumnIndex(USER_COL_AGE))));
            person.setAvatar(res.getString(res.getColumnIndex(USER_COL_AVATAR)));
            person.setEmail(res.getString(res.getColumnIndex(USER_COL_EMAIL)));
            person.setGender(res.getString(res.getColumnIndex(USER_COL_GENDER)));
            person.setPhone(res.getString(res.getColumnIndex(USER_COL_PHONE)));
            person.setHobbies(res.getString(res.getColumnIndex(USER_COL_HOBBIES)));
            persons.add(person);
        }

        res.close();
        db.close();
        return persons;
    }

    public Person getCurrentUser() {
        Person person = new Person();
        String query = "SELECT * FROM " + USER_TABLE + " WHERE "
                + USER_COL_FLAG + " = '" + USER_FLAG_CURRENT + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, null);
        res.moveToFirst();
        int a = res.getCount();
        if (res.getCount() != 0) {
            person.setAddress(res.getString(res.getColumnIndex(USER_COL_ADDRESS)));
            person.setFullName(res.getString(res.getColumnIndex(USER_COL_FULLNAME)));
            person.setAge(res.getInt(res.getColumnIndex(USER_COL_AGE)));
            person.setAvatar(res.getString(res.getColumnIndex(USER_COL_AVATAR)));
            person.setEmail(res.getString(res.getColumnIndex(USER_COL_EMAIL)));
            person.setGender(res.getString(res.getColumnIndex(USER_COL_GENDER)));
            person.setPhone(res.getString(res.getColumnIndex(USER_COL_PHONE)));
            person.setHobbies(res.getString(res.getColumnIndex(USER_COL_HOBBIES)));
            person.setRegistrationID(res.getString(res.getColumnIndex(USER_COL_REGISID)));
            boolean isDatingMen = (res.getInt(res.getColumnIndex(USER_COL_ISDATINGMEN)) == 1) ? true : false;
            person.setDatingMen(isDatingMen);
            boolean isDatingWomen = (res.getInt(res.getColumnIndex(USER_COL_ISDATINGWOMEN)) == 1) ? true : false;
            person.setDatingWomen(isDatingWomen);
            person.setDatingAge(res.getInt(res.getColumnIndex(USER_COL_DATINGAGE)));
        }
        res.close();
        db.close();

        return person;
    }

    public Person getPersonByEmail(String email) {
        Person person = new Person();

        String query = "SELECT * FROM " + USER_TABLE + " WHERE "
                + USER_COL_EMAIL + " = '" + email + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, null);
        res.moveToFirst();
        int a = res.getCount();
        if (res.getCount() != 0) {
            person.setAddress(res.getString(res.getColumnIndex(USER_COL_ADDRESS)));
            person.setFullName(res.getString(res.getColumnIndex(USER_COL_FULLNAME)));
            person.setAge(Integer.parseInt(res.getString(res.getColumnIndex(USER_COL_AGE))));
            person.setAvatar(res.getString(res.getColumnIndex(USER_COL_AVATAR)));
            person.setEmail(res.getString(res.getColumnIndex(USER_COL_EMAIL)));
            person.setGender(res.getString(res.getColumnIndex(USER_COL_GENDER)));
            person.setPhone(res.getString(res.getColumnIndex(USER_COL_PHONE)));
            person.setHobbies(res.getString(res.getColumnIndex(USER_COL_HOBBIES)));
            person.setRegistrationID(res.getString(res.getColumnIndex(USER_COL_REGISID)));

            person.setDatingMen(Boolean.valueOf(res.getString(res.getColumnIndex(USER_COL_ISDATINGMEN))));
            person.setDatingWomen(Boolean.valueOf(res.getString(res.getColumnIndex(USER_COL_ISDATINGWOMEN))));
        }
        res.close();
        db.close();
        return person;
    }


    //Chat History database
    public void insertConversation(String email1, String email2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONVERSATION_COL_USER1, email1);
        contentValues.put(CONVERSATION_COL_USER2, email2);
        contentValues.put(CONVERSATION_COL_TIME, "");

        db.insert(CONVERSATION_TABLE, null, contentValues);
        db.close();
    }

    public void insertConversationMessage(String email, String message, int conversationId, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONVERSATION_DETAIL_COL_EMAIL, email);
        contentValues.put(CONVERSATION_DETAIL_COL_MESSAGE, message);
        contentValues.put(CONVERSATION_DETAIL_COL_C_ID, conversationId);
        contentValues.put(CONVERSATION_DETAIL_COL_TIME, time);

        db.insert(CONVERSATION_DETAIL_TABLE, null, contentValues);
        db.close();
    }

    public int getConservationId(String email1, String email2) {
        int result = -1;
        String query = "SELECT * FROM " + CONVERSATION_TABLE + " WHERE "
                + CONVERSATION_COL_USER1 + " = '" + email1 + "' AND "
                + CONVERSATION_COL_USER2 + " = '" + email2 + "' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, null);
        res.moveToFirst();
        int a = res.getCount();
        if (res.getCount() != 0) {
            result = res.getInt(res.getColumnIndex(CONVERSATION_COL_ID));
        }
        res.close();
        db.close();
        return result;
    }

    public List<Message> getReplyByConversationId(int conversationId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM " + CONVERSATION_DETAIL_TABLE + ", " + USER_TABLE + " U WHERE "
                + USER_COL_EMAIL + " = " + CONVERSATION_DETAIL_COL_EMAIL + " AND "
                + CONVERSATION_DETAIL_COL_C_ID + " = '" + conversationId + "' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, null);

        int a = res.getCount();
        while (res.moveToNext()) {
            String email = res.getString(res.getColumnIndex(CONVERSATION_DETAIL_COL_EMAIL));
            String type = res.getString(res.getColumnIndex(USER_COL_FLAG));
            String msg = res.getString(res.getColumnIndex(CONVERSATION_DETAIL_COL_MESSAGE));
            String time = res.getString(res.getColumnIndex(CONVERSATION_DETAIL_COL_TIME));
            String avatar = res.getString(res.getColumnIndex(USER_COL_AVATAR));
            String item = "";
            if (type.equals(USER_FLAG_CURRENT)) {
                item = Message.CHAT_ME_ITEM;
            } else {
                item = Message.CHAT_FRIEND_ITEM;
            }
            Message message = new Message(msg, avatar, time, item);
            messages.add(message);
        }
        res.close();
        db.close();
        return messages;
    }

    public void deleteConversationById(int conversationId) {
        String query = "DELETE FROM " + CONVERSATION_TABLE + " WHERE "
                + CONVERSATION_COL_ID + " = '" + conversationId + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void deleteReplyByConversationId(int conversationId) {
        String query = "DELETE FROM " + CONVERSATION_DETAIL_TABLE + " WHERE "
                + CONVERSATION_DETAIL_COL_C_ID + " = '" + conversationId + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }


    //TODO: list friend conversation
    public List<Person> getListFriendWithConversation() {
        List<Person> persons = new ArrayList<>();
        String query = "SELECT * FROM " + USER_TABLE + ", " + CONVERSATION_TABLE + " WHERE "
                + USER_COL_FLAG + " = '" + USER_FLAG_FRIENDS + "' AND "
                + USER_COL_FLAG + " = '" + USER_FLAG_FRIENDS + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery(query, null);

        while (res.moveToNext()) {
            Person person = new Person();
            person.setAddress(res.getString(res.getColumnIndex(USER_COL_ADDRESS)));
            person.setFullName(res.getString(res.getColumnIndex(USER_COL_FULLNAME)));
            person.setAge(Integer.parseInt(res.getString(res.getColumnIndex(USER_COL_AGE))));
            person.setAvatar(res.getString(res.getColumnIndex(USER_COL_AVATAR)));
            person.setEmail(res.getString(res.getColumnIndex(USER_COL_EMAIL)));
            person.setGender(res.getString(res.getColumnIndex(USER_COL_GENDER)));
            person.setPhone(res.getString(res.getColumnIndex(USER_COL_PHONE)));
            person.setHobbies(res.getString(res.getColumnIndex(USER_COL_HOBBIES)));
            persons.add(person);
        }

        res.close();
        db.close();
        return persons;
    }


}
