package com.example.joctrivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joctrivia.data.AnswerListAsyncResponse;
import com.example.joctrivia.data.QuestionBank;
import com.example.joctrivia.model.Question;
import com.example.joctrivia.model.Score;
import com.example.joctrivia.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView questionTextView;
    private TextView questionCounterTextView;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private int getCurrentQuestionIndex = 0;
    private List<Question> questionsList;
    private TextView scoreTextView;

    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;


    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score();

        prefs = new Prefs(MainActivity.this);

        scoreTextView = findViewById(R.id.score_text);
        ImageButton nextButton = findViewById(R.id.next_button);
        ImageButton prevButton = findViewById(R.id.prev_button);
        Button trueButton = findViewById(R.id.true_button);
        Button falseButton = findViewById(R.id.false_button);
        questionCounterTextView = findViewById(R.id.counter_text);
        questionTextView = findViewById(R.id.question_textview);

        TextView highestScoreTextView = findViewById(R.id.highest_score);


        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);

        scoreTextView.setText(MessageFormat.format("Current Score : {0} ", String.valueOf(score.getScore())));

        currentQuestionIndex = prefs.getState();

        highestScoreTextView.setText(("Highest score : " + String.valueOf(prefs.getHighScore())));
        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {

                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTextView.setText(MessageFormat.format("{0} / {1}", currentQuestionIndex, questionList.size()));
                Log.d("Inside", "processFinished: " + questionArrayList);
            }
        });


    }

    @Override
    public void onClick(View view) {
        final Button trueButton = findViewById(R.id.true_button);
        final Button falseButton = findViewById(R.id.false_button);
        trueButton.setClickable(false);
        falseButton.setClickable(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                trueButton.setClickable(true);
                falseButton.setClickable(true);
            }
        }, 700);
        switch (view.getId()){
            case R.id.prev_button:
                if(currentQuestionIndex > 0) {
                    currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                    updateQuestion();
                }
                break;
            case R.id.next_button:
                goNext();
                break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;
        }

    }

    private void checkAnswer(boolean userChooseCorrect) {
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageId = 0;
        if ( userChooseCorrect == answerIsTrue){
            fadeView();
            addPoints();
            toastMessageId = R.string.correct_answer;
        }else{
            shakeAnimation();
            deductPoints();
            toastMessageId = R.string.wrong_answer;
        }
        Toast.makeText(MainActivity.this, toastMessageId, Toast.LENGTH_SHORT).show();

    }
    private void addPoints(){
        scoreCounter+=100;
        score.setScore(scoreCounter);
        scoreTextView.setText(MessageFormat.format("Current Score : {0} ", String.valueOf(score.getScore())));
        Log.d("Score", "addPoints: "+ score.getScore());
    }
    private void deductPoints(){
        scoreCounter-=100;
        if(scoreCounter > 0)
        {
            score.setScore(scoreCounter);
            scoreTextView.setText(MessageFormat.format("Current Score : {0} ", String.valueOf(score.getScore())));
            Log.d("Score", "addPoints: "+ score.getScore());
        }
        else
        {
            scoreCounter = 0;
            score.setScore(scoreCounter);
            scoreTextView.setText(MessageFormat.format(" Current Score : {0} ", String.valueOf(score.getScore())));
            Log.d("bad score", "deductPoints: " + score.getScore());
        }
    }
    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText(MessageFormat.format("{0} / {1}", currentQuestionIndex, questionList.size()));

    }
    private void fadeView(){
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setBackgroundColor(Color.RED);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setBackgroundColor(Color.WHITE);
                goNext();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    private void goNext(){
        currentQuestionIndex = (currentQuestionIndex+1) % questionList.size();
        updateQuestion();
    }

    @Override
    protected void onPause() {
        prefs.saveHighestScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }
}