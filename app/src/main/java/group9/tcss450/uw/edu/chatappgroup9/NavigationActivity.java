package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group9.tcss450.uw.edu.chatappgroup9.model.SearchFragRecylerViewAdapter;
import group9.tcss450.uw.edu.chatappgroup9.utils.SendPostAsyncTask;
import group9.tcss450.uw.edu.chatappgroup9.utils.ThemeUtil;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ChatFragment.OnFragmentInteractionListener,
        LandingFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        WeatherFragment.OnFragmentInteractionListener,
        ContactsFragment.OnFragmentInteractionListener,
        ConnectionFragment.OnFragmentInteractionListener{

    public static int mTheme = ThemeUtil.THEME_MEDITERRANEAN_BLUES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To change theme just put your theme id.
        int theme = ThemeUtil.getThemeId(mTheme);
        setTheme(theme);

        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences preferences = getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        if (preferences != null) {
            String username = preferences.getString(getString(R.string.keys_shared_prefs_username),
                    "unknown user");

            Log.e("NavigationActivity", "username: " + username);
            TextView textView = navigationView.getHeaderView(0).findViewById(R.id.navigationHeaderTextViewUsername);

            Log.e("NavigationActivity", "header : " + textView);
            textView.setText(username);
        }

        if(savedInstanceState == null) {
            if(findViewById(R.id.fragmentContainer) != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer,
                        new LandingFragment(), getString(R.string.keys_landing_fragment_tag))
                        .commit();
            }
        }






    }

    @Override
    public void onBackPressed() {
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
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_mediterranean_blues_theme) {
            changeTheme(ThemeUtil.THEME_MEDITERRANEAN_BLUES);
        }

        if (id == R.id.action_shimmering_blues_theme) {
            changeTheme(ThemeUtil.THEME_SHIMMERING_BLUES);
        }

        if (id == R.id.action_turquoise_red_theme) {
            changeTheme(ThemeUtil.THEME_TURQUOISE_WATERMELON);
        }

        if (id == R.id.action_orange_sunset_theme) {
            changeTheme(ThemeUtil.THEME_ORANGE_SUNSET);
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeTheme(int theme) {
        // Handles theme changes to activity
        mTheme = theme;
        setTheme(mTheme);

        NavigationActivity.this.recreate();

        int duration = Toast.LENGTH_SHORT;
        Context context = this.getBaseContext();
        Toast toast = Toast.makeText(context, "Changed Theme", duration);
        toast.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_landing) {
            loadFragment(new LandingFragment(), getString(R.string.keys_landing_fragment_tag));
        } else if (id == R.id.nav_chat) {
            loadFragment(new ChatFragment(), getString(R.string.keys_chat_fragment_tag));
        } else if (id == R.id.nav_connection) {
            loadFragment(new ContactsFragment(), getString(R.string.keys_contact_fragment_tag));
        } else if (id == R.id.nav_search) {
            loadFragment(new SearchFragment(), getString(R.string.keys_search_fragment_tag));
        } else if (id == R.id.nav_weather) {
            loadFragment(new WeatherFragment(), getString(R.string.keys_weather_fragment_tag));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLogout() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        prefs.edit().remove(getString(R.string.keys_shared_prefs_username));

        prefs.edit().putBoolean(
                getString(R.string.keys_prefs_stay_login),
                false)
                .apply();
        //the way to close an app programmaticaly
//        finishAndRemoveTask();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


    @Override
    public void onSearchByEmailAttempt(String theEmail) {
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_search)).build();
        JSONObject emailJSON = new JSONObject();

        try {
            emailJSON.put(getString(R.string.keys_json_email), theEmail);
            Log.e("NavigationActivity", "Put email to json" );
        } catch (JSONException theException) {
            Log.e("NavigationActivity", "Error creating JSON" + theException.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), emailJSON)
                .onPostExecute(this::handleEndOfSearch).build().execute();
    }


    @Override
    public void onSearchByUsernameAttempt(String theUsername) {
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_search)).build();
        JSONObject usernameJSON = new JSONObject();

        try {
            usernameJSON.put(getString(R.string.keys_json_username), theUsername);
        } catch (JSONException theException) {
            Log.e("NavigationActivity", "Error creating JSON" + theException.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), usernameJSON)
                .onPostExecute(this::handleEndOfSearch).build().execute();
    }

    @Override
    public void onSearchByNameAttempt(String theFirstName, String theLastName) {
//        Log.e("NavigationActivity", "Search by name");
        Uri uri = new Uri.Builder().scheme("https").appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_search)).build();
        JSONObject nameJSON = new JSONObject();

        try {
            nameJSON.put(getString(R.string.keys_json_firstname), theFirstName);
            nameJSON.put(getString(R.string.keys_json_lastname), theLastName);
        } catch (JSONException theException) {
            Log.e("NavigationActivity", "Error creating JSON" + theException.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), nameJSON)
                .onPostExecute(this::handleEndOfSearchByName).build().execute();

    }


    private void loadFragment(Fragment frag, String theFragmentTag) {
        Log.e("NavigationActivity", "" + theFragmentTag);
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag, theFragmentTag)
                .addToBackStack(null);
        ft.commit();

    }

    /**
     * Handle the search by email response. If found a user, show the user's first name and last name
     * in the result text view; otherwise show user not found.
     * @param theResponse the response return from the server
     */
    private void handleEndOfSearch(String theResponse) {
//        Log.e("NavigationActivity", "handleEndOfSearch start");
        try {
            JSONObject responseJSON = new JSONObject(theResponse);
            boolean success = responseJSON.getBoolean(getString(R.string.keys_json_success));
            RecyclerView recyclerView = findViewById(R.id.searchRecycleViewUserFound);

            if (success) {
                String username = responseJSON.get(getString(R.string.keys_json_username)).toString();
                String first = responseJSON.get(getString(R.string.keys_json_firstname)).toString();
                String last = responseJSON.get(getString(R.string.keys_json_lastname)).toString();
//                Log.e("NavigationActivity", "handleEndOfSearch success");

                String[] s = {username + ":" + first + ":" + last};
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                recyclerView.setHasFixedSize(true);

                // use a linear layout manager
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(mLayoutManager);

                // specify an adapter (see also next example)
                SearchFragRecylerViewAdapter mAdapter = new SearchFragRecylerViewAdapter(s);
                recyclerView.setAdapter(mAdapter);
                Log.e("NavigationActivity", "User found");
            }
        } catch (JSONException theException) {
            Log.e("NavigationActivity", "JSON parse error");
        }
    }

    /**
     * Handle the search by email response. If found a user, show the user's first name and last name
     * in the result text view; otherwise show user not found.
     * @param theResponse the response return from the server
     */
    private void handleEndOfSearchByName(String theResponse) {
        try {
            JSONObject responseJSON = new JSONObject(theResponse);
            boolean success = responseJSON.getBoolean(getString(R.string.keys_json_success));
            RecyclerView recyclerView = findViewById(R.id.searchRecycleViewUserFound);
            SearchFragRecylerViewAdapter mAdapter;
            if (success) {
                JSONArray users = responseJSON.getJSONArray(getString(R.string.keys_json_array_users_data));
                if (users.length() > 0) {
                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    recyclerView.setHasFixedSize(true);

                    // use a linear layout manager
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(mLayoutManager);

                    // specify an adapter (see also next example)
                    mAdapter = new SearchFragRecylerViewAdapter(jsonArrayUsersDataToStringArray(users));
                    recyclerView.setAdapter(mAdapter);
                }

                Log.e("NavigationActivity", "User found by name");
            } else {
                recyclerView.setAdapter(null);
                recyclerView.getAdapter().notifyDataSetChanged();
                Log.e("NavigationActivity", "User not found");
            }
        } catch (JSONException e) {
            Log.e("NavigationActivity", "JSON parse error" + e.getMessage());
        }
    }


    /**
     *
     * @param users the users data in Json array format
     * @return
     */
    private String[] jsonArrayUsersDataToStringArray(JSONArray users) {
        String[] msgs = new String[users.length()];
        try {
            for (int i = 0; i < users.length(); i++) {
                JSONObject msg = users.getJSONObject(i);
                String username = msg.get(getString(R.string.keys_json_username)).toString();
                String firstname = msg.get(getString(R.string.keys_json_firstname)).toString();
                String lastname = msg.get(getString(R.string.keys_json_lastname)).toString();
                msgs[i] = username + ":" + firstname + ":" + lastname;
            }
        } catch (JSONException e) {
            Log.e("NavigationActivity", "JSON parse error" + e.getMessage());
        }
        return msgs;

    }
}
