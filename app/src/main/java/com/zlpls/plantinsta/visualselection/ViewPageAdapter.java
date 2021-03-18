package com.zlpls.plantinsta.visualselection;

//********https://www.youtube.com/watch?v=oBhgyiBVd3k

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPageAdapter extends FragmentPagerAdapter {
    //1
    private final List<Fragment> lstFragment = new ArrayList<>();
    private final List<String> lstTitles = new ArrayList<>();

    public ViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public ViewPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public CharSequence getPageTitleTitle(int position){
        return lstTitles.get(position);

    }
    public void AddFragment(Fragment fragment,String title){
        lstFragment.add(fragment);
        lstTitles.add(title);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return lstFragment.get(position);
    }


    @Override
    public int getCount() {
        return lstTitles.size();
    }
}
