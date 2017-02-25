package io.lattis.hitme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * An activity representing a single Contact detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ContactListActivity}.
 */
public class ContactDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);


        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ConversationFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ConversationFragment.ARG_ITEM_ID));
            ConversationFragment fragment = new ConversationFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contact_detail_container, fragment)
                    .commit();
        }
    }

}
