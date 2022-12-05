package com.pagarplus.app.appcomponents.ui;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.pagarplus.app.R;
import com.pagarplus.app.network.models.userdashboard.UserBannerResponseItem;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SlideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SlideFragment extends Fragment {
    public static final String IMAGE_RESOURCE_ID = "image_resource_id";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String Url;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String ARG_SECTION_NUMBER = "section_number";
    public List<UserBannerResponseItem> PAGE_IMAGE;
    /*private int[] PAGE_IMAGE ;
            new int[] {
                    R.drawable.offer1, R.drawable.offer2, R.drawable.offer3
            };*/
    private SliderViewModel sliderViewModel;

    public SlideFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SlideFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SlideFragment newInstance(String param1, String param2) {
        SlideFragment fragment = new SlideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public  SlideFragment newInstance(int index, List<UserBannerResponseItem> list) {
        SlideFragment fragment = new SlideFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        PAGE_IMAGE = list;
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        sliderViewModel = ViewModelProviders.of(this).get(SliderViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        sliderViewModel.setIndex(index);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_slide, container, false);
        ImageView imageView = root.findViewById(R.id.imageUrl);
        Url = getArguments().getString(IMAGE_RESOURCE_ID);
        Log.e("imageurl","url iss..."+Url);
       // CustomBindingAdapterKt.loadImageFromNetwork(imageView,Url,getResources().getDrawable(R.drawable.image_not_found),getResources().getDrawable(R.drawable.image_not_found),1f,false);
        Glide.with(this).load(getResources().getDrawable(R.drawable.image_not_found)).into(imageView);
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}