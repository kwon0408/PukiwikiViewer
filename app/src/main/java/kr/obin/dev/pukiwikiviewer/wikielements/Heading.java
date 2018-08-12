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

import org.apache.commons.lang3.StringUtils;

public class Heading extends WikiElement
{
    // Original code is `lib/convert_html.php`

    /* TODO
    int level;
    int id;
    String msgTop;

    public Heading(WikiElement root, String str)
    {
        super();

        this.level = Math.min(3, StringUtils.countMatches(str, "*"));

        str = root.getAnchor(str, this.level)[0];
        this.msgTop = root.getAnchor(str, this.level)[1];
        this.id = root.getAnchor(str, this.level)[2];
        // TODO: is `root` instance of `Body`?
        this.insert(ElementFactory.makeInline(str));
        ++this.level;
    }

    @Override
    public WikiElement insert(WikiElement e)
    {
        super.insert(e);
        return (this.last = this);
    }

    @Override
    public boolean canContain(WikiElement e)
    {
        return false;
    }

    @Override
    public String toString()
    {
        return msgTop + wrap(super.toString(), "h" + level, String.format( "id=\"%s\"", id), true);
    }
    */
}
