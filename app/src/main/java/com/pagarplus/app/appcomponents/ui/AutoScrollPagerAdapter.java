package com.pagarplus.app.appcomponents.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pagarplus.app.network.models.createcreatebanner.FetchGetAdvertiseListResponseListItem;
import com.pagarplus.app.network.models.userdashboard.UserBannerResponseItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AutoScrollPagerAdapter extends FragmentPagerAdapter {
    List<FetchGetAdvertiseListResponseListItem> getBanners;
    SlideFragment slideFragment;
    public AutoScrollPagerAdapter(FragmentManager fm, List<FetchGetAdvertiseListResponseListItem> bannersList) {
        super(fm);
        getBanners = bannersList;
    }

    @NotNull
    @Override
    public SlideFragment getItem(int position) {
        // Return a SlideFragment (defined as a static inner class below).
        Log.e("BannerList","in adapter"+getBanners.toString());
        Bundle bundle = new Bundle();
        bundle.putString(SlideFragment.IMAGE_RESOURCE_ID, getBanners.get(position).getBannerImage());
        FetchGetAdvertiseListResponseListItem Banners = getBanners.get(position);
        bundle.putSerializable("object",Banners);
        SlideFragment frag =new SlideFragment();
        frag.setArguments(bundle);
        return frag;
    }
    @Override
    public int getCount() {
        return (getBanners == null) ? 0: getBanners.size();
    }
}
