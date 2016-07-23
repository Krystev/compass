package com.inveitix.android.compass.database.adapters;

import android.database.Cursor;

import java.util.List;

public interface ModelBuilder<T> {

    T buildModelFromCursor(Cursor cursor);

    List<T> buildModelsFromCursorAndClose(Cursor cursor);
}
