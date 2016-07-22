package com.inveitix.android.compass.database.adapters;

import android.content.ContentValues;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.inveitix.android.compass.database.DatabaseInstance;
import com.inveitix.android.compass.database.models.Model;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDatabaseAdapter<T extends Model>
        implements DatabaseAdapter<T>, ModelBuilder<T> {

    private static final String TAG = "BaseDatabaseAdapter";

    private static final String ID = "_id";

    private DatabaseInstance databaseInstance;

    public BaseDatabaseAdapter(Context context) {
        this.databaseInstance = DatabaseInstance.getInstance(context);
    }

    protected DatabaseInstance getDatabaseInstance() {
        return databaseInstance;
    }

    @Override
    public T findById(int id) {
        T model = null;
        String[] args = {String.valueOf(id)};
        Cursor cursor = this.query(null, ID + "=?", args, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                model = this.buildModelFromCursor(cursor);
            }
            cursor.close();
        }
        return model;
    }

    @Override
    public List<T> findAll(String sortOrder) {
        Cursor cursor = this.query(null, null, null, sortOrder, null);
        return this.buildModelsFromCursorAndClose(cursor);
    }

    @Override
    public void insert(T model) {
        try {
            ContentValues contentValues = this.buildModelContentValues(model);
            int id = (int) this.insert(contentValues);
            if (id > 0) {
                model.setId(id);
            }
        } catch (SQLiteConstraintException e) {
            Log.e(TAG, "Constraint exception occurs while inserting a " + model.getClass().getSimpleName() + " object");
        } catch (Exception e) {
            Log.e(TAG, "Exception occurs while inserting a " + model.getClass().getSimpleName() + " object");
        }
    }

    @Override
    public void update(T model) {
        String[] args = {String.valueOf(model.getId())};
        ContentValues contentValues = this.buildModelContentValues(model);
        this.update(contentValues, ID + "=?", args);
    }

    @Override
    public void delete(int id) {
        String[] args = {String.valueOf(id)};
        this.delete(ID + "=?", args);
    }

    protected Cursor query(String[] columns,
                           String whereClause, String[] whereArgs, String sortOrder, String limit) {
        return getDatabase().query(getTableName(), columns, whereClause, whereArgs, null, null, sortOrder, limit);
    }

    protected int update(ContentValues values,
                         String whereClause, String[] whereArgs) {
        return getDatabase().update(getTableName(), values, whereClause, whereArgs);
    }

    protected int delete(String whereClause, String[] whereArgs) {
        return getDatabase().delete(getTableName(), whereClause, whereArgs);
    }

    protected long insert(ContentValues values) {
        return getDatabase().insertOrThrow(getTableName(), "", values);
    }

    protected Cursor rawQuery(String sql, String[] whereArgs) {
        return getDatabase().rawQuery(sql, whereArgs);
    }

    private SQLiteDatabase getDatabase() {
        if (this.databaseInstance.getDatabase() == null) {
            throw new NullPointerException("You have to open the db connection first");
        }
        return this.databaseInstance.getDatabase();
    }

    @NonNull
    @Override
    public List<T> buildModelsFromCursorAndClose(Cursor cursor) {
        List<T> models = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    T model = buildModelFromCursor(cursor);
                    models.add(model);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return models;
    }

    // Abstract methods:

    @Override
    public abstract T buildModelFromCursor(Cursor cursor);

    protected abstract ContentValues buildModelContentValues(T model);

    protected abstract String getTableName();


}

