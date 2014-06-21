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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import org.al.chemy.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public class FileUtils {

    private DrawManager fManager;

    public FileUtils(DrawManager aManager) {
        fManager = aManager;
    }

    public void save(Context aContext) {
        new SavingImageTask(aContext, fManager.getBitmap(), false).execute();
    }

    public void share(Context aContext) {
        new SavingImageTask(aContext, fManager.getBitmap(), true).execute();
    }

    public class SavingImageTask extends AsyncTask<Object, Integer, Boolean> {

        private final Bitmap fBitmap;
        private final Context fContext;
        private final boolean fShare;
        private File fFile;

        public SavingImageTask(Context aContext, Bitmap aBitmap, boolean aShare) {
            fContext = aContext;
            fBitmap = aBitmap;
            fShare = aShare;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {

            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "DRAWCHEMY");
            try {
                if (!dir.exists()) {
                    if (!dir.mkdir()) {
                        throw new Exception("couldn't create the directory");
                    }
                }

                fFile = new File(dir, UUID.randomUUID().toString() + ".png");
                OutputStream outStream = new FileOutputStream(fFile);
                fBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.close();

            } catch (Exception e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            MediaScannerConnection.scanFile(fContext,
                    new String[]{fFile.toString()}, new String[]{"image/png"},
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    }
            );
            if (aBoolean) {
                Toast.makeText(fContext, fContext.getResources().getString(R.string.save), Toast.LENGTH_SHORT).show();
                if (fShare) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                     intent.setDataAndType(Uri.fromFile(fFile), "image/png");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fFile));
                    fContext.startActivity(Intent.createChooser(intent, "share"));
                }
                super.onPostExecute(true);
            } else {
                Toast.makeText(fContext, "Error during the saving", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void load(Context aContext, Uri aTargetUri) {
        new LoadImageTask(aContext, aTargetUri).execute();
    }


    public class LoadImageTask extends AsyncTask<Object,Integer,Bitmap> {

        private Context fContext;
        private Uri fTargetUri;

        public LoadImageTask(Context aContext, Uri aTargetUri) {
            super();
            fContext = aContext;
            fTargetUri = aTargetUri;
        }

        @Override
        protected Bitmap doInBackground(Object... objects) {
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(fContext.getContentResolver()
                        .openInputStream(fTargetUri));
            } catch (FileNotFoundException e) {
                bitmap = null;
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null) {
                fManager.putBitmapAsBackground(bitmap);
            } else {
                Toast.makeText(fContext, "Error during the loading", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
