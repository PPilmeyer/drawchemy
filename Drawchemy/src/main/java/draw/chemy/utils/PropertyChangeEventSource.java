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


public interface PropertyChangeEventSource {

  public void addPropertyEventListener(PropertyChangeEventListener aListener);

  public void addPropertyEventListener(String aProperty, PropertyChangeEventListener aListener);

  public void removePropertyEventListener(PropertyChangeEventListener aListener);

  public void firePropertyChange(PropertyChangeEvent aEvent);

}
