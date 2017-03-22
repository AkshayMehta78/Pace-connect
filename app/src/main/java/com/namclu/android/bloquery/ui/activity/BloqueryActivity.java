package com.namclu.android.bloquery.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.namclu.android.bloquery.R;
import com.namclu.android.bloquery.api.QuestionDataSource;
import com.namclu.android.bloquery.api.model.Question;
import com.namclu.android.bloquery.ui.adapter.QuestionAdapter;

import java.util.List;

/**
 * Created by namlu on 30-Jul-16.
 * <p>
 * BloqueryActivity.java is the default main screen of the app.
 */
public class BloqueryActivity extends Activity
        implements
        ChildEventListener,
        QuestionAdapter.QuestionAdapterDelegate {

    /* Constants */
    public static final String TAG = "BloqueryActivity";
    public static final String QUESTIONS = "questions";

    /* private fields */
    // A reference to an {@link RecyclerView.Adapter}
    private QuestionAdapter mQuestionAdapter;

    // A reference to the {@link RecyclerView} in the activity_bloquery.xml layout
    private RecyclerView mQueryRecyclerView;

    // A reference to the root Firebase {@link DatabaseReference} object
    private DatabaseReference mDatabaseReference;

    // A reference to a child Firebase {@link DatabaseReference} object
    private DatabaseReference mQuestionsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloquery);

        // Initialize the adapter
        mQuestionAdapter = new QuestionAdapter();
        // Set BloqueryActivity(this) as QuestionAdapter's delegate
        mQuestionAdapter.setQuestionAdapterDelegate(this);

        // Initialize Views in the layout
        mQueryRecyclerView = (RecyclerView) findViewById(R.id.recycler_question);

        // Set the layout, animator, and adapter for recyclerView
        mQueryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mQueryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mQueryRecyclerView.setAdapter(mQuestionAdapter);

        // Firebase: initialize references
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mQuestionsReference = mDatabaseReference.child(QUESTIONS);

        // Use QuestionDataSource.writeNewQuestion() to add Question to Firebase
        QuestionDataSource dataSource = new QuestionDataSource();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mQuestionsReference.addChildEventListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // call to clear previous data from adapter when restarting
        mQuestionAdapter.clear();
    }

    /*
     * Firebase: Required methods of ChildEventListener
     */
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Question question = dataSnapshot.getValue(Question.class);
        mQuestionAdapter.addQuestion(question);
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

    /*
     * Method from implementing QuestionAdapter.QuestionAdapterDelegate
     */
    @Override
    public void onItemClicked(int position, List<Question> questions) {

        // The current Question item
        Question questionItem = questions.get(position);
        String questionId = questionItem.getQuestionId();

        Intent intent = new Intent(this, SingleQuestionActivity.class);
        intent.putExtra("question_id", questionId);

        startActivity(intent);
    }
}
