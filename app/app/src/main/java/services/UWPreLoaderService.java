/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.common.io.ByteStreams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.unfoldingword.mobile.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ListIterator;

import model.DaoDBHelper;
import model.DataFileManager;
import model.daoModels.Book;
import model.daoModels.Version;
import model.parsers.MediaType;
import runnables.UpdateAndVerifyBookRunnable;
import runnables.UpdateLanguageLocaleRunnable;
import runnables.UpdateProjectsRunnable;
import utils.FileNameHelper;
import utils.UWPreferenceManager;

/**
 * Created by PJ Fechner
 * Service for adding the preloaded content to DB
 */
public class UWPreLoaderService extends UWUpdaterService {

    private static final String TAG = "UWPreLoader";

    public static final String BROAD_CAST_PRELOAD_SUCCESSFUL = "org.unfoldingword.mobile.PRELOAD_SUCCESSFUL";

    private boolean hasUpdatedVersions = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        addRunnable(new PreloadVersionsRunnable());

        return START_STICKY;
    }

    @Override
    protected void stopService() {
        // This is to cause the book text to only be added after all other content
        if(!hasUpdatedVersions) {
            addRunnable(new PreloadTextRunnable());
            hasUpdatedVersions = true;
        }
        else {
            getApplicationContext().sendBroadcast(new Intent(BROAD_CAST_PRELOAD_SUCCESSFUL));
            this.stopSelf();
        }
    }

    class PreloadVersionsRunnable implements Runnable {

        @Override
        public void run() {

            updateVersions();
            updateLocales();
            runnableFinished();
        }

        private void updateVersions(){
            try {
                String jsonString = loadDbFile(getApplicationContext().getResources().getString(R.string.preloaded_catalog_file_name));

                JSONObject jsonObject = new JSONObject(jsonString);
                long modified = jsonObject.getLong(UWUpdaterService.MODIFIED_JSON_KEY);
                UWPreferenceManager.setLastUpdatedDate(getApplicationContext(), modified);

//                UWDataParser.getInstance(getApplicationContext()).updateProjects(jsonObject.getJSONArray(UWDataParser.PROJECTS_JSON_KEY), false);
                addRunnable(new UpdateProjectsRunnable(jsonObject.getJSONArray(UWUpdaterService.PROJECTS_JSON_KEY), getThis()));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        private void updateLocales(){

            try {
                String jsonString = loadDbFile(getApplicationContext().getResources().getString(R.string.preloaded_locales_file_name));
                JSONArray locales = new JSONArray(jsonString);
                UWPreferenceManager.setHasDownloadedLocales(getApplicationContext(), true);
                addRunnable(new UpdateLanguageLocaleRunnable(locales, getThis()));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    class PreloadTextRunnable implements Runnable{

        @Override
        public void run() {

            List<Book> books = DaoDBHelper.getDaoSession(getApplicationContext())
                    .getBookDao().queryBuilder().list();

            ListIterator li = books.listIterator(books.size());

            // Iterate in reverse to start with the stories.
            while(li.hasPrevious()) {

                Book book = (Book) li.previous();
                try {
                    String signature = loadDbFile(FileNameHelper.getSaveFileNameFromUrl(book.getSignatureUrl()));
                    if(signature == null){
                        signature = "";
                    }
                    byte[] text = loadDbFileBytes(FileNameHelper.getSaveFileNameFromUrl(book.getSourceUrl()));
                    DataFileManager.saveDataForBook(getApplicationContext(), book, text, MediaType.MEDIA_TYPE_TEXT);
                    DataFileManager.saveSignatureForBook(getApplicationContext(), book, signature.getBytes(), MediaType.MEDIA_TYPE_TEXT);
//                    saveFile(text, book.getSourceUrl());
//                    saveFile(signature.getBytes("UTF-8"), book.getSignatureUrl());

                    UpdateAndVerifyBookRunnable runnable = new UpdateAndVerifyBookRunnable(book, getThis(), text, signature);
                    addRunnable(runnable, book.getVersion(), MediaType.MEDIA_TYPE_TEXT);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }

            List<Version> versions = DaoDBHelper.getDaoSession(getApplicationContext())
                    .getVersionDao().queryBuilder().list();

            for(Version version : versions){
                version.update();
            }

            runnableFinished();
        }
    }

    private void saveFile(byte[] bytes, String url){

        try{
            FileOutputStream fos = getApplicationContext().openFileOutput(FileNameHelper.getSaveFileNameFromUrl(url), Context.MODE_PRIVATE);
            fos.write(bytes);
            fos.close();
            Log.i(TAG, "USFM File Saved");
        }
        catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, "Error when saving USFM");
        }
    }

    private byte[] loadDbFileBytes(String fileName) throws IOException{

        // Open your local model.db as the input stream
        InputStream inputStream = getApplicationContext().getAssets().open("preloaded_content/" + fileName);

        byte[] file = ByteStreams.toByteArray(inputStream);
        return file;
    }

    private String loadDbFile(String fileName) throws IOException{

        // Open your local model.db as the input stream
        InputStream inputStream = getApplicationContext().getAssets().open("preloaded_content/" + fileName);
        String file = new String(ByteStreams.toByteArray(inputStream));
        return file;
    }


}
