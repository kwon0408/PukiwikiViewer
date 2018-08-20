/*
 * This file is part of Pukiwiki Viewer, an assistant app for Android users
 * when they have trouble with websites using Pukiwiki.
 * Copyright (C) 2018 dev.OBiN.kr Maintenance Crew "JH Kwon"
 *
 * Pukiwiki Viewer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pukiwiki Viewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Pukiwiki Viewer.  If not, see <https://www.gnu.org/licenses/>.
 * */

package kr.obin.dev.pukiwikiviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    private String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction()))
        {
            Uri uri = intent.getData();
            Log.d("MainActivity", uri != null? uri.toString(): "uri is null");
        }

        PageParser.setUriScheme(getString(R.string.uri_scheme));

        /*
        WebView wbContent = findViewById(R.id.wbContent);
        wbContent.loadData(
                "100%4G%45",//"%46%4c%4f%4f%52",
                "text/plain",
                null);*/
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            if (doubleBackToExitPressedOnce)
            {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.activity_main_press_twice), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable()
            {

                @Override
                public void run()
                {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            if (url != null && url.startsWith(getString(R.string.uri_scheme) + "://"))
            {
                Log.d("MyWebViewClient", url);

                try
                {
                    /* Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    Log.d("MyWebViewClient", (existPackage == null? "existPackage == null": "existPackage != null"));
                    if (existPackage != null)
                        startActivity(intent);  */
                    String title = Uri.parse(url).getEncodedQuery(); // TODO: UTF-8 engine support
                    Log.d("MyWebViewClient", "title == " + title);
                    title = URLDecoder.decode(title, "EUC-JP"); // TODO: UTF-8 engine support
                    Log.d("MyWebViewClient", "title == " + title);
                    showWikiPage(view, title);
                    showTitle(title, "BemaniWiki 2ND"); // TODO: actual site name!
                    return true;
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
                view.loadUrl(url);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.app_bar_search) // search icon
        {
            Activity activity = MainActivity.this;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Title");

            // Get the layout inflater
            LayoutInflater inflater = activity.getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            final View dialogView = inflater.inflate(R.layout.search_dialog, null);
            builder.setView(dialogView);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    EditText input = dialogView.findViewById(R.id.etQuery);
                    RadioGroup rg = dialogView.findViewById(R.id.rgCriterion);

                    Log.d("MainActivity", (input == null? "input == null": "input != null"));
                    if (input != null)
                    {
                        query = input.getText().toString();
                    }
                    int selected = rg.getCheckedRadioButtonId();
                    // TextView txtContentTest = findViewById(R.id.txtContentTest);
                    WebView wbContent = findViewById(R.id.wbContent);
                    wbContent.setWebViewClient(new MyWebViewClient());

                    // TODO: apply to general PukiWiki website
                    String titleForActionBar;
                    try
                    {
                        if (selected == R.id.rbExactSearch) // EXACT selected: use query as title
                        {
                            showWikiPage(wbContent, query);
                            titleForActionBar = query;
                        }
                        else // AND/OR selected: search with the query
                        {
                            String url = "http://www.bemaniwiki.com/index.php?cmd=search";

                            String src = PageParser.getSearchData(url,
                                    "encode_hint", "%A4%D7", // TODO: UTF-8 engine support - use "%E3%81%B7": this means "ぷ"
                                    "word", URLEncoder.encode(query, "EUC-JP"),
                                    "type", (selected == R.id.rbAndSearch? "AND": "OR"));

                            // TODO: use PukiWiki Parser
                            wbContent.loadData(
                                    src,
                                    "text/html; charset=UTF-8",
                                    "UTF-8");

                            titleForActionBar = getString(R.string.action_bar_search_result) + query;
                        }

                        // Set action bar text
                        showTitle(titleForActionBar, "BemaniWiki 2nd");
                        // TODO: apply to general PukiWiki website


                        // Log.d("MainActivity", src.substring(0, Math.min(src.length(), 50)));
                    } catch (Exception e)
                    {
                        wbContent.loadData(
                                ExceptionUtils.getStackTrace(e),
                                "text/plain; charset=UTF-8",
                                "UTF-8");
                    }

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        else if (id == R.id.action_settings)
        {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera)
        {
            // Handle the camera action
        }
        else if (id == R.id.nav_gallery)
        {

        }
        else if (id == R.id.nav_slideshow)
        {

        }
        else if (id == R.id.nav_manage)
        {

        }
        else if (id == R.id.nav_share)
        {

        }
        else if (id == R.id.nav_send)
        {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showWikiPage(WebView wv, String title) throws UnsupportedEncodingException
    {
        Log.d("showWikiPage", "wv == " + wv.toString());
        Log.d("showWikiPage", "title == " + title);
        String url = String.format("http://www.bemaniwiki.com/index.php?cmd=backup&page=%s&age=2147483648&action=source",
                URLEncoder.encode(title, "EUC-JP"));  // TODO: UTF-8 engine support
        String src = PageParser.getWikiSource(url);

        // TODO: use PukiWiki Parser
        wv.loadData(
                src,
                "text/plain; charset=UTF-8",
                "UTF-8");

    }

    private void showTitle(String title, String subtitle)
    {
        ActionBar bar = MainActivity.this.getSupportActionBar();
        Log.d("getActionBar", (bar == null? "bar == null": "bar != null"));
        if (bar != null)
        {
            bar.setTitle(title);
            bar.setSubtitle(subtitle);
        }
    }
}
