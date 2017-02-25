package io.lattis.hitme;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

import io.lattis.hitme.model.ChatBox;
import io.lattis.hitme.model.ChatNode;
import io.lattis.hitme.utils.QueryUtils;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ConversationFragment extends Fragment {


    public static final String TAG = "ConversationFragment";


    public static final String ARG_ITEM_ID = "item_contact";

    private String contactName;

    private Realm realm;
    private RecyclerView recyclerView;
    private static SimpleItemRecyclerViewAdapter adapter;
    private ChatNode currentChatNodes;
    private RealmResults<ChatBox> senderOnlyNodes;
    private RealmList<ChatBox> allChatNodes;

    public ConversationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            contactName = getArguments().getString(ARG_ITEM_ID);

            realm = Realm.getDefaultInstance();

            currentChatNodes = realm.where(ChatNode.class)
                    .beginsWith("contact", contactName)
                    .findFirst();
            senderOnlyNodes = realm.where(ChatBox.class)
                    .beginsWith("speaker", contactName).findAll();
            allChatNodes = currentChatNodes.getChatBox();

        }
    }


    private static class MyHandler extends Handler {
    }

    private final MyHandler mHandler = new MyHandler();

    public static class MyRunnable implements Runnable {
        private final WeakReference<Activity> mActivity;

        public MyRunnable(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            Activity activity = mActivity.get();
            if (activity != null) {
                adapter.notifyDataSetChanged();

            }
        }
    }

    private MyRunnable mRunnable = new MyRunnable(getActivity());


    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<ChatBox> msgs) {
        adapter = new SimpleItemRecyclerViewAdapter(msgs);
        recyclerView.setAdapter(adapter);
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
            holder.dateView.setText(QueryUtils.formatDate(new Date(Long.parseLong(mValues.get(position).getTimeSent()))));
            holder.timeView.setText(QueryUtils.formatTime(new Date(Long.parseLong(mValues.get(position).getTimeSent()))));

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


        recyclerView = (RecyclerView) rootView.findViewById(R.id.contact_list);
        assert recyclerView != null;
        final EditText msg_edittext = (EditText) rootView.findViewById(R.id.messageEditText);
        final ImageButton sendButton = (ImageButton) rootView.findViewById(R.id.sendMessageButton);


        setupRecyclerView(recyclerView, allChatNodes);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msg_edittext.getEditableText().toString();
                if (!message.equalsIgnoreCase("")) {
                    final ChatBox chatMessage = new ChatBox();
                    chatMessage.setSpeaker("Me");
                    chatMessage.setMessage(message);
                    chatMessage.setTimeSent(String.valueOf(System.currentTimeMillis()));
                    msg_edittext.setText("");

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            currentChatNodes.getChatBox().add(chatMessage);
                            adapter.notifyDataSetChanged();
                        }
                    });


                    final long changeTime = 5000L;

                    sendButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    currentChatNodes.getChatBox().add(senderOnlyNodes.get(0));
                                    adapter.notifyDataSetChanged();
                                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                                }
                            });

                        }
                    }, changeTime);

                    // Scroll to the end
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);


                }
            }
        });


        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
