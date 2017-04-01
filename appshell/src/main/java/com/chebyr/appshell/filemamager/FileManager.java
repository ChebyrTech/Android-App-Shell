package com.chebyr.appshell.filemamager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.FileObserver;

import java.io.File;

/**
 * Wrapper class for async file management
 */

public class FileManager
{
    private Context context;
    private CustomFileObserver fileObserver;
    private FileManagerCallback callback;

    public FileManager(Context context, FileManagerCallback callback)
    {
        this.context = context;
        this.callback = callback;
    }

    public void getImageList(String path, FileManagerCallback callback)
    {
        this.callback = callback;
        FileListTask fileListTask = new FileListTask();
        fileListTask.execute(path);
    }

    public void observe(String path)
    {
        fileObserver = new CustomFileObserver(path);
    }

    /**
     * Created by Administrator on 18/03/2017.
     */

    private class FileListTask extends AsyncTask<String, Void, File[]>
    {
        @Override
        protected File[] doInBackground(String... params)
        {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            //int a = Environment.getExternalStoragePublicDirectory();
            //File z= context.getExternalFilesDir(null);
            //File[] x = context.getExternalCacheDirs();
            //File[] c = context.getExternalMediaDirs();

            // getDataDir(); //API 24
            //File e= context.getFilesDir();
            //File w = context.getCacheDir();
            //getDir(name, mode);

            String path = params[0];
            File whatsAppImagePath = new File(externalStorageDirectory, path);
            return whatsAppImagePath.listFiles();
        }

        @Override
        protected void onPostExecute(File[] imageList)
        {
            super.onPostExecute(imageList);
            callback.onFileListRead(imageList);
        }
    }

    private class CustomFileObserver extends FileObserver
    {
        CustomFileObserver(String path)
        {
            super(path, FileObserver.CREATE | FileObserver.DELETE);
        }

        @Override
        public void onEvent(int event, String path)
        {
            switch(event)
            {
                case FileObserver.CREATE:
                {
                    callback.onFileCreated(path);
                }
                case FileObserver.DELETE:
                {
                    callback.onFileDeleted(path);
                }
            }
        }
    }

    public interface FileManagerCallback
    {
        void onFileListRead(File[] files);
        void onFileCreated(String path);
        void onFileDeleted(String path);
    }
}
