package com.chebyr.appshell.database;

/**
 * The base Interface for accessing Tables
 */

public interface Table
{
    void setDatabaseManager(DatabaseManager databaseManager);
    void createTable();
    void deleteTable();

    String getTablePrimaryKey();
    String getTableName();
    String getCreateTableQuery();
    String getDeleteTableQuery();
}
