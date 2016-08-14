package cillian.cexstockwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class SetupActivity extends AppCompatActivity {

    UserHandler handler;
    ImageButton ire;
    ImageButton uk;
    ImageButton us;
    ImageButton sp;
    ImageButton ind;
    ImageButton por;
    ImageButton neth;
    ImageButton mex;
    ImageButton pol;
    ImageButton aus;
    ImageButton itl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        addListenerOnButton();

        handler = new UserHandler(getBaseContext());
        handler.open();

        if (handler.returnAmount() > 0)
        {
            Intent intent = new Intent(SetupActivity.this,MainActivity.class);
            handler.close();
            startActivity(intent);
        }
    }

    public void finishSetup(String countryUrl)
    {
        Intent intent = new Intent(SetupActivity.this,AccountRegistration.class);
        intent.putExtra("URL",countryUrl);
        startActivity(intent);
    }

    public void addListenerOnButton() {


        ire = (ImageButton) findViewById(R.id.ireland);
        uk = (ImageButton) findViewById(R.id.uk);
        us = (ImageButton) findViewById(R.id.us);
        sp = (ImageButton) findViewById(R.id.spain);
        ind = (ImageButton) findViewById(R.id.india);
        por = (ImageButton) findViewById(R.id.portugal);
        neth = (ImageButton) findViewById(R.id.netherlands);
        mex = (ImageButton) findViewById(R.id.mexico);
        pol = (ImageButton) findViewById(R.id.poland);
        aus = (ImageButton) findViewById(R.id.austrailia);
        itl = (ImageButton) findViewById(R.id.italy);

        ire.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finishSetup("ie.m.webuy.com");

            }

        });

        uk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finishSetup("uk.m.webuy.com");

            }

        });

        us.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finishSetup("us.m.webuy.com");

            }

        });

        sp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finishSetup("es.m.webuy.com");

            }

        });

        ind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finishSetup("in.m.webuy.com");

            }

        });

        por.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finishSetup("pt.m.webuy.com");

            }

        });

        neth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finishSetup("nl.m.webuy.com");

            }

        });

        mex.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finishSetup("mx.m.webuy.com");

            }

        });

        pol.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finishSetup("pl.m.webuy.com");

            }

        });

        aus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finishSetup("au.m.webuy.com");

            }

        });

        itl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finishSetup("it.m.webuy.com");

            }



        });

    }


    public void submitInfo(View v)
    {
       /*EditText nameField = (EditText)(findViewById(R.id.nameField));
        EditText emailField = (EditText)(findViewById(R.id.emailField));

        String name = nameField.getText().toString();
        String email = emailField.getText().toString();

        //testLogin(name,email);

        if(name.length() > 0)
        {
            if(email.length() > 4 && email.contains("@"))
            {
                Intent intent = new Intent(SetupActivity.this,.class);
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
        }*/
    }

    /*public void login(String username,String password)
    {
        WebView webView;
        webView = (WebView) findViewById(R.id.activity_setup_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl("https://ie.m.webuy.com/member/login");
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                final String username = "cillianmcneill@gmail.com";
                final String password = "Sadhbhyb00C3X";

                final String js = "javascript:" +
                        "document.getElementById('uname').value = '" + username + "';" +
                        "document.getElementById('pwd').value = '" + password + "';" +
                        "document.getElementById('loginBtn').click()";

                if(Build.VERSION.SDK_INT>=19)
                {
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                        }
                    });
                }

                else
                {
                    view.loadUrl(js);
                }

                findViewById(R.id.activity_setup_webview).setVisibility(View.VISIBLE);
            }
        });
    }*/

}
