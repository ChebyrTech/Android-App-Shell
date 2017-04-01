package com.chebyr.appshell.medialoader;

import android.os.AsyncTask;

import java.io.File;

/**
 * Async media loader task
 */
public class MediaLoaderTask extends AsyncTask<File[], MediaLoader, Boolean>
{
    private MediaLoaderCallback callback;

    public MediaLoaderTask(MediaLoaderCallback callback)
    {
        this.callback = callback;
    }

    //@Override
    protected Boolean doInBackground(File[]... params)
    {
        File[] imageFileList = params[0];
        if (imageFileList == null)
        {
            throw new NullPointerException("imageFileList should not be null!");
        }

        for (File imageFile : imageFileList)
        {
            MediaLoader mediaLoader = new MediaLoader(imageFile);
            publishProgress(mediaLoader);
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(MediaLoader... values)
    {
        super.onProgressUpdate(values);
        callback.onProgressUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        super.onPostExecute(aBoolean);
        callback.onPostExecute();
    }

    public interface MediaLoaderCallback
    {
        void onProgressUpdate(MediaLoader mediaLoader);
        void onPostExecute();
    }
}
