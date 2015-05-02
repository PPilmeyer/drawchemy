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

import java.util.List;
import java.util.Vector;

public class PropertyChangeEventSupport implements PropertyChangeEventSource {

  private List<PropertyChangeEventListener> fListeners = new Vector<PropertyChangeEventListener>();

  public void addPropertyEventListener(PropertyChangeEventListener aListener) {
    fListeners.add(aListener);
  }

  public void addPropertyEventListener(String aProperty, PropertyChangeEventListener aPropertyChangeEventSupport) {
    PropertyChangeEventListenerWrapper wrapper = new PropertyChangeEventListenerWrapper(aPropertyChangeEventSupport, aProperty);
    fListeners.add(wrapper);
  }

  public void removePropertyEventListener(PropertyChangeEventListener aPropertyChangeEventListener) {
    List<PropertyChangeEventListener> toBeRemoved = new Vector<PropertyChangeEventListener>();
    fListeners.remove(aPropertyChangeEventListener);
    for (PropertyChangeEventListener listener : fListeners) {
      if (listener instanceof PropertyChangeEventListenerWrapper) {
        if (((PropertyChangeEventListenerWrapper) listener).fDelegate.equals(aPropertyChangeEventListener)) {
          toBeRemoved.add(listener);
        }
      }
    }
    fListeners.removeAll(toBeRemoved);
  }

  public void firePropertyChange(PropertyChangeEvent aEvent) {
    for (PropertyChangeEventListener listener : fListeners) {
      listener.propertyChange(aEvent);
    }
  }

  private static class PropertyChangeEventListenerWrapper implements PropertyChangeEventListener {

    private final PropertyChangeEventListener fDelegate;
    private final String fProperty;

    private PropertyChangeEventListenerWrapper(PropertyChangeEventListener aDelegate, String aProperty) {
      fDelegate = aDelegate;
      fProperty = aProperty;
    }

    @Override
    public void propertyChange(PropertyChangeEvent aEvent) {
      if (aEvent.getProperty().equals(fProperty)) {
        fDelegate.propertyChange(aEvent);
      }
    }
  }
}
