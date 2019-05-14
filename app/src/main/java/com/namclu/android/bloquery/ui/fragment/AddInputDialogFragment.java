package com.namclu.android.bloquery.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.namclu.android.bloquery.R;
import com.namclu.android.bloquery.api.model.Answer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddInputDialogListener} interface
 * to handle interaction events.
 * Use the {@link AddInputDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddInputDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private static final String TAG_UPDATE = "SingleEditActivity";
    private EditText mEditText;
    private TextView mTitle;
    private AppCompatButton postButton;
    private Bundle mBundle;
    private boolean isEdit = false;

    public static AddInputDialogFragment newInstance(String questionId, Answer answer) {
        AddInputDialogFragment fragment = new AddInputDialogFragment();
        Bundle args = new Bundle();
        args.putString("questionId", questionId);
        args.putString("answerId", answer.getAnswerId());
        args.putString("answer", answer.getAnswerString());
        args.putString("tag",TAG_UPDATE);
        args.putBoolean("isEdit",true);

        fragment.setArguments(args);
        return fragment;
    }

    // Listener interface with a method passing back data result.
    public interface AddInputDialogListener {
        void onFinishAddInput(String inputText);
        void onFinishEditInput(String inputText,String questionId,String answerId);
    }

    public AddInputDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static AddInputDialogFragment newInstance(String question) {
        AddInputDialogFragment fragment = new AddInputDialogFragment();
        Bundle args = new Bundle();
        args.putString("question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_input_dialog, container);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
        if(mBundle!=null && mBundle.containsKey("isEdit") ){
            isEdit = mBundle.getBoolean("isEdit");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        mEditText = (EditText) view.findViewById(R.id.field_add_input);
        mTitle = (TextView) view.findViewById(R.id.title);

        mTitle.setText(getTag().equalsIgnoreCase("BloqueryActivity")?"Add a Question ":isEdit ? "Edit Response":"Add a Response");
        postButton = (AppCompatButton) view.findViewById(R.id.postButton);

        postButton.setText(getTag().equalsIgnoreCase("BloqueryActivity")?"Post Question ":isEdit ? "Save Response":"Post Response");

        // Fetch arguments from bundle and set question
        String title = getArguments().getString("question", "Enter question");
        getDialog().setTitle(title);
        // Show soft kb automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        );

        // Setup a callback when the "Done" button is pressed on keyboard
        mEditText.setOnEditorActionListener(this);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return input text back to activity through the implemented listener
                AddInputDialogListener listener = (AddInputDialogListener) getActivity();
                if(isEdit){
                    listener.onFinishEditInput(mEditText.getText().toString(),mBundle.getString("questionId"),mBundle.getString("answerId"));
                }else{
                    listener.onFinishAddInput(mEditText.getText().toString());
                }

                // Close the dialog and return back to the parent activity
                dismiss();
            }
        });

        if(mBundle!=null && mBundle.containsKey("tag") && mBundle.getString("tag").equalsIgnoreCase(TAG_UPDATE)){
            String answer = mBundle.getString("answer");
            mEditText.setText(answer);
            mEditText.setSelection(answer.length());
        }
    }

    // Fires when the "Done" button is pressed
    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            AddInputDialogListener listener = (AddInputDialogListener) getActivity();
            if(isEdit){
                listener.onFinishEditInput(mEditText.getText().toString(),mBundle.getString("questionId"),mBundle.getString("answerId"));
            }else{
                listener.onFinishAddInput(mEditText.getText().toString());
            }
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
