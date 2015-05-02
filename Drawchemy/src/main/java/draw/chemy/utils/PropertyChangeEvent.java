/*
 * This file is part of the Drawchemy project - https://github.com/PPilmeyer/drawchemy
 *
 * Copyright (c) 2015 Pilmeyer Patrick
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

package draw.chemy.utils;


public class PropertyChangeEvent {

  private final String fProperty;
  private final Object fSource;
  private final Object fOldValue;
  private final Object fNewValue;

  public String getProperty() {
    return fProperty;
  }

  public Object getSource() {
    return fSource;
  }

  public Object getOldValue() {
    return fOldValue;
  }

  public Object getNewValue() {
    return fNewValue;
  }

  public PropertyChangeEvent(String aProperty, Object aSource, Object aOldValue, Object aNewValue) {
    fProperty = aProperty;
    fSource = aSource;
    fOldValue = aOldValue;
    fNewValue = aNewValue;
  }
}
