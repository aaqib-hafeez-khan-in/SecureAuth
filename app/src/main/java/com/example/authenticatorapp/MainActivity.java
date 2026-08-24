package com.example.authenticatorapp;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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
                    timerView.setText("Check the Base32 secret");
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
        Button saveButton = findViewById(R.id.generateButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        saveButton.setOnClickListener(v -> saveAccount());
        deleteButton.setOnClickListener(v -> deleteAccount());
        restoreAccount();
        handler.post(refresh);
    }

    private void restoreAccount() {
        try {
            activeSecret = SecureStorage.loadSecret(this);
            if (activeSecret != null) {
                String account = SecureStorage.loadAccount(this);
                accountInput.setText(account);
                secretInput.setText("");
                accountLabel.setText(account.isEmpty() ? "Authenticator code" : account);
            }
        } catch (Exception e) {
            activeSecret = null;
            Toast.makeText(this, "Could not unlock saved account", Toast.LENGTH_LONG).show();
        }
    }

    private void saveAccount() {
        String secret = secretInput.getText().toString().trim();
        if (secret.isEmpty()) {
            secretInput.setError("Enter a Base32 secret");
            return;
        }
        try {
            String account = accountInput.getText().toString().trim();
            TotpGenerator.generate(secret, System.currentTimeMillis());
            SecureStorage.save(this, account, secret);
            activeSecret = secret;
            accountLabel.setText(account.isEmpty() ? "Authenticator code" : account);
            secretInput.setText("");
            Toast.makeText(this, "Account saved securely", Toast.LENGTH_SHORT).show();
            refresh.run();
        } catch (Exception e) {
            secretInput.setError("Invalid Base32 secret");
        }
    }

    private void deleteAccount() {
        SecureStorage.clear(this);
        activeSecret = null;
        accountInput.setText("");
        secretInput.setText("");
        accountLabel.setText("No account configured");
        codeView.setText("------");
        timerView.setText("Add an account to begin");
        Toast.makeText(this, "Saved account deleted", Toast.LENGTH_SHORT).show();
    }

    @Override protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
