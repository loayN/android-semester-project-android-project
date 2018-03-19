package a2lend.app.com.a2lend;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class HomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static ProgressDialog progressDialog;
    public static RelativeLayout frameLayout,layoutButtons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        // find ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Find HomeFragment The Default Fragment -
        frameLayout = (RelativeLayout)findViewById(R.id.HomeLayout);
        frameLayout.setVisibility(View.VISIBLE); // VISIBLE  Fragment

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //region Menu Button - Open Navigation
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                //User Profile
                FirebaseUser user = DataAccess.getUser();
                //DataAccess.refresh();
                DataAccess.myListItems.clear();
                if(user.isAnonymous()){
                    ((TextView) findViewById(R.id.header_username)).setText("Anonymous");
                    ((TextView) findViewById(R.id.header_email)).setText("Anonymou@Anonymous.com");
                }else {
                    ((TextView) findViewById(R.id.header_username)).setText(user.getDisplayName());
                    ((TextView) findViewById(R.id.header_email)).setText(user.getEmail());
                    ((ImageView) findViewById(R.id.header_imageProfile)).setImageURI(user.getPhotoUrl());
                }
            }
        });
        //endregion

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        Log.d("onBackPressed","Press");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        Log.d("CreateOptionsMenu",menu.toString());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("onOptionsItemSelected",item.toString()+"  - " + id);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    // Handle navigation view item clicks here.
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.d("OnN-ItemSelected",item.toString()+"  - " + id);

        if (id == R.id.nav_profile) {
            MySupport.goToFragment(new ProfileFragment(),this);
        } else if (id == R.id.nav_myList) {
            MySupport.goToFragment(new MyListItemsFragment(),this);
        } else if (id == R.id.nav_search) {
           // fragment = new SearchFragment();
            MySupport.goToFragment(new SearchFragment(),this);
        } else if (id == R.id.nav_manage) {
            MySupport.goToFragment(new ControlPanelFragment(),this);
        } else if (id == R.id.nav_share) {
            //Todo Check
            MySupport.goToFragment(new HomeFragment(),this);
            frameLayout.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_send) {
            //Todo Check
            MySupport.goToFragment(new HomeFragment(),this);
            frameLayout.setVisibility(View.VISIBLE);
        }else if (id == R.id.nav_about) {
            MySupport.goToFragment(new AboutFragment(),this);

            // Todo Delete 2 Rows
           // Intent intent = new Intent(this,MapsActivity.class);
           // startActivity(intent);
        }else if (id == R.id.nav_addItem) {
            MySupport.goToFragment(new SelectImage(),this);
        }else if (id == R.id.nav_sginout) {
            finish();
            DataAccess.auth.signOut();
            //System.exit(0);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //region Buttons View view To Change FragmentLayout

    public void update(View view) throws InterruptedException {

        EditText nameEditText = (EditText) findViewById(R.id.ProfileUserName);
        EditText emailEditText = (EditText) findViewById(R.id.ProfileUserName);
        EditText phoneEditText = (EditText) findViewById(R.id.ProfileUserName);
        EditText Password = (EditText) findViewById(R.id.Password);

        DataAccess.UpdateUser(
                nameEditText.getText().toString()
                ,emailEditText.getText().toString()
                ,Password.getText().toString()
                ,phoneEditText.getText().toString()
                );

        Thread.sleep(500);
        MySupport.goToFragment(new ProfileFragment(),this);
        Toast.makeText(this, "Updating Successfully", Toast.LENGTH_SHORT).show();
    }

    public void intentToUpdateFragment(View view) {
        MySupport.goToFragment(new ProfileUpdateFragment(),this);
    }

    public void GoHomePageButton(View view) {
        MySupport.goToFragment(new HomeFragment(),this);

    }

    public void GoMyListItemButton(View view) {
        MySupport.goToFragment(new MyListItemsFragment(),this);

    }

    public void SearchByNameButton(View view) {
        MySupport.goToFragment(new SearchFragment(),this);

    }

    public void SearchByLocationButton(View view) {
        MySupport.goToFragment(new SearchByLocation(),this);

    }

    public void AddNewItemButton(View view) {
        MySupport.goToFragment(new SelectImage(),this);

    }

    public void AboutButton(View view) {
        MySupport.goToFragment(new AboutFragment(),this);

    }

    public void LogoutButton(View view) {
        finish();
        DataAccess.auth.signOut();
        System.exit(0);
    }

    public void SearchByLocationButtun(View view) {
        MySupport.goToFragment(new SearchByLocation(),this);

    }

    public void GoControlPanelButton(View view) {
        MySupport.goToFragment(new ControlPanelFragment(),this);

    }

    //endregion Buttons
}
