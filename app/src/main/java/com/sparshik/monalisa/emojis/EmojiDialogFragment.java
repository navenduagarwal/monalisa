package com.sparshik.monalisa.emojis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sparshik.monalisa.R;
import com.sparshik.monalisa.utils.Constants;

import java.util.ArrayList;

import static android.widget.LinearLayout.HORIZONTAL;

/**
 * Shows Emoji in Dialog Fragment
 */

public class EmojiDialogFragment extends DialogFragment {

    private EmojiAdapter mEmojiAdapter;
    private ArrayList<Integer> EmojiList;
    private RecyclerView recyclerView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_emojis, null);

        EmojiList = new ArrayList<>();

        loadEmojis();
        Log.d("Outside 1", EmojiList.size() + "");
        recyclerView = (RecyclerView) rootView.findViewById(R.id.emoji_list);
        assert recyclerView != null;
        mEmojiAdapter = new EmojiAdapter(EmojiList, getActivity());
        setupRecyclerView(recyclerView);

        builder.setView(rootView)
                .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EmojiDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), 5, HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        Log.d("Outside Adapter", EmojiList.size() + "");
        recyclerView.setAdapter(mEmojiAdapter);
    }

    private void loadEmojis() {
        EmojiList.add(R.drawable.e1);
        EmojiList.add(R.drawable.e2);
        EmojiList.add(R.drawable.e3);
        EmojiList.add(R.drawable.e4);
        EmojiList.add(R.drawable.e5);
        EmojiList.add(R.drawable.e6);
        EmojiList.add(R.drawable.e7);
        EmojiList.add(R.drawable.e8);
        EmojiList.add(R.drawable.e9);
        EmojiList.add(R.drawable.e10);
        EmojiList.add(R.drawable.e11);
        EmojiList.add(R.drawable.e12);
        EmojiList.add(R.drawable.e13);
        EmojiList.add(R.drawable.e14);
        EmojiList.add(R.drawable.e15);
        EmojiList.add(R.drawable.e16);
        EmojiList.add(R.drawable.e17);
        EmojiList.add(R.drawable.e18);
        EmojiList.add(R.drawable.e19);
        EmojiList.add(R.drawable.e20);
        EmojiList.add(R.drawable.e21);
        EmojiList.add(R.drawable.e22);
        EmojiList.add(R.drawable.e23);
        EmojiList.add(R.drawable.e24);
        EmojiList.add(R.drawable.e25);
        EmojiList.add(R.drawable.e26);
        EmojiList.add(R.drawable.e27);
        EmojiList.add(R.drawable.e28);
        EmojiList.add(R.drawable.e29);
        EmojiList.add(R.drawable.e30);
        EmojiList.add(R.drawable.e31);
        EmojiList.add(R.drawable.e32);
        EmojiList.add(R.drawable.e33);
        EmojiList.add(R.drawable.e34);
        EmojiList.add(R.drawable.e35);
        EmojiList.add(R.drawable.e36);
        EmojiList.add(R.drawable.e37);
        EmojiList.add(R.drawable.e38);
        EmojiList.add(R.drawable.e39);
        EmojiList.add(R.drawable.e40);
        EmojiList.add(R.drawable.e41);
        EmojiList.add(R.drawable.e42);
        EmojiList.add(R.drawable.e43);
        EmojiList.add(R.drawable.e44);
        EmojiList.add(R.drawable.e45);
        EmojiList.add(R.drawable.e46);
        EmojiList.add(R.drawable.e47);
        EmojiList.add(R.drawable.e48);
        EmojiList.add(R.drawable.e49);
        EmojiList.add(R.drawable.e50);
        EmojiList.add(R.drawable.e51);
        EmojiList.add(R.drawable.e52);
        EmojiList.add(R.drawable.e53);
        EmojiList.add(R.drawable.e54);
        EmojiList.add(R.drawable.e55);
        EmojiList.add(R.drawable.e56);
        EmojiList.add(R.drawable.e57);
        EmojiList.add(R.drawable.e58);
        EmojiList.add(R.drawable.e59);
        EmojiList.add(R.drawable.e60);
        EmojiList.add(R.drawable.e61);
        EmojiList.add(R.drawable.e62);
        EmojiList.add(R.drawable.e63);
        EmojiList.add(R.drawable.e64);
        EmojiList.add(R.drawable.e65);
        EmojiList.add(R.drawable.e66);
        EmojiList.add(R.drawable.e67);
        EmojiList.add(R.drawable.e68);
        EmojiList.add(R.drawable.e69);
        EmojiList.add(R.drawable.e70);
        EmojiList.add(R.drawable.e71);
        EmojiList.add(R.drawable.e72);
        EmojiList.add(R.drawable.e73);
        EmojiList.add(R.drawable.e74);
        EmojiList.add(R.drawable.e75);
        EmojiList.add(R.drawable.e76);
        EmojiList.add(R.drawable.e77);
        EmojiList.add(R.drawable.e78);
    }

    public interface Callback {

        void onClose();
    }

    /**
     * Adapter for Emoji RecyclerView
     */

    public class EmojiAdapter
            extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {

        private final ArrayList<Integer> mEmojis;
        private Activity mActivity;
        private SharedPreferences preferences;

        public EmojiAdapter(ArrayList<Integer> emojis, Activity activity) {
            mEmojis = emojis;
            Log.d("Indie Adapter", mEmojis.size() + "");
            mActivity = activity;
            preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        }

        @Override
        public EmojiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.emoji_list_content, parent, false);
            return new EmojiAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final EmojiAdapter.ViewHolder holder, int position) {
            final int emojiUrl = mEmojis.get(position);
            int d = R.drawable.e1;
            Glide.with(mActivity)
                    .load(emojiUrl)
                    .override(96, 96)
                    .into(holder.mEmoji);
            Log.d("Indie Adapter", mEmojis.size() + "");

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor spe = preferences.edit();
                    spe.putInt(Constants.KEY_CURRENT_EMOJI, emojiUrl).apply();
                    ((Callback) getActivity()).onClose();
                    EmojiDialogFragment.this.getDialog().cancel();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mEmojis.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mEmoji;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mEmoji = (ImageView) view.findViewById(R.id.emoji_item);
            }
        }
    }
}
