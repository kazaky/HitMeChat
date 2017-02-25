package io.lattis.hitme;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.lattis.hitme.model.ChatBox;
import io.lattis.hitme.model.ChatNode;
import io.realm.Realm;
import io.realm.RealmList;

public class ConversationFragment extends Fragment {


    public static final String TAG = "ConversationFragment";


    public static final String ARG_ITEM_ID = "item_contact";

    private String contactName;

    private Realm realm;
    private View recyclerView;

    public ConversationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            contactName = getArguments().getString(ARG_ITEM_ID);

            realm = Realm.getDefaultInstance();


            //  CollapsingToolbarLayout appBarLayout =
            //        (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            //if (appBarLayout != null) {

            // mTitle = firstChatNodes.getChatBox().get(0).getMessage();
            //    for (ChatNode someChat : allChatNodes) {
            //       Log.d("TAG", "onCreate: " + someChat.getContact());
            //   }

            // appBarLayout.setTitle(mTitle);
            //    }
        }
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<ChatBox> msgs) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(msgs));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<ChatBox> mValues;

        public SimpleItemRecyclerViewAdapter(List<ChatBox> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.speakerView.setText(mValues.get(position).getSpeaker());
            holder.msgView.setText(mValues.get(position).getMessage());
            holder.dateView.setText(mValues.get(position).getTimeSent());
            holder.timeView.setText(mValues.get(position).getTimeSent());

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView speakerView;
            public final TextView msgView;
            public final TextView dateView;
            public final TextView timeView;
            public ChatBox mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                speakerView = (TextView) view.findViewById(R.id.speaker);
                msgView = (TextView) view.findViewById(R.id.chat_message);
                dateView = (TextView) view.findViewById(R.id.date);
                timeView = (TextView) view.findViewById(R.id.time);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + speakerView.getText() + "'";
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_detail, container, false);

        ChatNode currentChatNodes = realm.where(ChatNode.class)
                .beginsWith("contact", contactName)
                .findFirst();
        recyclerView = rootView.findViewById(R.id.contact_list);
        assert recyclerView != null;
        RealmList<ChatBox> allChatNodes = currentChatNodes.getChatBox();

        setupRecyclerView((RecyclerView) recyclerView, allChatNodes);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
