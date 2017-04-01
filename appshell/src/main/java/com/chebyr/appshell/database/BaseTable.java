package com.chebyr.appshell.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class with basic table access implementation
 */

public abstract class BaseTable implements Table
{
    protected DatabaseManager databaseManager;

    public BaseTable()
    {
    }

    @Override
    public void setDatabaseManager(DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

    @Override
    public void createTable()
    {
        SQLiteDatabase db = databaseManager.getReadableDatabase();
        String query = getCreateTableQuery();
        db.execSQL(query);
    }

    @Override
    public void deleteTable()
    {
        SQLiteDatabase db = databaseManager.getReadableDatabase();
        String query = getDeleteTableQuery();
        db.execSQL(query);
    }

    // Put Information into a Database
    public void insertInto(ContentValues values)
    {
        String tableName = getTableName();
        // Insert data into the database by passing a ContentValues object to the insert() method:
        // Gets the data repository in write mode

        SQLiteDatabase writableDatabase = databaseManager.getWritableDatabase();
        // Insert the new row, returning the primary key value of the new row
        long newRowId = writableDatabase.insert(tableName, null, values);

    }

    // Read Information from a Database
    public List query(String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        String tableName = getTableName();

        SQLiteDatabase readableDatabase = databaseManager.getReadableDatabase();
        Cursor cursor = readableDatabase.query(
                tableName,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        String primaryKey = getTablePrimaryKey();
        ArrayList itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(primaryKey));
            itemIds.add(itemId);
        }
        cursor.close();
        return itemIds;
    }

    // Delete Information from a Database
    public void delete(String selection, String[] selectionArgs)
    {
        String tableName = getTableName();
        // Issue SQL statement.
        SQLiteDatabase readableDatabase = databaseManager.getReadableDatabase();
        readableDatabase.delete(tableName, selection, selectionArgs);
    }

    // Update Table
    public void updateTable(ContentValues values, String selection, String[] selectionArgs)
    {
        String tableName = getTableName();

        SQLiteDatabase readableDatabase = databaseManager.getReadableDatabase();
        int count = readableDatabase.update(
                tableName,
                values,
                selection,
                selectionArgs);
    }
}
