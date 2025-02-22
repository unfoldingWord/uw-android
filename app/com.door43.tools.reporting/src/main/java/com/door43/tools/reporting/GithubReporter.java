package com.door43.tools.reporting;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.io.File;

/**
 * This class submits information to a github repository
 */
public class GithubReporter {

    private static final String TAG = "GithubReporter";

    private static final int MAX_TITLE_LENGTH = 50;
    private static final String DEFAULT_CRASH_TITLE = "crash report";
    private static final String DEFAULT_BUG_TITLE = "bug report";
    private final String sRepositoryUrl;
    private final String sGithubOauth2Token;
    private final Context sContext;

    public GithubReporter(Context context, String repositoryUrl, String githubOauth2Token) {
        sContext = context;
        sRepositoryUrl = repositoryUrl;
        sGithubOauth2Token = githubOauth2Token;
    }

    /**
     * Creates a crash issue on github.
     * @param notes notes supplied by the user
     * @param stacktraceFile the stacktrace file
     */
    public void reportCrash(String notes, File stacktraceFile) {
            String stacktrace = FileUtil.getStringFromFile(stacktraceFile);
            reportCrash(notes, stacktrace, null);
    }

    /**
     * Creates a crash issue on github.
     * @param notes notes supplied by the user
     * @param stacktraceFile the stacktrace file
     * @param logFile the log file
     */
    public void reportCrash(String notes, File stacktraceFile, File logFile) {
        String log = null;
        if(logFile.exists()) {
                log = FileUtil.getStringFromFile(logFile);
        }
        String stacktrace = FileUtil.getStringFromFile(stacktraceFile);
        reportCrash(notes, stacktrace, log);
    }

    /**
     * Creates a crash issue on github.
     * @param notes notes supplied by the user
     * @param stacktrace the stracktrace
     * @param log information from the log
     */
    public void reportCrash(String notes, String stacktrace, String log) {
        String title = getTitle(notes, DEFAULT_CRASH_TITLE);
        StringBuffer bodyBuf = new StringBuffer();
        bodyBuf.append(getNotesBlock(notes));
        bodyBuf.append(getEnvironmentBlock());
        bodyBuf.append(getStacktraceBlock(stacktrace));
        bodyBuf.append(getLogBlock(log));

        String[] labels = new String[]{"crash report"};
        String response = submit(new GitHubClient.IssueBody(title, bodyBuf.toString(), labels));
        // TODO: handle response
    }

    /**
     * Creates a bug issue on github
     * @param notes notes supplied by the user
     */
    public void reportBug(String notes) {
        reportBug(notes, "");
    }

    /**
     * Creates a bug issue on github
     * @param notes notes supplied by the user
     * @param logFile the log file
     */
    public void reportBug(String notes, File logFile) {
        String log = null;
        log = FileUtil.getStringFromFile(logFile);
        reportBug(notes, log);
    }

    /**
     * Creates a bug issue on github
     * @param notes notes supplied by the user
     * @param log information from the log
     */
    public void reportBug(String notes, String log) {
        String title = getTitle(notes, DEFAULT_BUG_TITLE);
        StringBuffer bodyBuf = new StringBuffer();
        bodyBuf.append(getNotesBlock(notes));
        bodyBuf.append(getEnvironmentBlock());
        bodyBuf.append(getLogBlock(log));

        String[] labels = new String[]{"bug report"};
        String response = submit(new GitHubClient.IssueBody(title, bodyBuf.toString(), labels));

        Log.i(TAG, "bug Report Response: " + response);
    }

    /**
     * Generates the json payload that will be set to the github server.
     * @param title the issue title
     * @param body the issue body
     * @param labels the issue labels. These will be created automatically went sent to github
     * @return
     */
//    private JsonObject generatePayload(String title, String body, String[] labels) {
//
//
//
//        JsonObject json = new JsonObject();
//        try {
//            json.addProperty("title", title);
//            json.addProperty("body", body);
//            JsonArray labelsJson = new JsonArray();
//            for(String label:labels) {
//                labelsJson.add(new JsonEllabel);
//            }
//            try {
//                PackageInfo pInfo = sContext.getPackageManager().getPackageInfo(sContext.getPackageName(), 0);
//                labelsJson.add(pInfo.versionName);
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//            json.add("labels", labels);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return json;
//    }

