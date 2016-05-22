package cillian.cexstockwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

public class SetupActivity extends AppCompatActivity {

    UserHandler handler;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        submit = (Button)(findViewById(R.id.submitButton));

        handler = new UserHandler(getBaseContext());
        handler.open();

        if (handler.returnAmount() > 0)
        {
            Intent intent = new Intent(SetupActivity.this,MainActivity.class);
            handler.close();
            startActivity(intent);
        }
    }

    public void skipSetup(View v)
    {
        UserHandler handler = new UserHandler(getBaseContext());
        handler.open();
        handler.insertData("User", "none", "Skipped");
        handler.close();
        Intent intent = new Intent(SetupActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void submitInfo(View v)
    {
        EditText nameField = (EditText)(findViewById(R.id.nameField));
        EditText emailField = (EditText)(findViewById(R.id.emailField));

        String name = nameField.getText().toString();
        String email = emailField.getText().toString();

        if(name.length() > 0)
        {
            if(email.length() > 4 && email.contains("@"))
            {
                Intent intent = new Intent(SetupActivity.this,CardRegistrationActivity.class);
                intent.putExtra("NAME",name);
                intent.putExtra("EMAIL", email);
                startActivity(intent);
            }

            else
            {
                Toast.makeText(getBaseContext(), "Please enter a valid email", Toast.LENGTH_LONG).show();
            }

        }

        else
        {
            Toast.makeText(getBaseContext(), "Please enter a valid name", Toast.LENGTH_LONG).show();
        }
    }

}
