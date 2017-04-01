package com.chebyr.royalpatiala.database;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.chebyr.appshell.database.BaseTable;

import java.util.List;

/**
 * Wrapper class for Catalog Table access
 */

public class CatalogTable extends BaseTable
{
    @Override
    public String getTablePrimaryKey()
    {
        return Table.Columns._ID;
    }

    @Override
    public String getTableName()
    {
        return Table.Columns.TABLE_NAME;
    }

    @Override
    public String getCreateTableQuery()
    {
        return Table.CREATE_TABLE;
    }

    @Override
    public String getDeleteTableQuery()
    {
        return Table.DELETE_TABLE;
    }

    // Put Information into a Database
    public void write(String title, String subtitle)
    {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Table.Columns.COLUMN_NAME_TITLE, title);
        values.put(Table.Columns.COLUMN_NAME_SUBTITLE, subtitle);

        insertInto(values);
    }

    // Read Information from a Database
    public List read(String title)
    {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                Table.Columns._ID,
                Table.Columns.COLUMN_NAME_TITLE,
                Table.Columns.COLUMN_NAME_SUBTITLE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = Table.Columns.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { title };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                Table.Columns.COLUMN_NAME_SUBTITLE + " DESC";

        return query(projection, selection, selectionArgs, sortOrder);
    }



    // Delete Information from a Database
    public void deleteRows(String title)
    {
        // Define 'where' part of query.
        String selection = Table.Columns.COLUMN_NAME_TITLE + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { title };

        delete(selection, selectionArgs);
    }


    // Update Table
    public void update(String title, String titleNew)
    {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(Table.Columns.COLUMN_NAME_TITLE, titleNew);

        // Which row to update, based on the title
        String selection = Table.Columns.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = { title };

        updateTable(values, selection, selectionArgs);
    }

    /**
     * Created by Administrator on 15/03/2017.
     */

    private interface Table
    {
        String CREATE_TABLE =
                "CREATE TABLE " + Columns.TABLE_NAME + " (" +
                        Columns._ID + " INTEGER PRIMARY KEY," +
                        Columns.COLUMN_NAME_TITLE + " TEXT," +
                        Columns.COLUMN_NAME_SUBTITLE + " TEXT)";

        String DELETE_TABLE = "DROP TABLE IF EXISTS " + Columns.TABLE_NAME;

        /* Inner class that defines the table contents */
        class Columns implements BaseColumns
        {
            static final String TABLE_NAME = "entry";
            static final String COLUMN_NAME_TITLE = "title";
            static final String COLUMN_NAME_SUBTITLE = "subtitle";
        }
    }
}