    /**
     * Sends the new issue request to github
//     * @param json the payload
     * @return
     */
    private String submit(GitHubClient.IssueBody body) {

        return GitHubClient.submitIssue("token " + sGithubOauth2Token, body);

        // headers
//        List<NameValuePair> headers = new ArrayList<>();
//        headers.add(new BasicNameValuePair("Authorization", ));
//        headers.add(new BasicNameValuePair("Content-Type", "application/json"));
//
//        // create post request
//        DefaultHttpClient httpClient = new DefaultHttpClient();
//        HttpPost httpPost = new HttpPost(sRepositoryUrl);
//        try {
//            httpPost.setEntity(new StringEntity(json.toString()));
//            if(headers != null) {
//                for(NameValuePair h:headers) {
//                    httpPost.setHeader(h.getName(), h.getValue());
//                }
//            }
//            HttpResponse response = httpClient.execute(httpPost);
//            return response.getStatusLine().toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "";
//        }
    }

    /**
     * Generates the notes block.
     * @param log
     * @return
     */
    private String getLogBlock(String log) {
        StringBuffer logBuf = new StringBuffer();
        if (log != null && log.length() > 0) {
            logBuf.append("Log history\n======\n");
            logBuf.append("```java\n");
            logBuf.append(log + "\n");
            logBuf.append("```\n");
        }
        return logBuf.toString();
    }

    /**
     * Generates the stacktrace block
     * @param stacktrace the stacktrace text
     * @return
     */
    private static String getStacktraceBlock(String stacktrace) {
        StringBuffer stacktraceBuf = new StringBuffer();
        if(stacktrace != null && stacktrace.length() > 0) {
            stacktraceBuf.append("Stack trace\n======\n");
            stacktraceBuf.append("```java\n");
            stacktraceBuf.append(stacktrace + "\n");
            stacktraceBuf.append("```\n");
        }
        return stacktraceBuf.toString();
    }

    /**
     * Generates the ntoes block
     * @param notes notes supplied by the user
     * @return
     */
    private static String getNotesBlock(String notes) {
        StringBuffer notesBuf = new StringBuffer();
        if (notes.length() > 0) {
            notesBuf.append("Notes\n======\n");
            notesBuf.append(notes + "\n");
        }
        return notesBuf.toString();
    }

    /**
     * Generates the environment block
     * @return
     */
    private String getEnvironmentBlock() {
        PackageInfo pInfo = null;
        StringBuffer environmentBuf = new StringBuffer();
        environmentBuf.append("\nEnvironment\n======\n");
        environmentBuf.append("Environment Key | Value" + "\n");
        environmentBuf.append(":----: | :----:" + "\n");
        try {
            pInfo = sContext.getPackageManager().getPackageInfo(sContext.getPackageName(), 0);
            environmentBuf.append("version | " + pInfo.versionName + "\n");
            environmentBuf.append("build | " + pInfo.versionCode + "\n");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String udid = Settings.Secure.getString(sContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        environmentBuf.append("UDID | " + udid + "\n");
        environmentBuf.append("Android Release | " + Build.VERSION.RELEASE + "\n");
        environmentBuf.append("Android SDK | " + Build.VERSION.SDK_INT + "\n");
        environmentBuf.append("Brand | " + Build.BRAND + "\n");
        environmentBuf.append("Device | " + Build.DEVICE + "\n");
        return environmentBuf.toString();
    }

    /**
     * Generates the title from the notes
     * @param notes notes supplied by the user
     * @param defaultTitle the title to use if the user notes are insufficient
     * @return
     */
    private static String getTitle(String notes, String defaultTitle) {
        String title = defaultTitle;
        if (notes.length() < MAX_TITLE_LENGTH && notes.length() > 0) {
            title = notes;
        } else if (notes.length() > 0) {
            title = notes.substring(0, MAX_TITLE_LENGTH - 3) + "...";
        }
        return title;
    }
}
