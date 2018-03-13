package com.ck.clump.ui.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ck.clump.R;
import com.ck.clump.ui.activity.MainActivity;
import com.ck.clump.ui.activity.model.GroupModel;
import com.ck.clump.ui.activity.model.UserModel;
import com.ck.clump.model.response.ContactResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.FriendInfoActivity;
import com.ck.clump.ui.activity.GroupInfoActivity;
import com.ck.clump.ui.activity.adapter.StickyContractHeadersAdapter;
import com.ck.clump.ui.activity.model.Contact;
import com.ck.clump.ui.presenter.fragment.ContactsFragmentPresenter;
import com.ck.clump.ui.view.fragment.ContactsFragmentView;
import com.google.gson.Gson;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

public class ContactsFragment extends BaseFragment implements ContactsFragmentView {

    public static final int REQUEST_GROUP_INFO = 1;

    @Inject
    public Service service;

    private Context mContext;
    private List<Contact> mContacts;
    private List<Contact> mOriginContacts;
    private StickyContractHeadersAdapter mAdapter;
    private ContactsFragmentPresenter presenter;

    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @Bind(R.id.rvItems)
    RecyclerView rvItems;
    @Bind(R.id.imvEmpty)
    ImageView imvEmpty;

    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        mContext = getActivity();
        baseActivity.getDeps().injectContactsFragment(this);
        presenter = new ContactsFragmentPresenter(service, this);
        ButterKnife.bind(this, view);
        setupSwipeRefreshLayout();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setupSwipeRefreshLayout() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity activity = (MainActivity) getActivity();
                activity.checkContactPermission();
            }
        });
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary);

        setupRecyclerView();
        getLocalData();
        presenter.getContactsList("CONTACT", false, false);
    }

    private void setupRecyclerView() {
        mContacts = new ArrayList<>();
        mOriginContacts = new ArrayList<>();

        mAdapter = new StickyContractHeadersAdapter(mContext, mContacts, new StickyContractHeadersAdapter.OnItemClickListener() {
            @Override
            public void onClick(Contact Item, boolean isInfoClick) {
                if (isInfoClick) {
                    if (Item.getType() == Contact.TYPE_GROUP) {
                        Intent intentGroup = new Intent(getActivity(), GroupInfoActivity.class);
                        intentGroup.putExtra("groupId", Item.getId());
                        startActivityForResult(intentGroup, REQUEST_GROUP_INFO);
                    } else {
                        Intent intentFriend = new Intent(getActivity(), FriendInfoActivity.class);
                        intentFriend.putExtra("userId", Item.getId());
                        startActivity(intentFriend);
                    }
                } else {

                }
            }
        }, false);
        rvItems.setAdapter(mAdapter);

        // Set layout manager
        rvItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        rvItems.addItemDecoration(headersDecor);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        // Add touch listeners
        StickyRecyclerHeadersTouchListener touchListener =
                new StickyRecyclerHeadersTouchListener(rvItems, headersDecor);
        touchListener.setOnHeaderClickListener(
                new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
                    @Override
                    public void onHeaderClick(View header, int position, long headerId) {
                    }
                });
        rvItems.addOnItemTouchListener(touchListener);
    }

    public void searchContent(String content) {
        mContacts.clear();
        for (Contact contact : mOriginContacts) {
            if (contact.getName().toLowerCase().contains(content.toLowerCase())) {
                mContacts.add(contact);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showWait() {
        swipeContainer.setRefreshing(true);
    }

    @Override
    public void removeWait() {
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onFailure(String appErrorMessage, boolean finishActivity) {
        baseActivity.showErrorMessage(appErrorMessage, finishActivity);
    }

    @Override
    public void onSuccess(Object o) {
        ContactResponse response = (ContactResponse) o;
        final List<GroupModel> lstGroup = response.getDATA().getGroups();
        final List<UserModel> lstUser = response.getDATA().getUsers();
        baseActivity.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createOrUpdateAllFromJson(GroupModel.class, new Gson().toJson(lstGroup));
                realm.createOrUpdateAllFromJson(UserModel.class, new Gson().toJson(lstUser));
            }
        });
        showList(lstGroup, lstUser);
    }

    @Override
    public void logout() {
        baseActivity.clearDataAndLogout();
    }

    private void getLocalData() {
        List<GroupModel> lstGroup = baseActivity.getRealm()
                .where(GroupModel.class)
                .findAll();
        List<UserModel> lstUser = baseActivity.getRealm()
                .where(UserModel.class)
                .findAll();
        showList(lstGroup, lstUser);
    }

    private void showList(List<GroupModel> lstGroup, List<UserModel> lstUser) {
        mOriginContacts.clear();
        mContacts.clear();
        for (GroupModel item : lstGroup) {
            mOriginContacts.add(new Contact(item.getId(), item.getImagePath(), item.getName(), Contact.TYPE_GROUP));
        }
        for (UserModel item : lstUser) {
            mOriginContacts.add(new Contact(item.getId(), item.getAvatarPath(), item.getDisplayName(), Contact.TYPE_PERSONAL));
        }

        for (Contact contact : mOriginContacts) {
            mContacts.add(contact);
        }
        if (mOriginContacts.size() > 0) {
            rvItems.setVisibility(View.VISIBLE);
            imvEmpty.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        } else {
            rvItems.setVisibility(View.GONE);
            imvEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != getActivity().RESULT_OK) return;
        if (requestCode == REQUEST_GROUP_INFO) {
            presenter.getContactsList("CONTACT", true, true);
        }
    }

    public void getListContact() {
        presenter.getContactsList("CONTACT", true, false);
    }
}
