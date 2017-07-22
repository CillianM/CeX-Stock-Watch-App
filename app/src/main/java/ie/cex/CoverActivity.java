package ie.cex;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import ie.cex.handlers.UserHandler;

public class CoverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);

        Intent i;

        if (userExists()) {
            i = new Intent(CoverActivity.this, SetupActivity.class);
        } else {
            i = new Intent(CoverActivity.this, ContainerActivity.class);
        }
        final Intent intent = i;
        int timeOut = 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, timeOut);
    }

    private boolean userExists() {
        UserHandler handler = new UserHandler(getBaseContext());
        handler.open();
        int amount = handler.returnAmount();
        handler.close();
        return amount == 0;
    }
}
