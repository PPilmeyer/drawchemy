# drawchemy
*Drawchemy* is a open abstract drawing application for Android devices, especially tablets. It is inspired of the desktop application *Al.Chemy* (http://al.chemy.org/). With Drawchemy, you can create colorful drawings with few strokes. You can use Drawchemy to make finished artworks or sketchs for your favorite drawing software.

Liberate your creativity with Drawchemy.

You can see some artworks on the dedicated *Tumblr* (http://drawchemy.tumblr.com/). 
Don't hesite to send me your art to publish it. 

The current features are :

  * Drawing with several "brushes"
  * Drawing lines or shapes.
  * Color selection with opacity.
  * Effects (mirror, gradient, color with a random hue)
  * Zoom and pan
  * Save in PNG and share your drawing
  * load images as background

Drawchemy is under *License GPLv3*.
The minimum Android version is *honeycomb* 3.0

Link to *Google Play* : https://play.google.com/store/apps/details?id=draw.chemy

## Import in Android Studio
the root directory is not a android studio project, it is a directory which contains an android studio project.

1. `File > Open`
2. `Project Structure > Project` and set the SDK
3. `Project Structure > Module > Add > New Module > Application Module`
4. Add the drawchemy/Drawchemy/src/main/java as source dir and  drawchemy/Drawchemy/src/main/res as resources
5. Go to the Android Module you created at step 3 and correct the manifest location, the resources directory in the `Structure` tab and the directory for generated sources in `Generated Sources`
6. Apply

This has been tested on Android Studio 2.1.1 on Linux Mint Rebecca
