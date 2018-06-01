package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import group9.tcss450.uw.edu.chatappgroup9.model.Credentials;
import group9.tcss450.uw.edu.chatappgroup9.utils.InputVerificationTool;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;

/**
 * This activity allow new user to register for the app   the user will then be sent to a new user verification fragment.
 *
 * @author Garrett Engle, Jenzel Villanueva, Cory Davis,Minqing Chen
 */
public class RegistrationActivity extends AppCompatActivity {

    private int MIN_LENGTH_USERNAME_PASSWORD = 6;
    private final String USERNAME_EMPTY = "Username cannot be empty";
    private final String USERNAME_TOO_SHORT = "Username is too short";
    private final String FIRST_NAME_EMPTY = "First name cannot be empty";
    private final String LAST_NAME_EMPTY = "Last name cannot be empty";
    private final String PASSWORD_NOT_MATCH = "Passwords are not match";
    private final String PASSWORD_TOO_SHORT = "Password is too short";
    private final String EMAIL_INVALID = "Email address is invalid";
    private final String INVALID_CHARACTER = "Invalid character contained";
    private final String PASSWORD_TOO_SIMPLE = "Password is too simple";
    private final String USERNAME_EXIST = "Username has already exist";
    private final String EMAIL_EXIST = "Email has already registered";

    private EditText myUsername;
    private EditText myPassword;
    private EditText myConfirmPassword;
    private EditText myEmail;
    private EditText myFirstName;
    private EditText myLastName;

    private int myVerificationPin;

    /**
     * creates and initializes the state of the activity and   creates the layout for the activity
     * @param savedInstanceState the state of the last time this activity was called
     */
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        myUsername = findViewById(R.id.registrationEditTextUsername);
        myPassword = findViewById(R.id.registrationEditTextPassword);
        myConfirmPassword = findViewById(R.id.registrationEditTextConfirmPassword);
        myEmail = findViewById(R.id.registrationEditTextEmail);
        myFirstName = findViewById(R.id.registrationEditTextFirstName);
        myLastName = findViewById(R.id.registrationEditTextLastName);
        myVerificationPin = verificationPinGenerator();

