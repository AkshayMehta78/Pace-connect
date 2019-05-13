package com.namclu.android.bloquery.ui.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.namclu.android.bloquery.R;
import com.namclu.android.bloquery.api.model.Answer;
import com.namclu.android.bloquery.ui.activity.SingleQuestionActivity;


import java.util.ArrayList;
import java.util.List;



public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerAdapterViewHolder> {

    /* Private fields */
    private List<Answer> mAnswers;

    private Activity mActivity;



    public AnswerAdapter(Activity activity) {
        mAnswers = new ArrayList<>();
        mActivity = activity;
    }

    public void addAnswer(Answer answer) {
        mAnswers.add(answer);
        notifyDataSetChanged();
    }

    // Add a List<Answer>
    public void addAnswers(List<Answer> answers) {
        mAnswers.addAll(answers);
        notifyDataSetChanged();
    }

    @Override
    public AnswerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.answer_list_item, parent, false);
        return new AnswerAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(AnswerAdapterViewHolder holder, int position) {
        holder.update(position, mAnswers.get(position));
    }

    @Override
    public int getItemCount() {
        return mAnswers.size();
    }


    class AnswerAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView answerString;
        int position;
        ImageView overFlowMenu;

        public AnswerAdapterViewHolder(View itemView) {
            super(itemView);
            answerString = itemView.findViewById(R.id.text_answer_string);
            overFlowMenu = itemView.findViewById(R.id.overFlowMenu);
            overFlowMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetMenuDialog dialog = new BottomSheetBuilder(mActivity)
                            .setMode(BottomSheetBuilder.MODE_LIST)
                            .setMenu(R.menu.options)
                            .setItemClickListener(new BottomSheetItemClickListener() {
                                @Override
                                public void onBottomSheetItemClick(MenuItem item) {
                                    if(item.getItemId() == R.id.action_delete){
                                        ((SingleQuestionActivity)mActivity).removeAnswer(mAnswers.get(position));
                                        mAnswers.remove(position);
                                        notifyDataSetChanged();
                                    }
                                }
                            })
                            .createDialog();

                    dialog.show();
                }
            });
        }

        void update(int position, Answer answer) {
            this.position = position;
            answerString.setText(answer.getAnswerString());
        }
    }
}
