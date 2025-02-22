/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.github.peejweej.androidsideloading.activities.SideLoadActivity;
import com.github.peejweej.androidsideloading.model.SideLoadInformation;
import com.github.peejweej.androidsideloading.model.SideLoadVerifier;

import java.util.List;
import java.util.Map;

import model.daoModels.Version;
import model.parsers.MediaType;

/**
 * Created by Fechner on 8/24/15.
 */
public class SharingHelper {

    private static final String FILE_EXTENSION = ".ufw";

    public static SideLoadInformation getLoadInformation(){
        return new SideLoadInformation(FILE_EXTENSION, new SideLoadVerifier() {
            @Override
            public boolean fileIsValid(String file) {
                return true;
            }

            @Override
            public boolean fileIsValid(Uri file) {
                return true;
            }
        });
    }

    private static SideLoadInformation getShareInformation(Uri fileUri, String fileName){
        return new SideLoadInformation(fileName, fileUri);
    }

    public static void getShareInformation(final Context context, final Map<Version, List<MediaType>> versions, final SideLoadInformationResponse response){

        new AsyncTask<Void, Void, SideLoadInformation>(){
            @Override
            protected SideLoadInformation doInBackground(Void... params) {

                String fileName = (versions.size() == 1) ? getFileNameForVersion((Version) versions.keySet().toArray()[0]) : getGenericFileName();
                Uri fileUri = DataFileManager.createUriForSideLoad(context, versions, fileName);

                if(fileUri == null){
                    return null;
                }
                else {
//                    String filePath = Uri.fromFile(new File(fileUri.getPath())).getPath();
//                    String fileFullPath = Uri.fromFile(new File(fileUri.getEncodedPath())).getEncodedPath();
                    return getShareInformation(fileUri, fileName);
                }
            }

            @Override
            protected void onPostExecute(SideLoadInformation information) {
                super.onPostExecute(information);
                response.informationLoaded(information);
            }
        }.execute();
    }

    public interface SideLoadInformationResponse{
        void informationLoaded(SideLoadInformation information);
    }

//    private static Uri getFileForVersion(Context context, Version version){
//
//        FileUtilities.compressText(version.getAsSideLoadJson(context).toString());
//        return FileUtil.createTemporaryFile(context, FileUtilities.compressText(version.getAsSideLoadJson(context).toString()), getFileNameForVersion(version));
//    }

    public static String getFileNameForVersion(Version version){
        return version.getName() + " (" + version.getLanguage().getLanguageAbbreviation() + ")" + FILE_EXTENSION;
    }

    public static Intent getIntentForLoading(Context context){
        return new Intent(context, SideLoadActivity.class).putExtra(SideLoadActivity.SIDE_LOAD_INFORMATION_PARAM, getLoadInformation());
    }

    public static String getGenericFileName() {
        return "UnfoldingWord Versions" + FILE_EXTENSION;
    }

//    public static Intent getIntentForSharing(Context context, Version version){
//
//        return new Intent(context, SideShareActivity.class).putExtra(SideShareActivity.SIDE_LOAD_INFORMATION_PARAM, getShareInformation(context, version, new ArrayList<MediaType>()));
//    }
}