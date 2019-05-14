package com.namclu.android.bloquery.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.namclu.android.bloquery.R;
import com.namclu.android.bloquery.api.model.Question;
import com.namclu.android.bloquery.ui.activity.BloqueryActivity;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionAdapterViewHolder> {



    public static interface QuestionAdapterDelegate {
        public void onItemClicked(int position, List<Question> queries);
        public void onLikedClicked(int position,Question question);
    }

    /* private fields */
    private List<Question> mQuestions;

    // References to delegate objects
    private WeakReference<QuestionAdapterDelegate> mDelegate;

    public QuestionAdapter() {
        mQuestions = new ArrayList<>();
    }

    public void addQuestion(Question question) {
        mQuestions.add(question);
        notifyDataSetChanged();
    }

    @Override
    public QuestionAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_list_item, parent, false);
        return new QuestionAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(QuestionAdapterViewHolder holder, int position) {
        holder.update(position, mQuestions.get(position));
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    /*
     * Getters and setter methods
     */
    public QuestionAdapterDelegate getQuestionAdapterDelegate() {
        if (mDelegate == null) {
            return null;
        }
        return mDelegate.get();
    }

    public void setQuestionAdapterDelegate(QuestionAdapterDelegate delegate) {
        mDelegate = new WeakReference<QuestionAdapterDelegate>(delegate);
    }

    class QuestionAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Reference to Question items
        TextView questionString,statusTextView;
        TextView timeStamp;
        ImageView likedStatusImageView;
        LinearLayout likedStatusLayout;

        int position;

        public QuestionAdapterViewHolder(View itemView) {

            super(itemView);
            questionString = (TextView) itemView.findViewById(R.id.text_question_string);
            timeStamp = (TextView) itemView.findViewById(R.id.text_question_time_stamp);
            likedStatusImageView = itemView.findViewById(R.id.likedStatusImageView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            likedStatusLayout = itemView.findViewById(R.id.likedStatusLayout);

            likedStatusLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDelegate.get().onLikedClicked(position,mQuestions.get(position));
                }
            });
            itemView.setOnClickListener(this);
        }

        void update(int position, Question question) {

            this.position = position;

            questionString.setText(question.getQuestionString().trim());
            timeStamp.setText("Submitted: " + formatDate(new Date(question.getTimeStamp())));

            if(question.isLiked){
                likedStatusImageView.setImageResource(R.drawable.ic_liked);
                statusTextView.setText("Liked");
            }else{
                likedStatusImageView.setImageResource(R.drawable.ic_not_liked);
                statusTextView.setText("Like");
            }
        }

        // Return a formatted date string (i.e. 1 Jan, 2000 ) from a Date object.
        private String formatDate(Date date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
            return dateFormat.format(date);
        }

        // Only method of View.OnClickListener
        @Override
        public void onClick(View view) {
            if (getQuestionAdapterDelegate() != null) {
                getQuestionAdapterDelegate().onItemClicked(position, mQuestions);
            }
        }
    }

    public void updateItemInList(int position, boolean isLiked) {
        mQuestions.get(position).isLiked = isLiked;
        notifyItemChanged(position);
    }
}
