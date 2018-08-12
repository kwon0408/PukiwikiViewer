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

public class Paragraph extends WikiElement
{
    // Original code is `lib/convert_html.php`
    String param;

    public Paragraph(String str, String param)
    {
        super();
        this.param = param;
        if (str.equals(""))
            return;

        if (str.substring(0, 1).equals("~"))
            str = " " + str.substring(1);
        this.insert(ElementFactory.makeInline(str));
    }

    @Override
    public boolean canContain(WikiElement e)
    {
        return e instanceof Inline || e != null;
    }

    @Override
    public String toString()
    {
        return this.wrap(super.toString(), "p", this.param, true);
    }
}
