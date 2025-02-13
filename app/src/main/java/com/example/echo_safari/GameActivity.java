package com.example.echo_safari;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private Button nextButton, backButton, startButton;
    private TextView questionText, questionNumberText;
    private ImageView questionImage;
    private int currentQuestionIndex = 0;
    private int score = 0;

    // Questions related to eco-friendly, zero waste, and recycling
    private String[] questions = {
            "What is a common method for reducing waste in landfills?",
            "Which of the following is a recyclable material?",
            "What does 'zero waste' aim to achieve?",
            "Which of these is an example of a reusable product?",
            "What is one of the main benefits of recycling?"
    };

    // Options for each question
    private String[][] options = {
            {"Composting", "Burning", "Burying", "Incineration"},
            {"Plastic bottles", "Used tissues", "Cigarette butts", "Food wrappers"},
            {"Minimizing waste", "Maximizing landfill use", "Producing more plastic", "Using more disposable items"},
            {"Plastic cups", "Reusable water bottles", "Plastic straws", "Single-use bags"},
            {"Creating more jobs", "Conserving natural resources", "Increasing pollution", "Increasing waste"},
    };

    // Index of the correct answers
    private int[] correctAnswers = {0, 0, 0, 1, 1}; // Correct answers index
    private int[] questionImages = {
            R.drawable.composting, R.drawable.recyclable_material, R.drawable.zero_waste,
            R.drawable.reusable_bottle, R.drawable.recycling_benefit
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        radioGroup = findViewById(R.id.radioGroup);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);
        startButton = findViewById(R.id.startButton);
        questionText = findViewById(R.id.questionText);
        questionNumberText = findViewById(R.id.questionNumberText);
        questionImage = findViewById(R.id.questionImage);

        // Initially, hide the Next button and show Start button
        nextButton.setVisibility(View.GONE);

        // Handle Start Button click
        startButton.setOnClickListener(v -> {
            startButton.setVisibility(View.GONE);  // Hide Start button
            nextButton.setVisibility(View.VISIBLE); // Show Next button
            setQuestion(currentQuestionIndex);     // Load the first question
            questionNumberText.setVisibility(View.VISIBLE); // Show question number
        });

        // Handle Next Button click
        nextButton.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedAnswer = findViewById(selectedId);
                if (selectedAnswer.getText().equals(options[currentQuestionIndex][correctAnswers[currentQuestionIndex]])) {
                    score++;
                }
            }

            currentQuestionIndex++;
            if (currentQuestionIndex < questions.length) {
                setQuestion(currentQuestionIndex);
            } else {
                // Show score and reset game
                questionText.setText("Your score: " + score);
                questionNumberText.setVisibility(View.GONE); // Hide question number
                nextButton.setEnabled(false);  // Disable next button after quiz is over
            }
        });

        // Handle Back Button click to go home
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setQuestion(int index) {
        questionText.setText(questions[index]);
        questionNumberText.setText("Question " + (index + 1));
        radioGroup.clearCheck();

        // Add radio buttons dynamically
        radioGroup.removeAllViews();
        for (String option : options[index]) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioGroup.addView(radioButton);
        }

        // Set question image
        questionImage.setImageResource(questionImages[index]);
    }
}
