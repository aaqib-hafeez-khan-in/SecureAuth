package com.example.authenticatorapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final Handler handler = new Handler();
    private EditText accountInput;
    private EditText secretInput;
    private TextView accountLabel;
    private TextView codeView;
    private TextView timerView;
    private String activeSecret;

    private final Runnable refresh = new Runnable() {
        @Override public void run() {
            if (activeSecret != null) {
                try {
                    long now = System.currentTimeMillis();
                    codeView.setText(TotpGenerator.generate(activeSecret, now));
                    long remaining = 30 - ((now / 1000) % 30);
                    timerView.setText("Refreshes in " + remaining + "s");
                } catch (Exception e) {
                    codeView.setText("Invalid secret");
                    timerView.setText(e.getMessage());
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accountInput = findViewById(R.id.accountInput);
        secretInput = findViewById(R.id.secretInput);
        accountLabel = findViewById(R.id.accountLabel);
        codeView = findViewById(R.id.codeView);
        timerView = findViewById(R.id.timerView);
        Button generateButton = findViewById(R.id.generateButton);
        generateButton.setOnClickListener(v -> activateAccount());
        handler.post(refresh);
    }

    private void activateAccount() {
        String secret = secretInput.getText().toString().trim();
        if (secret.isEmpty()) {
            secretInput.setError("Enter a Base32 secret");
            return;
        }
        activeSecret = secret;
        String account = accountInput.getText().toString().trim();
        accountLabel.setText(account.isEmpty() ? "Authenticator code" : account);
        refresh.run();
    }

    @Override protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
