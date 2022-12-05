package com.pagarplus.app.modules.expensereport.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pagarplus.app.appcomponents.ui.SlideFragment;
import com.pagarplus.app.network.models.expense.ImageItems;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AutoScrollPagerAdapter extends FragmentPagerAdapter {
    ArrayList<ImageItems> getImages;
    SlideFragment slideFragment;
    public AutoScrollPagerAdapter(FragmentManager fm, ArrayList<ImageItems> imagesList) {
        super(fm);
        getImages = imagesList;
    }
    @NotNull
    @Override
    public SlideFragment getItem(int position) {
        // Return a SlideFragment (defined as a static inner class below).
        Log.e("BannerList","in adapter"+getImages.toString());
        Bundle bundle = new Bundle();
        bundle.putString(SlideFragment.IMAGE_RESOURCE_ID, getImages.get(position).getBillImageUrl());
        ImageItems Banners = getImages.get(position);
        bundle.putSerializable("object",Banners);
        SlideFragment frag =new SlideFragment();
        frag.setArguments(bundle);
        return frag;
    }
    @Override
    public int getCount() {
        return (getImages == null) ? 0: getImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }
}
