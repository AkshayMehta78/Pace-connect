package com.namclu.android.bloquery.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.namclu.android.bloquery.R;
import com.namclu.android.bloquery.api.model.Answer;
import com.namclu.android.bloquery.ui.adapter.AnswerAdapter;
import com.namclu.android.bloquery.ui.fragment.AddInputDialogFragment;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SingleQuestionActivity extends AppCompatActivity implements
        ChildEventListener,
        AddInputDialogFragment.AddInputDialogListener {

    /* Constants */
    public static final String TAG = "SingleQuestionActivity";
    public static final String TAG_UPDATE = "SingleEditActivity";

    private TextView mQuestionString;

    private int position;

    private AnswerAdapter mAnswerAdapter;

    private DatabaseReference mAnswersReference;
    private DatabaseReference mQuestionReference;

    private FirebaseUser mCurrentUser;
    private Long numberOfAnswers;

    private boolean isOwner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_question);

        // Add Actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_single_question);
        setSupportActionBar(toolbar);

        // Get the intent data from BloqueryActivity
        String questionId = getIntent().getStringExtra("question_id_key");
        String userId = getIntent().getStringExtra("current_ques_user_id");

        // Initialise Views
        mQuestionString = (TextView) findViewById(R.id.text_single_question_string);

        // Initialise Firebase stuff
        mQuestionReference = FirebaseDatabase.getInstance().getReference("questions").child(questionId);
        mQuestionReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equalsIgnoreCase("numberOfAnswers")){
                    numberOfAnswers = (Long)dataSnapshot.getValue();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAnswersReference = FirebaseDatabase.getInstance().getReference("answers").child(questionId);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        isOwner = mCurrentUser.getUid().equalsIgnoreCase(userId);

        // Set listener on Database
        mAnswersReference.addChildEventListener(this);

        /* RecyclerView stuff */
        mAnswerAdapter = new AnswerAdapter(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_answers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAnswerAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Set values for this single Question object
        mQuestionString.setText(getIntent().getStringExtra("question_string"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isOwner){
            getMenuInflater().inflate(R.menu.single_question, menu);
        }else{
            getMenuInflater().inflate(R.menu.single_question, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_delete){
            removeQuestion();
        }else{
            showEditDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * Firebase: Required methods of ChildEventListener
     */
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Answer answer = dataSnapshot.getValue(Answer.class);
        mAnswerAdapter.addAnswer(answer);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onFinishAddInput(String inputText) {
        if (inputText.isEmpty()) {
            Toast.makeText(this, "Please enter an answer...", Toast.LENGTH_SHORT).show();
        } else {
            String userId = mCurrentUser.getUid();
            String key = mAnswersReference.push().getKey();

            Answer answer = new Answer(userId, key, inputText, (long) System.currentTimeMillis(), 0);
            mAnswersReference.child(key).setValue(answer);

            Toast.makeText(this, "Answer added!", Toast.LENGTH_SHORT).show();

            updateAnswerCountInQuestion();
        }
    }

    @Override
    public void onFinishEditInput(String answer, String questionId, String answerId) {
        if (answer.isEmpty()) {
            Toast.makeText(this, "Please enter an answer...", Toast.LENGTH_SHORT).show();
        } else {
            Map<String,Object> taskMap = new HashMap<String,Object>();
            taskMap.put("answerString", answer);
            mAnswersReference.child(answerId).updateChildren(taskMap);
            mAnswerAdapter.updateAnswerInList(answerId,answer);
        }
    }

    private void updateAnswerCountInQuestion() {
        numberOfAnswers = numberOfAnswers + 1;
        Map<String,Object> taskMap = new HashMap<String,Object>();
        taskMap.put("numberOfAnswers", numberOfAnswers);
        mQuestionReference.updateChildren(taskMap);
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddInputDialogFragment addInputDialogFragment = AddInputDialogFragment.newInstance("Add an answer");
        addInputDialogFragment.show(fm, TAG);
    }


    public void removeAnswer(Answer answer) {
        String questionId = getIntent().getStringExtra("question_id_key");
        DatabaseReference answerReference = FirebaseDatabase.getInstance().getReference("answers").child(questionId);
        answerReference.child(answer.getAnswerId()).removeValue();
    }



    private void removeQuestion() {
        mQuestionReference.removeValue();
        finish();
    }

    public void updateAnswer(Answer answer) {
        String questionId = getIntent().getStringExtra("question_id_key");
        FragmentManager fm = getSupportFragmentManager();
        AddInputDialogFragment addInputDialogFragment = AddInputDialogFragment.newInstance(questionId,answer);
        addInputDialogFragment.show(fm, TAG_UPDATE);
    }
}
