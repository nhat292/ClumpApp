package com.ck.clump.ui.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ck.clump.ui.activity.MainActivity;
import com.ck.clump.ui.activity.fragment.ChatsFragment;
import com.ck.clump.ui.activity.fragment.ContactsFragment;
import com.ck.clump.ui.activity.fragment.EventsFragment;
import com.ck.clump.ui.activity.fragment.SettingsFragment;

public class MainAdapter extends FragmentPagerAdapter {
    private ContactsFragment contactsFragment;
    private ChatsFragment chatsFragment;
    private EventsFragment eventsFragment;

    public MainAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                contactsFragment =  ContactsFragment.newInstance();
                return contactsFragment;
            case 1:
                chatsFragment =  ChatsFragment.newInstance();
                return chatsFragment;
            case 2:
                eventsFragment = EventsFragment.newInstance();
                return eventsFragment;
            case 3:
                return SettingsFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return MainActivity.ITEMS;
    }

    public ContactsFragment getContactsFragment() {
        return contactsFragment;
    }

    public ChatsFragment getChatsFragment() {
        return chatsFragment;
    }

    public EventsFragment getEventsFragment() {
        return eventsFragment;
    }
}
