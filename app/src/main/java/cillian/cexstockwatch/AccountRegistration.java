package cillian.cexstockwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class AccountRegistration extends AppCompatActivity {

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_registration);
        Intent intent = getIntent();
        url = intent.getStringExtra("URL");


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
            handler.insertData(url, email, pass, "Skipped");
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
