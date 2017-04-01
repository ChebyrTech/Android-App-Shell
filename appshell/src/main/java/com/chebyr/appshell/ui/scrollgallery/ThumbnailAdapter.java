package com.chebyr.appshell.ui.scrollgallery;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chebyr.appshell.medialoader.MediaLoader;
import com.chebyr.appshell.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 24/03/2017.
 */

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder>
{
    private List<MediaLoader> mListOfMedia;
    private Fragment parentFragment;
    private OnClickListener callback;

    public ThumbnailAdapter(Fragment parentFragment, OnClickListener callback)
    {
        this.parentFragment = parentFragment;
        this.mListOfMedia = new ArrayList<>();
        this.callback = callback;
    }

    @Override
    public ThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.thumbnail_layout, parent, false);

        return new ThumbnailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ThumbnailViewHolder holder, final int position)
    {
        final MediaLoader mediaLoader = mListOfMedia.get(position);
        holder.thumbnailView.setOnClickListener(holder);
        holder.thumbnailView.setTag(position);

        holder.thumbnailView.post(new Runnable()
        {
            @Override
            public void run()
            {
                mediaLoader.loadThumbnail(parentFragment, holder.thumbnailView, holder);
            }
        });
    }

    @Override
    public int getItemCount()
    {
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

    public class ThumbnailViewHolder extends RecyclerView.ViewHolder
            implements MediaLoader.SuccessCallback, View.OnClickListener
    {
        public ImageView thumbnailView;

        public ThumbnailViewHolder(View view)
        {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail_view);
        }

        @Override
        public void onSuccess()
        {

        }

        @Override
        public void onClick(View v)
        {
            int position = (int)v.getTag();
            callback.onClick(position);
        }
    }



    public interface OnClickListener
    {
        void onClick(int position);
    }

}