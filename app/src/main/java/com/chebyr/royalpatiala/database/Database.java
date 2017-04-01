package com.chebyr.royalpatiala.database;

import android.content.Context;

import com.chebyr.appshell.database.DatabaseManager;
import com.chebyr.appshell.database.Table;

/**
 * Database manager for app
 */

public class Database
{
    private DatabaseManager databaseManager;

    public Database(Context context)
    {
        databaseManager = new DatabaseManager(context, DatabaseProps.DATABASE_NAME, DatabaseProps.DATABASE_VERSION);
        Table tableCatalog = databaseManager.createTable(CatalogTable.class);
        Table tableStock = databaseManager.createTable(StockTable.class);
        Table tableOrder = databaseManager.createTable(OrderTable.class);

    }

    /**
     * Created by Administrator on 27/03/2017.
     */

    private interface DatabaseProps
    {
        // If you change the database schema, you must increment the database version.
        int DATABASE_VERSION = 1;
        String DATABASE_NAME = "RoyalPatiala.db";
    }
}
