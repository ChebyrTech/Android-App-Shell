package com.chebyr.appshell.ui.scrollgallery;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chebyr.appshell.R;
import com.chebyr.appshell.medialoader.MediaLoader;
import com.chebyr.appshell.medialoader.MediaLoaderTask;

import java.io.File;

/**
 * Created by chebyr on 6.8.15.
 */
public class ScrollGalleryFragment extends Fragment implements
        ThumbnailAdapter.OnClickListener, MediaLoaderTask.MediaLoaderCallback
{
    public static String TAG = "ScrollGalleryFragment";
    private Context context;
    private int placeholderResId;

    // Views
    private HackyViewPager viewPager;
    private RecyclerView thumbnailScrollView;
    private ThumbnailAdapter thumbnailAdapter;
    private Fragment buttonBar;
    private FragmentManager fragmentManager;

    public ScreenSlidePagerAdapter pagerAdapter;


    private static final String IMG_LIST = "img_list";

    public ScrollGalleryFragment()
    {
        // Required empty public constructor
    }

    public static ScrollGalleryFragment newInstance(File[] imageList, FragmentManager fragmentManager)
    {
        ScrollGalleryFragment fragment = new ScrollGalleryFragment();
        fragment.fragmentManager = fragmentManager;
        Bundle args = new Bundle();
        args.putSerializable(IMG_LIST, imageList);
        fragment.setArguments(args);
        return fragment;
    }

    public void setButtonBar(Fragment buttonBar)
    {
        this.buttonBar = buttonBar;
    }

    // Listeners
    private final ViewPager.SimpleOnPageChangeListener viewPagerChangeListener = new ViewPager.SimpleOnPageChangeListener()
    {
        @Override public void onPageSelected(int position)
        {
            thumbnailScrollView.scrollToPosition(position);
        }
    };

    @Override
    public void onClick(int position)
    {
        Log.d(TAG, "Thumbnail clicked: " + position);
        viewPager.setCurrentItem(position, true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        ConstraintLayout rootView = (ConstraintLayout) inflater.inflate(R.layout.scroll_gallery_view, container, false);
        viewPager = (HackyViewPager) rootView.findViewById(R.id.viewPager);

        thumbnailScrollView = (RecyclerView) rootView.findViewById(R.id.thumbnails_scroll_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        thumbnailScrollView.setLayoutManager(layoutManager);

        thumbnailAdapter = new ThumbnailAdapter(this, this);
        thumbnailScrollView.setAdapter(thumbnailAdapter);

        // initializeViewPager
        pagerAdapter = new ScreenSlidePagerAdapter(fragmentManager, viewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerChangeListener);

        return rootView;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume()
    {
        super.onResume();
//        Bundle args = getArguments();
//        File[] imageList = (File[])args.getSerializable(IMG_LIST);
//        addMedia(imageList);
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    /**
     * Set up OnPageChangeListener for internal ViewPager
     * @param listener
     */
    public void addOnPageChangeListener(final ViewPager.OnPageChangeListener listener) {
        viewPager.clearOnPageChangeListeners();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override public void onPageSelected(int position)
            {
                thumbnailScrollView.scrollToPosition(position);
                listener.onPageSelected(position);
            }

            @Override public void onPageScrollStateChanged(int state) {
                listener.onPageScrollStateChanged(state);
            }
        });
    }

    /**
     * Set the current item displayed in the view pager.
     *
     * @param i a zero-based index
     * @return
     */
    public ScrollGalleryFragment setCurrentItem(int i) {
        viewPager.setCurrentItem(i, false);
        return this;
    }

    public int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

    private int calculateInSampleSize(int imgWidth, int imgHeight, int maxWidth, int maxHeight) {
        int inSampleSize = 1;
        while (imgWidth / inSampleSize > maxWidth || imgHeight / inSampleSize > maxHeight) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    public void addMedia(File[] imageFileList)
    {
        MediaLoaderTask mediaLoaderTask = new MediaLoaderTask(this);
        mediaLoaderTask.execute(imageFileList);
    }

    @Override
    public void onProgressUpdate(MediaLoader mediaLoader)
    {
        thumbnailAdapter.addMedia(mediaLoader);
        pagerAdapter.addMedia(mediaLoader);
    }

    @Override
    public void onPostExecute()
    {

    }
}
