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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import org.al.chemy.R;

public class MirrorSpinnerAdapter extends ArrayAdapter<MirrorSpinnerAdapter.MIRROR> {

  public enum MIRROR {
    NONE(R.drawable.no_flip, PaintState.MIRROR.None),
    VERTICAL(R.drawable.flip_vertical, PaintState.MIRROR.Vertical),
    HORIZONTAL(R.drawable.flip_horizontal, PaintState.MIRROR.Horizontal),
    BOTH(R.drawable.flip_h_v, PaintState.MIRROR.Both),
    KALEIDOSCOPE(R.drawable.kaleidoscope, PaintState.MIRROR.Kaleidoscope);

    private final int fDrawableID;
    private final PaintState.MIRROR fState;

    MIRROR(int aDrawableID, PaintState.MIRROR aState) {
      fDrawableID = aDrawableID;
      fState = aState;
    }

    public int getDrawable() {
      return fDrawableID;
    }

    public PaintState.MIRROR getState() {
      return fState;
    }
  }

  public MirrorSpinnerAdapter(Context context, int textViewResourceId) {
    super(context, textViewResourceId, MIRROR.values());
  }

  @Override
  public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
    return getCustomView(position, cnvtView, prnt);
  }

  @Override
  public View getView(int pos, View cnvtView, ViewGroup prnt) {
    return getCustomView(pos, cnvtView, prnt);
  }

  public View getCustomView(int position, View convertView, ViewGroup parent) {
    ImageView imageView = new ImageView(getContext());
    int drawable = MIRROR.values()[position].getDrawable();
    imageView.setImageResource(drawable);
    return imageView;
  }

  public static class MirrorListener implements AdapterView.OnItemSelectedListener {

    private PaintState fPaintState;

    public MirrorListener(PaintState aPaintState) {
      fPaintState = aPaintState;
    }

    @Override
    public void onItemSelected(AdapterView<?> aAdapterView, View aView, int i, long l) {
      fPaintState.setMirrorState(MIRROR.values()[i].getState());
    }

    @Override
    public void onNothingSelected(AdapterView<?> aAdapterView) {
      fPaintState.setMirrorState(PaintState.MIRROR.None);
    }
  }

}
