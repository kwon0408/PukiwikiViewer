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

package kr.obin.dev.pukiwikiviewer.wikielements;

import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public /*abstract*/ class WikiElement
{
    // Original code is `lib/convert_html.php`

    protected WikiElement parent;
    protected ArrayList<WikiElement> wikiElements;
    protected WikiElement last;

    public WikiElement()
    {
        wikiElements = new ArrayList<>();
        last = this;
    }

    public void setParent(WikiElement parent)
    {
        this.parent = parent;
    }

    public WikiElement add(WikiElement e)
    {
        if (this.canContain(e))
        {
            return this.insert(e);
        }
        else
        {
            return this.parent.insert(e);
        }
    }

    public WikiElement insert(WikiElement e)
    {
        e.setParent(this);
        this.wikiElements.add(e);
        return (this.last = e.last);
    }

    public boolean canContain(WikiElement e)
    {
        return true;
    }

    public String wrap(String str, String tag, String param, Boolean canOmit)
    {
        // param = "", canOmit = true by default
        return (canOmit && str.equals(""))? "": ("<" + tag + param + ">" + str + "</" + tag + ">");
    }

    @Override
    public String toString()
    {
        ArrayList<String> ret = new ArrayList<>();
        for (WikiElement e: wikiElements)
        {
            ret.add(e.toString());
        }

        return TextUtils.join("\n", ret.toArray());
    }

    public String dump()
    {
        return dump(0);
    }

    public String dump(int indent)
    {
        StringBuilder ret = new StringBuilder(StringUtils.repeat(" ", indent) + this.getClass() + "\n");
        indent += 2;
        for (WikiElement e: wikiElements)
        {
            ret.append(e == null ? "" : e.dump(indent));
        }

        return ret.toString();
    }

}
