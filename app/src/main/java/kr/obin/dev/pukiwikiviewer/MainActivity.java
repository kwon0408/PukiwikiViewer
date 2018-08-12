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
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.net.URLEncoder;
import java.security.InvalidParameterException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                Activity activity = MainActivity.this;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Title");

                // Get the layout inflater
                LayoutInflater inflater = activity.getLayoutInflater();

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                final View dialogView = inflater.inflate(R.layout.search_dialog, null);
                builder.setView(dialogView)
                        // Add action buttons
                        .setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                EditText input = dialogView.findViewById(R.id.etQuery);
                                RadioGroup rg = dialogView.findViewById(R.id.rgCriterion);

                                Log.d("MainActivity", Boolean.toString(input != null));
                                m_Text = input.getText().toString();
                                int selected = rg.getCheckedRadioButtonId();
                                // TextView txtContentTest = findViewById(R.id.txtContentTest);
                                WebView wbContent = findViewById(R.id.wbContent);
                                String url = null;

                                // TODO: apply to general PukiWiki website
                                try
                                {
                                    if (selected == R.id.rbExactSearch) // use query as title
                                    {
                                        url = String.format("http://www.bemaniwiki.com/index.php?cmd=backup&page=%s&age=2147483648&action=source",
                                                URLEncoder.encode(m_Text, "EUC-JP"));
                                        String src = PageParser.getWikiSource(url);

                                        // TODO: use PukiWiki Parser
                                        wbContent.loadData(
                                                src,
                                                "text/plain; charset=UTF-8",
                                                "UTF-8");
                                    }
                                    else // search with the query
                                    {
                                        url = "http://www.bemaniwiki.com/index.php?cmd=search";

                                        String src = PageParser.getSearchData(url,
                                                "encode_hint", "%A4%D7", // TODO: UTF-8 engine support - use "%E3%81%B7": this means "ぷ"
                                                "word", URLEncoder.encode(m_Text, "EUC-JP"),
                                                "type", (selected == R.id.rbAndSearch ? "AND" : "OR"));

                                        // TODO: use PukiWiki Parser
                                        wbContent.loadData(
                                                src,
                                                "text/html; charset=UTF-8",
                                                "UTF-8");

                                    }

                                    // Log.d("MainActivity", src.substring(0, Math.min(src.length(), 50)));
                                } catch (Exception e)
                                {
                                    wbContent.loadData(
                                            ExceptionUtils.getStackTrace(e),
                                            "text/plain; charset=UTF-8",
                                            "UTF-8");
                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
