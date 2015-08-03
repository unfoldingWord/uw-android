package tasks;


import android.os.AsyncTask;

import org.json.JSONObject;

import model.UWDatabaseModel;

/**
 * Created by Fechner on 6/17/15.
 */
public class ModelCreator {

    private static final String TAG = "ModelCreator";

    private final UWDatabaseModel dbModel;
    private final UWDatabaseModel parentOrNull;

    public ModelCreator(UWDatabaseModel dbModel, UWDatabaseModel parentSlugOrNull) {

        this.dbModel = (dbModel != null)? dbModel : null;
        this.parentOrNull = (parentSlugOrNull != null)? parentSlugOrNull : null;
    }

    public UWDatabaseModel start(JSONObject object) {

        if(parentOrNull != null){
            UWDatabaseModel finalModel = dbModel.setupModelFromJson(object, parentOrNull);
            return finalModel;
        }
        else {
            UWDatabaseModel finalModel = dbModel.setupModelFromJson(object);
            return finalModel;
        }
    }
}