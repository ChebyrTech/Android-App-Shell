package com.chebyr.appshell.ui.scrollgallery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.chebyr.appshell.medialoader.MediaLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chebyr on 29.8.15.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
{
    private static String TAG = "ScreenSlidePagerAdapter";
    private HackyViewPager viewPager;
    private List<MediaLoader> mListOfMedia;

    public ScreenSlidePagerAdapter(FragmentManager fragmentManager, HackyViewPager viewPager)
    {
        super(fragmentManager);
        this.viewPager = viewPager;
        this.mListOfMedia = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position)
    {
        Log.d(TAG, "getItem: " + position);
        DetailImageFragment fragment = null;
        if (position < mListOfMedia.size())
        {
            MediaLoader mediaLoader = mListOfMedia.get(position);
            fragment = DetailImageFragment.newInstance(mediaLoader, viewPager);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mListOfMedia.size();
    }

    public void addMedia(MediaLoader mediaInfo)
    {
        if (mediaInfo == null) {
            throw new NullPointerException("Infos may not be null!");
        }

        mListOfMedia.add(mediaInfo);
        notifyDataSetChanged();
    }
}
