package cillian.cexstockwatch;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AccountRegistration extends AppCompatActivity {

    String url;
    boolean loginFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_registration);
        Intent intent = getIntent();
        url = intent.getStringExtra("URL");

        String failure = intent.getStringExtra("FAIL");
        if (failure != null)
        {
            loginFailed = true;
        }

        if(loginFailed)
        {
            TextView loadingText = (TextView)findViewById(R.id.description);
            loadingText.setText("Login Failed");
        }

    }

    public void skipSetup(View view)
    {
        UserHandler handler = new UserHandler(getBaseContext());
        handler.open();
        try {
            handler.insertData(url, "Skipped", "Skipped", "Skipped");
            Intent intent = new Intent(AccountRegistration.this,MainActivity.class);
            startActivity(intent);
        }
        catch (Exception e)
        {

        }
        int tmpint = handler.returnAmount();
        handler.close();
    }

    public void submitInfo(View view)
    {
        EditText nameField = (EditText)(findViewById(R.id.passwordField));
        EditText emailField = (EditText)(findViewById(R.id.emailField));

        String pass = nameField.getText().toString();
        String email = emailField.getText().toString();

        try
        {
            pass = Crypto.encrypt(email, pass);
            String tmp = Crypto.decrypt(email, pass);
            UserHandler handler = new UserHandler(getBaseContext());
            handler.open();


            if(loginFailed)
            {
                String oldEmail = "Skipped";
                String oldPass = "Skipped";
                Cursor c1 = handler.returnData();
                if (c1.moveToFirst()) {
                    do {
                        url = c1.getString(0);
                        oldEmail = c1.getString(1);
                        try {
                            oldPass = c1.getString(2);
                            oldPass = Crypto.decrypt(oldEmail,oldPass);
                        } catch (Exception e) {

                        }
                    }
                    while (c1.moveToNext());
                }
                pass = Crypto.encrypt(email,pass);
                handler.updateEmail(oldEmail,email);
                handler.updatePassword(oldPass,pass);
            }
            else
            {
                handler.insertData(url, email, pass, "Skipped");

            }
            int tmpint = handler.returnAmount();
            handler.close();
            Intent intent = new Intent(AccountRegistration.this,MainActivity.class);
            startActivity(intent);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }

}
