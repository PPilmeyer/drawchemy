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
import android.text.Html;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getClick(item.getItemId());
        return true;
    }

    private void getClick(int aId) {
        switch (aId) {
            case R.id.i_line:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_line)));
                break;
            case R.id.i_scraw:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_scrawl)));
                break;
            case R.id.i_ribbon:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_ribbon)));
                break;
            case R.id.i_splatter:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_splatter)));
                break;
            case R.id.i_xshape:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_xshape)));
                break;
            case R.id.i_paintbrush:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_paint_brush)));
                break;
            case R.id.i_ball:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_ball)));
                break;
            case R.id.i_basic_shapes:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_basic_shapes)));
                break;
            case R.id.i_straigtline:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_straight_line)));
                break;
            case R.id.i_web_brush:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_web_brush)));
                break;
            case R.id.i_sketch_brush:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_sketch_brush)));
                break;
            case R.id.i_xshape2:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_xshape_v2)));
                break;
            case R.id.help_fill:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_fill)));
                break;
            case R.id.help_stroke:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_stroke)));
                break;
            case R.id.help_mirror:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_mirrors)));
                break;
            case R.id.help_kaleidoscope:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_kaleidoscope)));
                break;
            case R.id.help_gradient:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_gradient)));
                break;
            case R.id.help_unicolor:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_unicolor)));
                break;
            case R.id.help_load:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_load)));
                break;
            case R.id.help_save:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_save)));
                break;
            case R.id.help_share:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_share)));
                break;
            case R.id.help_clear:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_clear)));
                break;
            case R.id.help_undo:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_undo)));
                break;
            case R.id.help_zoom:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_zoom_pan)));
                break;
            case R.id.help_one:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_reset_view)));
                break;
            case R.id.help_logo:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_hide)));
                break;
            case R.id.help_colors:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_colors)));
                break;
            case R.id.help_settings:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_settings)));
                break;
            case R.id.help_extra:
                fTextView.setText(Html.fromHtml(getString(R.string.descr_extra)));
                break;
            default:
                break;
        }

    }
}