        Button submit = findViewById(R.id.registrationButtonSubmit);
        submit.setOnClickListener(this::submitOnClicked);

    }

    /**
     * creates the  on click listener for the submition button of the registration page.
     * Build a credentials object and then called the register attempt method.
     *
     * @param theSubmitButton  this is the view of the submit button that will send the registration to the DB
     */
    public void submitOnClicked(View theSubmitButton){
        if (isRegistrationInfoGood(theSubmitButton)) {
            final Credentials info = new Credentials.Builder(myUsername.getText().toString(),
                    myPassword.getEditableText())
                    .addEmail(myEmail.getText().toString())
                    .addFirstName(myFirstName.getText().toString())
                    .addLastName(myLastName.getText().toString())
                    .addVerification(myVerificationPin).build();

            onRegisterAttempt(info);
        }
    }

    /**
     * This method must be called before submit onRegisterAttempt() to make sure all registration
     * information meet the requirement.
     * @return true if the registration information is ready to for the server; false otherwise.
     */
    private boolean isRegistrationInfoGood(View theSubmitButton) {
        boolean result = true;
        String email = myEmail.getText().toString();
        String firstname = myFirstName.getText().toString();
        String lastname = myLastName.getText().toString();
        String username = myUsername.getText().toString();
        String password = myPassword.getText().toString();
        String confirmPassword = myConfirmPassword.getText().toString();

        if (!InputVerificationTool.isEmail(email)) {
            result = false;
            myEmail.setError(EMAIL_INVALID);
        }

        if (TextUtils.isEmpty(firstname)) {
            result = false;
            myFirstName.setError(FIRST_NAME_EMPTY);
        }else if (!InputVerificationTool.isName(firstname)) {
            result = false;
            myFirstName.setError(INVALID_CHARACTER);
        }

        if (TextUtils.isEmpty(lastname)) {
            result = false;
            myLastName.setError(LAST_NAME_EMPTY);
        } else if (!InputVerificationTool.isName(lastname)) {
            result = false;
            myLastName.setError(INVALID_CHARACTER);
        }

        if (TextUtils.isEmpty(username)) {
            result = false;
            myUsername.setError(USERNAME_EMPTY);
        } else if (!InputVerificationTool.isUsername(username)) {
            result = false;
            myUsername.setError(INVALID_CHARACTER);
        } else if (username.length() < MIN_LENGTH_USERNAME_PASSWORD) {
            result = false;
            myUsername.setError(USERNAME_TOO_SHORT);
        }

        //password and confirm password check
        if (!password.equals(confirmPassword)) {
            result = false;
            myPassword.setError(PASSWORD_NOT_MATCH);
            myConfirmPassword.setError(PASSWORD_NOT_MATCH);
        } else if (password.length() < MIN_LENGTH_USERNAME_PASSWORD) {
            result = false;
            myPassword.setError(PASSWORD_TOO_SHORT);
            myConfirmPassword.setError(PASSWORD_TOO_SHORT);
        } else if (!InputVerificationTool.isPassword(password)) {
            result = false;
            myPassword.setError(PASSWORD_TOO_SIMPLE);
            myConfirmPassword.setError(PASSWORD_TOO_SIMPLE);
        }

        return result;
    }

    /**
     * Attempts to submit the registration to the server. It will fail when username or email has
     * already registered.
     * @param registrationInfo
     */
    private void onRegisterAttempt(final Credentials registrationInfo) {

        //build the web server URL
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_register)).build();

        //build the JSON object
        JSONObject msg = registrationInfo.asJSONObject();

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleRegistrationOnPost)
                .build().execute();
    }


    /**
     * handles the registration response returned from the server.
     * the response is in JSON format.
     * @param theResult the JSON formatted String response from the web service
     */
    private void handleRegistrationOnPost(final String theResult) {
        try {
            JSONObject resultJSON = new JSONObject(theResult);
            boolean success = resultJSON.getBoolean(getString(R.string.keys_json_success));
            String failReason = null;

            if (!success) {
               failReason = resultJSON.getJSONObject(getString(R.string.keys_json_error))
                       .getString(getString(R.string.keys_json_detail));
            }

            if (success) {
                Toast.makeText(getApplicationContext(), myEmail.getText().toString()
                        + ", Please verify your pin", Toast.LENGTH_LONG).show();

                toVerificationActivity();

            } else if (failReason.contains(getString(R.string.keys_json_username))) {
                myUsername.setError(USERNAME_EXIST);

            } else if (failReason.contains(getString(R.string.keys_json_email))) {
                myEmail.setError(EMAIL_EXIST);

            } else {
                Log.e("Registration Activity","Registration fail reason: " + failReason);
            }

        } catch (JSONException e) {
            Log.e("JSON parse error",theResult + System.lineSeparator()
                    + e.getMessage()+e.getLocalizedMessage());
        }
    }

    /**
     *  Loads the  mainactivity  to replace   this activity on back press
     */
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    /**
     *  On successful registration we load the verification fragment that verifies we have a valid email for submission.
     */
    public void toVerificationActivity() {
        Intent intent = new Intent(this, VerificationActivity.class);

        String getUsername = myUsername.getText().toString();

        //Create the bundle; pass in username so Verification knows how to verify pin
        Bundle bundle = new Bundle();

        //Add your data to bundle
        bundle.putString("username", getUsername);

        //Add the bundle to the intent
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
    }

    /**
     * On Verification settings call generates a pin to add to the database for verification.
     * @return
     */
    public int verificationPinGenerator() {
        Random random = new Random();

        int value = random.nextInt(10000);
        return value;
    }

}

