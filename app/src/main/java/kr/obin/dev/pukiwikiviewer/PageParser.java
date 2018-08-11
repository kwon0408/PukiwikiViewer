/*
 * This file is part of Pukiwiki Viewer, an assistant app for Android users
 * when they have trouble with websites using Pukiwiki.
 * Copyright (C) 2018 dev.OBiN.kr Maintenance Crew "JH Kwon"
 *
 * Pukiwiki Viewer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PageParser
{
    private static String src;
    private static Connection.Response response = null;

    private PageParser(String langLocale)
    {

    }

    private static class MyAsync extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground(String... strings)
        {
            Log.i("MyAsync", "path is " + strings[0]);
            try
            {
                response = Jsoup.connect(strings[0])
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
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

    public static int getStatus()
    {
        return response.statusCode();
    }

    public static String getWikiSource(final String path)
    {
        MyAsync async = new MyAsync();
        async.execute(path);

        try
        {
            return async.get();
        } catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
            return ExceptionUtils.getStackTrace(e);
        }
    }
}
