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

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class PageParser
{
    private static String src;
    private static Connection connection = null;
    private static Connection.Response response = null;
    private static String USER_AGENT
            = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) PukiWikiViewer/0.0.0.0 Chrome/68.0.3440.106 Safari/537.36";

    private PageParser()
    {

    }

    private static class PreGettingAsync extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground(String... strings)
        {
            Log.i("PreGettingAsync", "path is " + strings[0]);
            try
            {
                response = Jsoup.connect(strings[0])
                        .userAgent(USER_AGENT)
                        .timeout(10000)
                        .execute();

                Document doc = response.parse();
                Elements pre = doc.getElementsByTag("pre");

                if (pre.size() > 0)
                {
                    return pre.first().text();
                }
                else
                {
                    return "No <pre> tag found";
                }
            } catch (IOException e)
            {
                e.printStackTrace();
                return ExceptionUtils.getStackTrace(e);
            }
        }
    }

    private static class SearchAsync extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground(String... strings)
        {
            Log.i("SearchAsync", strings.toString());
            try
            {
                StringBuilder result = new StringBuilder();
                connection = Jsoup.connect(strings[0])
                        .userAgent(USER_AGENT);
                Log.d("SearchAsync",  "connection created");

                for (int i = 1; i < strings.length; i += 2)
                {
                    connection.data(strings[i], strings[i + 1]);
                }
                Log.d("SearchAsync",  "data submitted");

                Document doc = connection.post();
                Log.d("SearchAsync",  "post completed");

                Elements ul = doc.getElementsByTag("ul");

                if (ul.size() > 0)
                {
                    Log.d("SearchAsync",  "<ul> found");
                    Elements li = ul.first().select("li");
                    if (li.size() > 0)
                    {
                        String line;
                        Log.d("SearchAsync",  "<li> found, size == " + li.size());
                        for (Element e : li)
                        {
                            Log.d("SearchAsync",  e.text());
                            // line = e.text() + "<br>";
                            line = String.format("<a href=\"pwv://www.bemaniwiki.com/index.php?%s\">%s</a><br />", // TODO: general PukiWiki address
                                    URLEncoder.encode(e.text(), "EUC-JP"),
                                    e.text()
                                    );
                            result.append(line);
                        }
                        Log.d("SearchAsync",  result.toString());
                        return result.toString();
                    }
                    else
                    {
                        return "No <li> tag found";
                    }
                }
                else
                {
                    return "No <ul> tag found";
                }
            } catch (IOException e)
            {
                e.printStackTrace();
                return ExceptionUtils.getStackTrace(e);
            }
        }
    }

    public static int getStatus()
    {
        return response.statusCode();
    }

    public static String getWikiSource(final String url)
    {
        PreGettingAsync async = new PreGettingAsync();
        async.execute(url);

        try
        {
            return async.get();
        } catch (Exception e)
        {
            e.printStackTrace();
            return ExceptionUtils.getStackTrace(e);
        }
    }

    public static String getSearchData(final String... criteria)
    {
        SearchAsync async = new SearchAsync();
        async.execute(criteria);

        try
        {
            return async.get();
        } catch (Exception e)
        {
            e.printStackTrace();
            return ExceptionUtils.getStackTrace(e);
        }
    }
}
