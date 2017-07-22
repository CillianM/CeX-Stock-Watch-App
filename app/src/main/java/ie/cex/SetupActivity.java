package ie.cex;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ie.cex.handlers.UserHandler;


/**
 * A login screen that offers login via email/password.
 */
public class SetupActivity extends AppCompatActivity {

    String[] locations = {
            "ie.m.webuy.com",
            "uk.m.webuy.com",
            "us.m.webuy.com",
            "es.m.webuy.com",
            "in.m.webuy.com",
            "pt.m.webuy.com",
            "nl.m.webuy.com",
            "mx.m.webuy.com",
            "pl.m.webuy.com",
            "au.m.webuy.com",
            "it.m.webuy.com"

    };
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView nameView;
    private Spinner locationSpinner;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        // Set up the login form.
        nameView = (AutoCompleteTextView) findViewById(R.id.email);
        locationSpinner = (Spinner) findViewById(R.id.spinner);
        fillSpinner();

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void fillSpinner() {

        List<String> locationlist = new ArrayList<>();
        locationlist.add("Ireland");
        locationlist.add("United Kingdom");
        locationlist.add("United States");
        locationlist.add("Spain");
        locationlist.add("India");
        locationlist.add("Portugal");
        locationlist.add("Netherlands");
        locationlist.add("Mexico");
        locationlist.add("Poland");
        locationlist.add("Australia");
        locationlist.add("Italy");

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, locationlist);
        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        locationSpinner.setAdapter(dataAdapter);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        nameView.setError(null);

        // Store values at the time of the login attempt.
        String email = nameView.getText().toString();
        String location = locations[locationSpinner.getSelectedItemPosition()];

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            nameView.setError(getString(R.string.error_field_required));
            focusView = nameView;
            cancel = true;
        } else if (!isNameValid(email)) {
            nameView.setError(getString(R.string.error_invalid_email));
            focusView = nameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, location);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isNameValid(String name) {
        return name.length() > 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String name;
        private final String locationUrl;

        UserLoginTask(String name, String locationUrl) {
            this.name = name;
            this.locationUrl = locationUrl;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            UserHandler handler = new UserHandler(getBaseContext());
            handler.open();
            handler.insertData(locationUrl, name, "1234");
            handler.close();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(SetupActivity.this, ContainerActivity.class);
                startActivity(intent);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

