package garmin.com.academyshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    mPasswordView.requestFocus();
                }
                return true;
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    login(mEmailView.getText().toString(), mPasswordView.getText().toString());
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login(mEmailView.getText().toString(), mPasswordView.getText().toString());

            }
        });
    }
    private void login(String loginEmail, String loginPassword) {
        // Get shared preferences used in settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Check if login email is not empty, if it is empty will not log in
        if (!TextUtils.isEmpty(loginEmail)) {
            // Get registered email from shared preferences
            String registeredEmail = sharedPreferences.getString(getString(R.string.key_login_email), "");

            // If registered email is empty make the registration for this email and optional password
            if (TextUtils.isEmpty(registeredEmail)) {
                // Save the email and optional password to shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.key_login_email), loginEmail);
                editor.putString(getString(R.string.key_login_password), loginPassword);
                editor.apply();
                login();
            } else if (registeredEmail.equals(loginEmail)) {
                // If registered email is the same with the login email check the optional password
                String registeredPassword = sharedPreferences.getString(getString(R.string.key_login_password), "");
                if (registeredPassword.equals(loginPassword)) {
                    login();
                } else {
                    showToast("Wrong password");
                }
            } else {
                showToast("Wrong email");
            }
        } else {
            showToast("Email missing");
        }
    }

    private void login() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

