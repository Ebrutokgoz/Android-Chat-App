package com.example.chatapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAccessAdapter extends FragmentPagerAdapter {

    public TabAccessAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            case 2:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;   //Kaç tabımız olduğunu döndürür
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Mesajlar";
            case 1:
                return "Arkadaşlar";
            case 2:
                return "İstekler";
            default:
                return null;
        }
    }
}
