package io.lattis.hitme;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.lattis.hitme.model.ChatNode;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * An activity representing a list of Contacts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ContactDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ContactListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<ChatNode>> {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public static final String TAG = ContactListActivity.class.getName();


    private static final int EARTHQUAKE_LOADER_ID = 1;


    private static final String API_REQUEST_URL
            = "http://hitme-dev.us-west-2.elasticbeanstalk.com/api/all-messages";


    private TextView mEmptyStateTextView;
    private Realm realm;
    private View recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = findViewById(R.id.contact_list);
        assert recyclerView != null;

        if (findViewById(R.id.contact_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
            Log.e(TAG, "onCreate, initLoader: ");

        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText("no_internet_connection");

        }


        realm = Realm.getDefaultInstance();

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<ChatNode> contacts) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(contacts));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<ChatNode> mValues;

        public SimpleItemRecyclerViewAdapter(List<ChatNode> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contact_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            // holder.mIdView.setText(mValues.get(position).id);
            holder.mContact.setText(mValues.get(position).getContact());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ContactDetailFragment.ARG_ITEM_ID, holder.mItem.getContact());
                        ContactDetailFragment fragment = new ContactDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.contact_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ContactDetailActivity.class);
                        intent.putExtra(ContactDetailFragment.ARG_ITEM_ID, holder.mItem.getContact());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContact;
            public ChatNode mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContact = (TextView) view.findViewById(R.id.contact);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContact.getText() + "'";
            }
        }
    }


    @Override
    public Loader<List<ChatNode>> onCreateLoader(int id, Bundle args) {
        Log.e(TAG, "onCreateLoader: ");


        Uri baseUri = Uri.parse(API_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        // Create a new loader for the given URL
        return new ChatLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<ChatNode>> loader, List<ChatNode> data) {
        Log.e(TAG, "onLoadFinished: ");

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        //  mEmptyStateTextView.setText(R.string.no_earthquakes);

        // Clear the adapter of previous earthquake data
        // adapter.clear();

        RealmResults<ChatNode> allChatNodes = realm.where(ChatNode.class)
                .findAll();
        for (ChatNode someChat : allChatNodes) {
            Log.d(TAG, "onCreate: " + someChat.getContact());
        }


        setupRecyclerView((RecyclerView) recyclerView, allChatNodes);

    }

    @Override
    public void onLoaderReset(Loader<List<ChatNode>> loader) {
        Log.e(TAG, "onLoaderReset: ");
        // Loader reset, so we can clear out our existing data.

        // adapter.clear();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // So you won't leak memory
        realm.close();
    }
}
