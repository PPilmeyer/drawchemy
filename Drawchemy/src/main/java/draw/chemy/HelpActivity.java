/*
 * This file is part of the Drawchemy project - https://code.google.com/p/drawchemy/
 *
 * Copyright (c) 2014 Pilmeyer Patrick
 *
 * Drawchemy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Drawchemy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Drawchemy.  If not, see <http://www.gnu.org/licenses/>.
 */


package draw.chemy;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.TextView;

import org.al.chemy.R;

public class HelpActivity extends Activity {

    private TextView fTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view);
        fTextView = (TextView) findViewById(R.id.help_textview);
        fTextView.setText("fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf fefezf");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        MenuItem item = menu.findItem(R.id.help_brushes);
        SubMenu subMenu;
        if (item != null) {
            subMenu = item.getSubMenu();
            getMenuInflater().inflate(R.menu.creators_menu, subMenu);
        }
        return true;
    }
}
