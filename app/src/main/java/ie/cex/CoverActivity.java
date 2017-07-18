package ie.cex;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ie.cex.handlers.UserHandler;

public class CoverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);

        //Wait the timeout then move on to home activity
        int timeOut = 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createUser();
                Intent i = new Intent(CoverActivity.this, ContainerActivity.class);
                startActivity(i);
                finish();
            }
        }, timeOut);
    }

    private void createUser() {
        UserHandler handler = new UserHandler(getBaseContext());
        handler.open();
        if (handler.returnAmount() < 1) {
            try {
                handler.insertData("TEST", "TEST", "TEST");
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        }
        handler.close();
    }
}
