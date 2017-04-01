package com.chebyr.appshell.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Wrapper class for database management
 */

public class DatabaseManager extends SQLiteOpenHelper
{
    ArrayList<Table> tableList;

    public SQLiteDatabase readableDatabase;
    public SQLiteDatabase writableDatabase;

    public DatabaseManager(Context context, String databaseName, int databaseVersion)
    {
        super(context, databaseName, null, databaseVersion);

        tableList = new ArrayList<>();
        readableDatabase = getReadableDatabase();
        writableDatabase = getWritableDatabase();
    }

    public Table createTable(Class<? extends Table> tableClass)
    {
        try
        {
            Table table = tableClass.newInstance();
            table.setDatabaseManager(this);
            tableList.add(table);

            return table;
        }
        catch (InstantiationException e)
        {
            return null;
        }
        catch (IllegalAccessException e)
        {
            return null;
        }
    }

    public void onCreate(SQLiteDatabase db)
    {
        for(Table table:tableList)
            table.createTable();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over

        for(Table table:tableList)
            table.deleteTable();

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }
}