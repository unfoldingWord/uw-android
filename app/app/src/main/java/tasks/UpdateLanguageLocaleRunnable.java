package tasks;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import model.DaoDBHelper;
import model.UWDatabaseModel;
import model.daoModels.DaoSession;
import model.daoModels.LanguageLocale;
import model.daoModels.LanguageLocaleDao;
import services.UWUpdater;

/**
 * Created by Fechner on 6/17/15.
 */
public class UpdateLanguageLocaleRunnable implements Runnable{

    private static final String TAG = "UpdateLangLocaleRunble";

    private JSONArray jsonModels;
    private UWUpdater updater;

    public UpdateLanguageLocaleRunnable(JSONArray jsonModels, UWUpdater updater) {
        this.jsonModels = jsonModels;
        this.updater = updater;
    }

    @Override
    public void run() {

        parseModels(jsonModels);

    }
    private void parseModels(JSONArray models){

        List<LanguageLocale> locales = new ArrayList<LanguageLocale>();
        Log.i(TAG, "Started Locales");
        for(int i = 0; i < models.length(); i++){

            try {
                locales.add((LanguageLocale) new LanguageLocale().setupModelFromJson(models.getJSONObject(i)));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        addAllLocales(locales);
    }

    private void addAllLocales(List<LanguageLocale> locales){

        Log.d(TAG, "Will add all locales to DB");
        DaoDBHelper.getDaoSession(updater.getApplicationContext()).getLanguageLocaleDao().insertOrReplaceInTx(locales);
        Log.d(TAG, "Added all locales to DB");

        updater.runnableFinished();
    }

    private class LanguageLocaleSaveOrUpdateTask extends ModelSaveOrUpdateTask{

        public LanguageLocaleSaveOrUpdateTask(Context context, ModelCreationTaskListener listener) {
            super(context, listener);
        }

        @Override
        protected UWDatabaseModel getExistingModel(String slug, DaoSession session) {
            return LanguageLocale.getLocalForKey(slug, session);
        }
    }
}