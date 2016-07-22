package com.inveitix.android.compass.database.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.inveitix.android.compass.database.models.LocationModel;

public class LocationDbAdapter extends BaseDatabaseAdapter<LocationModel>
        implements DatabaseAdapter<LocationModel>, ModelBuilder<LocationModel> {

    public static final String ID = "_id";
    public static final String TIMESTAMP = "timestamp";
    public static final String NORTH_OFFSET = "north_offset";

    public static final String DATABASE_TABLE = "locations";

    public static final String CREATE_TABLE_QUERY =
            "CREATE TABLE " + DATABASE_TABLE + " ("
                    + ID + " INTEGER PRIMARY KEY, "
                    + TIMESTAMP + " INTEGER, "
                    + NORTH_OFFSET + " REAL);";

    public static final String DROP_TABLE_QUERY =
            "DROP TABLE IF EXISTS " + DATABASE_TABLE;

    public LocationDbAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getTableName() {
        return DATABASE_TABLE;
    }

    // Abstract methods implementations:

    @Override
    public LocationModel buildModelFromCursor(Cursor cursor) {
        LocationModel locationModel = null;

        if (cursor != null) {
            locationModel = new LocationModel();
            locationModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
            locationModel.setNorthOffset(cursor.getFloat(cursor.getColumnIndexOrThrow(NORTH_OFFSET)));
            locationModel.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(TIMESTAMP)));
        }

        return locationModel;
    }

    @Override
    public ContentValues buildModelContentValues(LocationModel model) {
        ContentValues values = new ContentValues();

        if (model.getId() > 0) {
            values.put(ID, model.getId());
        }
        values.put(NORTH_OFFSET, model.getNorthOffset());
        values.put(TIMESTAMP, model.getTimestamp());

        return values;
    }
}
