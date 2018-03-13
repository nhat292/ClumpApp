package com.ck.clump.ui.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ck.clump.R;
import com.ck.clump.enums.EventStatus;
import com.ck.clump.ui.activity.model.EventLocal;
import com.ck.clump.model.response.EventResponse;
import com.ck.clump.service.Service;
import com.ck.clump.ui.activity.EventDetailActivity;
import com.ck.clump.ui.activity.adapter.EventAdapter;
import com.ck.clump.ui.activity.model.Event;
import com.ck.clump.ui.activity.model.ObjectItem;
import com.ck.clump.ui.presenter.fragment.EventsFragmentPresenter;
import com.ck.clump.ui.view.fragment.EventsFragmentView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

public class EventsFragment extends BaseFragment implements EventsFragmentView {

    public static final int REQUEST_EVENT_DETAIL = 1;

    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @Bind(R.id.rvItems)
    RecyclerView rvItems;

    @Inject
    Service service;


    private Context mContext;
    private List<ObjectItem> mEvents = new ArrayList<>();
    private EventAdapter mAdapter;
    private EventsFragmentPresenter presenter;

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
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
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        baseActivity.getDeps().injectEventsFragment(this);
        presenter = new EventsFragmentPresenter(service, this);
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
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        presenter.getEventsList(true);
                    }
                }, 3000);
            }
        });
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryDark,
                R.color.colorPrimary);
        setupRecyclerView();
        getLocalData();
        presenter.getEventsList(false);
    }

    private void setupRecyclerView() {
        mAdapter = new EventAdapter(mContext, mEvents, new EventAdapter.OnItemClickListener() {
            @Override
            public void onClick(Event Item) {
                Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                intent.putExtra("eventID", Item.getId());
                intent.putExtra("eventStatus", Item.getStatus());
                startActivityForResult(intent, REQUEST_EVENT_DETAIL);
            }
        });
        rvItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvItems.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_EVENT_DETAIL) {
                presenter.getEventsList(true);
            }
        }
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
        EventResponse response = (EventResponse) o;
        final List<EventLocal> eventLocals = new ArrayList<>();
        for (Event event : response.getdATA()) {
            eventLocals.add(EventLocal.fromEvent(event));
        }
        baseActivity.getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createOrUpdateAllFromJson(EventLocal.class, new Gson().toJson(eventLocals));
            }
        });
        showList(response.getdATA());
    }

    @Override
    public void logout() {
        baseActivity.clearDataAndLogout();
    }

    private void getLocalData() {
        List<EventLocal> eventLocalLists = baseActivity.getRealm()
                .where(EventLocal.class)
                .findAll();
        List<Event> events = new ArrayList<>();
        for (EventLocal eventLocal : eventLocalLists) {
            events.add(Event.fromEventLocal(eventLocal));
        }
        showList(events);
    }

    private void showList(List<Event> listEvent) {
        Collections.sort(listEvent, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return o2.getStatus().compareToIgnoreCase(o1.getStatus());
            }
        });
        mEvents.clear();
        boolean isAddHeader = false;
        for (Event event : listEvent) {
            if (event.getStartTime() < System.currentTimeMillis() - (60 * 60 * 1000)) {
                event.setStatus(EventStatus.PAST.getValue());
            }
            if (event.getStatus().equals(EventStatus.PAST.getValue())) {
                if (!isAddHeader) {
                    mEvents.add(new ObjectItem(EventAdapter.TYPE_HEADER, mContext.getString(R.string.completed_events)));
                    isAddHeader = true;
                }
                mEvents.add(new ObjectItem(EventAdapter.TYPE_ITEM, event));
            } else {
                mEvents.add(new ObjectItem(EventAdapter.TYPE_ITEM, event));
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void getEventList() {
        presenter.getEventsList(true);
    }
}
