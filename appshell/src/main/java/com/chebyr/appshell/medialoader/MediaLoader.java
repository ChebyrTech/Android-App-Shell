package com.chebyr.appshell.medialoader;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chebyr.appshell.R;

import java.io.File;

/**
 * Media Info contains the information required to load and display the media in the gallery.
 */
public class MediaLoader
{
    private static String TAG = "MediaLoader";
    private File file;

    public MediaLoader(File imgPath) {
        this.file = imgPath;
    }

    /**
     * @return true if implementation load's image, otherwise false
     */

    public boolean isImage() {
        return true;
    }

    /**
     * Loads image and sets it to imageView. After that implementation can call callback to set imageView's
     * scale type to ScaleType.FIT_CENTER.
     */

    public void loadMedia(Fragment fragment, final ImageView imageView, final SuccessCallback callback)
    {
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        Glide.with(fragment)
                .load(file)
                .asBitmap()
                .override(width, height)
                .fitCenter()
                .placeholder(R.mipmap.placeholder_image)
                .into(new GlideCallback(imageView, width, height, callback));
    }

    /**
     * Loads thumbnail image and sets it to thumbnailView. After that implementation can call callback
     * to set thumbnailView's scale type to ScaleType.FIT_CENTER.
     */

    public void loadThumbnail(Fragment fragment, ImageView thumbnailView, SuccessCallback callback)
    {
        ViewGroup.LayoutParams layoutParams = thumbnailView.getLayoutParams();
        int width = thumbnailView.getWidth();
        int height = thumbnailView.getHeight();

        if(width <= 0)
            width = layoutParams.width;

        if(height <= 0)
            height = layoutParams.height;
        //Log.d(TAG, "loadThumbnail layoutParams width: " + layoutParams.width + " height: " + layoutParams.height);
        //Log.d(TAG, "loadThumbnail width: " + width + " height: " + height);

        Glide.with(fragment)
                .load(file)
                .asBitmap()
                .override(width, height)
                .placeholder(R.mipmap.placeholder_image)
                .fitCenter()
                .thumbnail(0.1f)
                .into(new GlideCallback(thumbnailView, width, height, callback));
    }

    /**
     * Implementation may call this callback for report to imageView, what it's image was changed
     */

    public interface SuccessCallback {
        void onSuccess();
    }

    private class GlideCallback extends SimpleTarget<Bitmap>
    {
        SuccessCallback successCallback;
        ImageView imageView;

        public GlideCallback(ImageView imageView, int width, int height, SuccessCallback successCallback)
        {
            super(width, height);
            this.imageView = imageView;
            this.successCallback = successCallback;
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation)
        {
            imageView.setImageBitmap(resource);
            successCallback.onSuccess();
        }
    }
}
