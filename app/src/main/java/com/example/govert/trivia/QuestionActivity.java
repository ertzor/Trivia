package com.example.govert.trivia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuestionActivity extends AppCompatActivity implements QuestionRequest.Callback {
    private ArrayList<Question> questions;
    private Integer score, questionsCorrect, questionNumber;
    private String difficulty, nickName, selectedAnswer, correctAnswer;
    private TextView numberTV, categoryTV, questionTV;
    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        // set score
        score = 0;
        questionNumber = 0;
        questionsCorrect = 0;
        selectedAnswer = null;

        // get views and buttons
        numberTV = (TextView) findViewById(R.id.numberTextView);
        categoryTV = (TextView) findViewById(R.id.categoryTextView);
        questionTV = (TextView) findViewById(R.id.questionTextView);
        listView = (ListView) findViewById(R.id.questionList);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
        radioButton4 = (RadioButton) findViewById(R.id.radioButton4);

        // get difficulty and nickName
        difficulty = (String) getIntent().getStringExtra("difficulty");
        nickName = (String) getIntent().getStringExtra("nickName");

        // get questions
        QuestionRequest x = new QuestionRequest(this);
        x.getQuestions(this, difficulty);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener());
    }

    public void submitClicked(View view) {
        // check if last question
        if (questionNumber == 9) {
            // put highscore in database


            // congratulate player
            numberTV.setText("Game finished!");

            // tell the player how they performed
            if (questionsCorrect < 3) {
                String congratulations = String.format(Locale.getDefault(), "Better luck " +
                                "next time, %s",
                        nickName);
                categoryTV.setText(congratulations);
            }
            else if (questionsCorrect < 7) {
                String congratulations = String.format(Locale.getDefault(), "Not bad, %s",
                        nickName);
                categoryTV.setText(congratulations);
            }
            else if (questionsCorrect <= 9) {
                String congratulations = String.format(Locale.getDefault(), "Great job, %s!",
                        nickName);
                categoryTV.setText(congratulations);
            }
            else {
                String congratulations = String.format(Locale.getDefault(), "PERFECTION, " +
                                "%s!", nickName);
                categoryTV.setText(congratulations);
            }

            // show score
            String scoreString = String.format(Locale.getDefault(),
                    "You had %d/10 questions right for a score of %d!", questionsCorrect, score);
            questionTV.setText(scoreString);

            // let player know highScore was saved
            Toast.makeText(this, "Highscore saved!", Toast.LENGTH_LONG).show();

            // show questions and their answers
            radioGroup.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            findViewById(R.id.submitButton).setVisibility(View.INVISIBLE);
        }
        else if (selectedAnswer == null) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_LONG).show();
        }
        else if (selectedAnswer.equals(correctAnswer)) {
            // add score
            switch (difficulty) {
                case "easy":
                    score += 1;
                    break;
                case "medium":
                    score += 2;
                    break;
                case "hard":
                    score += 3;
                    break;
            }

            // increment questionsCorrect
            questionsCorrect += 1;

            // increment questionNumber
            questionNumber += 1;

            // call newQuestion
            newQuestion(questionNumber);
        }
        else {
            // increment questionNumber
            questionNumber += 1;

            // call newQuestion
            newQuestion(questionNumber);
        }
    }

    private class OnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.radioButton1:
                    selectedAnswer = radioButton1.getText().toString();
                    break;
                case R.id.radioButton2:
                    selectedAnswer = radioButton2.getText().toString();
                    break;
                case R.id.radioButton3:
                    selectedAnswer = radioButton3.getText().toString();
                    break;
                case R.id.radioButton4:
                    selectedAnswer = radioButton4.getText().toString();
                    break;
            }
        }
    }

    @Override
    public void gotQuestions(ArrayList<Question> questions) {
        // get questions
        this.questions = questions;

        // call newQuestion
        newQuestion(questionNumber);
    }

    @Override
    public void gotQuestionsError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void newQuestion(Integer number) {
        // reset selectedAnswer
        radioGroup.clearCheck();
        selectedAnswer = null;

        // get question
        Question current = questions.get(number);

        // set number
        String numberString = String.format(Locale.getDefault(),
                "Question %d", number + 1);
        numberTV.setText(numberString);

        // set category and question
        categoryTV.setText(current.getCategory());
        questionTV.setText(current.getQuestion());

        // get and shuffle answers
        List<String> answers = current.getIncorrect();
        answers.add(current.getCorrect());
        Collections.shuffle(answers);
        correctAnswer = current.getCorrect();

        // set radioButtons
        radioButton2.setText(answers.get(0));
        radioButton3.setText(answers.get(1));

        if (answers.size() > 2) {
            radioButton1.setVisibility(View.VISIBLE);
            radioButton4.setVisibility(View.VISIBLE);
            radioButton1.setText(answers.get(2));
            radioButton4.setText(answers.get(3));
        }
        else {
            radioButton1.setVisibility(View.INVISIBLE);
            radioButton4.setVisibility(View.INVISIBLE);
        }
    }
}