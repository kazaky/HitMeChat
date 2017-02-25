package io.lattis.hitme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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


    private static final int CHAT_LOADER_ID = 1;


    private static final String API_REQUEST_URL
            = "http://hitme-dev.us-west-2.elasticbeanstalk.com/api/all-messages";

    private Realm realm;
    private View recyclerView;
    public static boolean previouslyLoaded = false;
    private SharedPreferences sharedPref;
    private TextView mEmptyStateTextView;
    private SimpleItemRecyclerViewAdapter adapter;
    private RealmResults<ChatNode> allChatNodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        realm = Realm.getDefaultInstance();


        recyclerView = findViewById(R.id.contact_list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        assert recyclerView != null;


        sharedPref = getPreferences(Context.MODE_PRIVATE);
        previouslyLoaded = sharedPref.getBoolean("saved_before", false);

        if (!previouslyLoaded) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                LoaderManager loaderManager = getSupportLoaderManager();
                loaderManager.initLoader(CHAT_LOADER_ID, null, this);
                Log.e(TAG, "onCreate, initLoader: ");

            } else {
                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.GONE);

                mEmptyStateTextView.setText("no_internet_connection");
            }
        } else {
            loadFromDatabase();
        }
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<ChatNode> contacts) {

         adapter = new SimpleItemRecyclerViewAdapter(contacts);


        recyclerView.setAdapter(adapter);
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
            holder.mContact.setText(mValues.get(position).getContact());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ConversationFragment.ARG_ITEM_ID, holder.mItem.getContact());
                        ConversationFragment fragment = new ConversationFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.contact_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ContactDetailActivity.class);
                        intent.putExtra(ConversationFragment.ARG_ITEM_ID, holder.mItem.getContact());

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

        loadFromDatabase();
    }

    private void loadFromDatabase() {
         allChatNodes = realm.where(ChatNode.class)
                .findAll();


        for (ChatNode someChat : allChatNodes) {
            if (!someChat.getContact().isEmpty()) {
                sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("saved_before", true);
                editor.commit();

            }
            Log.d(TAG, "onCreate: " + someChat.getContact());
        }

        setupRecyclerView((RecyclerView) recyclerView, allChatNodes);

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

    }

    @Override
    public void onLoaderReset(Loader<List<ChatNode>> loader) {
        Log.e(TAG, "onLoaderReset: ");
        // Loader reset, so we can clear out our existing data.

        allChatNodes.clear();
        adapter.notifyDataSetChanged();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // So you won't leak memory
        realm.close();
    }
}
