package com.inveitix.android.compass.database;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;

import com.inveitix.android.compass.database.adapters.LocationDbAdapter;

public class DatabaseInstance {

    private static DatabaseInstance instance;

    public static DatabaseInstance getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseInstance.class) {
                if (instance == null) {
                    instance = new DatabaseInstance(context);
                    instance.open();
                }
            }
        }

        return instance;
    }

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private DatabaseInstance(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    private void open() throws SQLException {
        this.database = this.databaseHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDatabase() {
        return this.database;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "compass.db";
        private static final int DATABASE_VERSION = 19;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
            db.setForeignKeyConstraintsEnabled(true);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        private void createTables(SQLiteDatabase db) {
            db.execSQL(LocationDbAdapter.CREATE_TABLE_QUERY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            resetTables(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            resetTables(db);
        }

        protected void resetTables(SQLiteDatabase db) {
            db.execSQL(LocationDbAdapter.DROP_TABLE_QUERY);
            createTables(db);
        }
    }
}

