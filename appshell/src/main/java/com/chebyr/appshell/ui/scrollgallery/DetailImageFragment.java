package com.chebyr.appshell.ui.scrollgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chebyr.appshell.medialoader.MediaLoader;
import com.chebyr.appshell.R;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by chebyr on 29.8.15.
 */
public class DetailImageFragment extends Fragment
{
    private Context context;
    private MediaLoader mMediaLoader;

    private HackyViewPager viewPager;
    private ImageView detailImageView;
    private PhotoViewAttacher photoViewAttacher;

    public static final String IS_LOCKED = "isLocked";
    public static final String IMAGE = "image";

    public DetailImageFragment()
    {
    }

    public static DetailImageFragment newInstance(MediaLoader mediaLoader, HackyViewPager viewPager)
    {
        DetailImageFragment fragment = new DetailImageFragment();
        fragment.mMediaLoader = mediaLoader;
        fragment.viewPager = viewPager;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.detail_image_fragment, container, false);
        detailImageView = (ImageView) rootView.findViewById(R.id.detail_image_view);

        detailImageView.post(new Runnable()
        {
            @Override
            public void run()
            {
                int h = detailImageView.getHeight();

                if (mMediaLoader != null) {
                    mMediaLoader.loadMedia(DetailImageFragment.this, detailImageView, new MediaLoader.SuccessCallback() {
                        @Override
                        public void onSuccess() {
                            photoViewAttacher = new PhotoViewAttacher(detailImageView);
                        }
                    });
                }
            }
        });

        loadInstanceState(savedInstanceState);
        return rootView;
    }

    public void loadInstanceState(Bundle savedInstanceState)
    {
        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(IS_LOCKED, false);
            viewPager.setLocked(isLocked);
            if (savedInstanceState.containsKey(IMAGE)) {
                detailImageView.setImageBitmap((Bitmap) savedInstanceState.getParcelable(IMAGE));
            }
            photoViewAttacher = new PhotoViewAttacher(detailImageView);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if (isViewPagerActive()) {
            outState.putBoolean(IS_LOCKED, viewPager.isLocked());
        }
        if (isBackgroundImageActive()) {
            outState.putParcelable(IMAGE, ((BitmapDrawable) detailImageView.getDrawable()).getBitmap());
        }
    }

    private boolean isViewPagerActive() {
        return viewPager != null;
    }

    private boolean isBackgroundImageActive() {
        return detailImageView != null && detailImageView.getDrawable() != null;
    }
}
