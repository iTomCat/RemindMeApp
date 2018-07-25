package com.example.tomcat.remindmeapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomcat.remindmeapp.data.AppContentProvider;
import com.example.tomcat.remindmeapp.data.PlacesContract;
import com.example.tomcat.remindmeapp.data.RemindersContract;
import com.example.tomcat.remindmeapp.models.Places;
import com.example.tomcat.remindmeapp.models.Reminder;

import java.util.List;

/**
 * Fragment with Active RemindersFragment - Reminder List
 */

public class RemindersFragment extends Fragment implements
        ReminderAdapter.ReminderAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks{

    public ReminderAdapter.ReminderAdapterOnClickHandler mClickHandler = this;


    private final static int REMINDERS_ID_LOADER = 24;
    private final static int PLACES_ID_LOADER = 26;

    public static final String SORT_ORDER = "sort";
    private List<Reminder> mReminderList = null;
    private List<Places> mPlacesList = null;
    private ReminderAdapter adapter;

    public RemindersFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminders_fragment, container, false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
                (getActivity(), LinearLayoutManager.VERTICAL, false);

        adapter = new ReminderAdapter(mClickHandler);

        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view_reminders);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.d("RemFrag", "Moved " );
                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                Log.d("RemFrag", "Long Presed " );
                return super.isLongPressDragEnabled();
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                Log.d("RemFrag", "Swipped " + swipeDir);

            }


           /* @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder.getItemViewType() == ITEM_TYPE_ACTION_WIDTH_NO_SPRING) return 0;
                return makeMovementFlags(ItemTouchHelper.UP|ItemTouchHelper.DOWN,
                        ItemTouchHelper.START|ItemTouchHelper.END);
            }*/

          /*  @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ReminderAdapter.ViewHolder){
                    Log.d("RemFrag", "aaaaa" );
                    return 0;
                }
                Log.d("RemFrag", "bbbb" );
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
            */
        }).attachToRecyclerView(mRecyclerView);


        return view;
    }

    // ********************************************************************************************* Click on Reminder List
    @Override
    public void onClick(int position) {
        Log.d("RemFrag", "Button List " + position);
    }

    @Override
    public void onResume() {
        super.onResume();
        makeMovieSearchQuery(REMINDERS_ID_LOADER);
    }



    // *********************************************************************************************
    // ********************************************************************************************* Start Load
    private void makeMovieSearchQuery(int laoder) {
        //Bundle queryBundle = new Bundle();
        //queryBundle.putInt(SEARCH_QUERY_URL_EXTRA, sortOrder);
        //queryBundle.putInt(SORT_ORDER, sortOrder);
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        int currLoadID = REMINDERS_ID_LOADER;

        switch(laoder) {
            case REMINDERS_ID_LOADER:
                currLoadID = REMINDERS_ID_LOADER;
                break;
            case PLACES_ID_LOADER:
                currLoadID = PLACES_ID_LOADER;
            default: break;
        }

        Object movieSearchLoader = loaderManager.getLoader(currLoadID);
        if (movieSearchLoader == null){
            //loaderManager.initLoader(REMINDERS_ID_LOADER, queryBundle, this);
            loaderManager.initLoader(currLoadID, null, this);
        }else {
            loaderManager.restartLoader(currLoadID, null, this);
        }

    }

    // --------------------------------------------------------------------------------------------- Async Task Loader
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        //mLoadingIndicator.setVisibility(View.VISIBLE);
       // int currSortOrder = args.getInt(SORT_ORDER);

       /* // ---------------------------------------------------------------- Load POPULAR / TOP RATED
        if ((NetworkUtils.checkInternetConnect(this)
                && (currSortOrder == POPULAR) || (currSortOrder == TOP_RATED))) {
            return new MovieLoader(this, args.getInt(SEARCH_QUERY_URL_EXTRA));

            // -------------------------------------------------------------------------- Load FAVORITES
        } else {
            return new CursorLoader(this, MovieContract.MovieEntry.CONTENT_URI,
                    null, null, null, null);
        }*/

        if (id == REMINDERS_ID_LOADER) { // -------------------------------------------------------- Load REMINDERS
            return new CursorLoader(getActivity(),
                    RemindersContract.RemindersEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);


        } else { // -------------------------------------------------------------------------------- Load PLACES
            return new CursorLoader(getActivity(),
                    PlacesContract.PlacesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }
    }

    //@SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader loader, Object loadedData) {
        //mLoadingIndicator.setVisibility(View.INVISIBLE);

        /*if ((selectedSort == POPULAR) || (selectedSort == TOP_RATED)) {
            mMovieAdapter.setRemindersData((List<Movie>) loadedData);
            mReminderList = (List<Movie>) loadedData;
        } else if (selectedSort == FAVORITES){
            Cursor mFavoritesData = (Cursor) loadedData;
            mReminderList = MovieContentProvider.remindersListFromCursor(mFavoritesData);
            mMovieAdapter.setRemindersData(mReminderList);
        }

        if (listPos == 0) {
            mRecyclerView.smoothScrollToPosition(listPos);
        } else {
            mRecyclerView.scrollToPosition(listPos);
        }*/

        /*Cursor mFavoritesData;
        mFavoritesData = (Cursor) loadedData;*/

        switch (loader.getId()) {
            case REMINDERS_ID_LOADER:
                Cursor mRemindersData = (Cursor) loadedData;
                // do some stuff here
                mReminderList = AppContentProvider.remindersListFromCursor(mRemindersData);
                makeMovieSearchQuery(PLACES_ID_LOADER); // Start Load Places form DB
                break;
            case PLACES_ID_LOADER:
                // do some other stuff here
                Cursor mPlacesData = (Cursor) loadedData;
                mPlacesList = AppContentProvider.placesListFromCursor(mPlacesData);
                adapter.setRemindersData(mReminderList, mPlacesList);
                break;
            default:
                break;
        }

        //Cursor mFavoritesData = (Cursor) loadedData;
        //mReminderList = AppContentProvider.remindersListFromCursor(mFavoritesData);
        //adapter.setRemindersData(mReminderList);


        //mRecyclerView.smoothScrollToPosition(listPos);


    }

    @Override
    public void onLoaderReset(Loader loader) {

        //TODO MAKE LOAD RESET
    }

    // ********************************************************************************************* Async Task Loader Class for POPULAR & TOP RATED
    /*static class MovieLoader extends AsyncTaskLoader<List<Movie>> {
        private int sortOrder;

        MovieLoader(Context context, int sortOrder) {
            super(context);
            this.sortOrder = sortOrder;
        }

        @Override
        protected void onStartLoading(){
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public List<Movie> loadInBackground() {
            String dataFromMovieDB = NetworkUtils.getDataFromMoveDB(sortOrder);
            List<Movie> moviesList = null;

            if (dataFromMovieDB != null) {
                try {
                    moviesList = JSONUtilis.parseMovieJson(dataFromMovieDB);
                    Log.d("dataTest", "Data from MovieDB: " + dataFromMovieDB);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                return null;
            }
            return moviesList;
        }
    }*/
}
