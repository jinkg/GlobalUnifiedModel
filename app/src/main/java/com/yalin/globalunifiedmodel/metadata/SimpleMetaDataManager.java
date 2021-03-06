package com.yalin.globalunifiedmodel.metadata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * 作者：YaLin
 * 日期：2016/10/26.
 */

public class SimpleMetaDataManager implements MetaDataManager {
    private Context mContext;
    private DatabaseHelper mDbHelper;
    private final Object mLock = new Object();

    @Override
    public void close() {
        mDbHelper.close();
    }

    interface Tables {
        String USERS = "users";
    }

    public SimpleMetaDataManager(Context context) {
        this(context, null);
    }

    SimpleMetaDataManager(Context context, String filename) {
        mContext = context;
        mDbHelper = new DatabaseHelper(context, filename);
    }

    @Override
    public long login(UserModel user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserColumns.USER_NAME, user.name);
        contentValues.put(UserColumns.USER_AGE, user.age);
        synchronized (mLock) {
            final SQLiteDatabase db = mDbHelper.getWritableDatabase();
            return db.insert(Tables.USERS, null, contentValues);
        }
    }

    @Override
    public void update(UserModel newUser) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserColumns.USER_NAME, newUser.name);
        contentValues.put(UserColumns.USER_AGE, newUser.age);
        synchronized (mLock) {
            final SQLiteDatabase db = mDbHelper.getReadableDatabase();
            db.update(Tables.USERS, contentValues, BaseColumns._ID + " = ?",
                    new String[]{String.valueOf(newUser.id)});
        }
    }

    @Override
    public void exit() {
        synchronized (mLock) {
            final SQLiteDatabase db = mDbHelper.getReadableDatabase();
            db.execSQL("DELETE FROM " + Tables.USERS);
        }
    }

    @Override
    public UserModel getLoginUser() {
        synchronized (mLock) {
            final SQLiteDatabase db = mDbHelper.getReadableDatabase();

            Cursor cursor = null;
            try {
                cursor = db.query(Tables.USERS, UserColumns.GET_COLUMNS, null, null,
                        null, null, BaseColumns._ID + " DESC");

                if (cursor.moveToNext()) {
                    return createUserFromCursor(cursor);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return null;
        }
    }

    private UserModel createUserFromCursor(Cursor cursor) {
        long id = cursor.getLong(0);
        UserModel user = new UserModel(id);
        user.name = cursor.getString(1);
        user.age = cursor.getInt(2);
        return user;
    }

    public interface UserColumns {
        String USER_NAME = "user_name";
        String USER_AGE = "user_age";

        String[] GET_COLUMNS = new String[]{
                BaseColumns._ID,
                USER_NAME,
                USER_AGE
        };
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "main.db";

        DatabaseHelper(Context context, String filename) {
            super(context, filename != null ? filename : DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createUserTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            int version = oldVersion;
            if ((version == 1 || version == 2) && version < newVersion) {
                // do upgrade 1->3 or 2->3
                version = 3;
            }
            if (version == 3 && version < newVersion) {
                // do upgrade 3->4
                version = 4;
            }
        }

        private void createUserTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + Tables.USERS + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + UserColumns.USER_NAME + " TEXT NOT NULL,"
                    + UserColumns.USER_AGE + " INTEGER,"
                    + "UNIQUE (" + UserColumns.USER_NAME + ") ON CONFLICT REPLACE)");
        }
    }
}
