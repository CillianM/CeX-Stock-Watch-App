package ie.cex;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import ie.cex.connectivity.DetectConnection;
import ie.cex.connectivity.ProductGrabber;
import ie.cex.fragments.ProfileFragment;
import ie.cex.fragments.ScanningFragment;
import ie.cex.fragments.WebFragment;
import ie.cex.handlers.DatabaseHandler;

public class ContainerActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    int count = 0;
    FloatingActionMenu menu;
    FloatingActionButton watch;
    FloatingActionButton scan;
    FloatingActionButton user;

    // Arraylist of custom nav items for side bar
    List<NavItem> mNavItems = new ArrayList<>();
    private boolean watchRemoved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        mNavItems.add(new NavItem("Website", "View The Cex Website", R.drawable.web));
        mNavItems.add(new NavItem("Sell", "Scan In Items To Sell", R.drawable.cash));
        mNavItems.add(new NavItem("Profile", "Scan In Items To Sell", R.drawable.user));

        menu = (FloatingActionMenu) findViewById(R.id.menu);
        user = (FloatingActionButton) findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFragment(2);
            }
        });
        watch = (FloatingActionButton) findViewById(R.id.watch);
        watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                watchItem();
            }
        });
        scan = (FloatingActionButton) findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFragment(1);
            }
        });
        selectFragment(0);
    }

    private void watchItem() {
        String url = "";
        WebFragment webFragment = (WebFragment) getFragmentManager().findFragmentByTag("WEB");
        if (webFragment != null && webFragment.isVisible()) {
            url = webFragment.getUrl();
        }

        if (!url.contains("product-detail"))
            Toast.makeText(getBaseContext(), "Can only add item pages to watchlist!", Toast.LENGTH_LONG).show();

        else {
            if (DetectConnection.checkInternetConnection(ContainerActivity.this)) {
                try {
                    ProductGrabber grabber = new ProductGrabber(new DatabaseHandler(getBaseContext()), url);
                    grabber.execute();
                    Toast.makeText(getBaseContext(), "Item added to watchlist!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "An error occured please try again later", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getBaseContext(), "No Internet! Try Again Later", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        WebFragment webFragment = (WebFragment) getFragmentManager().findFragmentByTag("WEB");
        if (webFragment != null && webFragment.isVisible()) {
            webFragment.onBackPressed();
        } else {
            selectFragment(0);
        }

    }

    public void hideFab() {
        menu.hideMenu(true);
    }

    public void showFab() {
        menu.showMenu(true);
    }

    public void addWatch() {
        if (watchRemoved) {
            menu.addMenuButton(watch);
            watchRemoved = false;
        }
    }

    public void removeWatch() {
        menu.removeMenuButton(watch);
        watchRemoved = true;
    }

    public void closeFab() {
        menu.close(false);
    }

    private void selectFragment(int position) {
        Fragment fragment = null; //initialize empty fragment
        count = 0;
        closeFab();
        if (position == 0) {
            fragment = new WebFragment();
            //Replace current fragment
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment, "WEB")
                    .commit();
            addWatch();
        } else if (position == 1) {
            fragment = new ScanningFragment();
            //Replace current fragment
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment, "SCAN")
                    .commit();
            removeWatch();
        } else if (position == 2) {
            fragment = new ProfileFragment();
            //Replace current fragment
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment, "PROFILE")
                    .commit();
            removeWatch();
        }

        setTitle(mNavItems.get(position).getmTitle());
    }


}
