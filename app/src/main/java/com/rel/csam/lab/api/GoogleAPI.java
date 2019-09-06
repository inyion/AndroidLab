package com.rel.csam.lab.api;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sam on 17. 8. 25.
 */

public class GoogleAPI extends AsyncTask {

    public static final String TAG = "GoogleAPI";

    public static final String CLIENT_ID = "827364123875-5nu687v964e8mk3618anrqp47488hni4.apps.googleusercontent.com";
    public static final String CLIENT_SECRET = "5SuAd2oPUYkdecEhTgDp9Pn_";

    public static final int RC_SIGN_IN = 9001;

    public enum API {
        CONTACTS("people/me");

        private String url;
        API(String url) {
            this.url = url;
        }

        public String getUrl() {
            return this.url;
        }
    }

    private API currentAPI;
    private GoogleLogin googleLogin;
    private Context context;

    public GoogleAPI(Context context) {
        this.context = context;
    }

    public GoogleAPI API(API api) {
        currentAPI = api;
        return this;
    }

//    public void signCheck(Activity activity, final LoginCompleteListener listener) {
//
//        if (googleLogin == null) {
//            googleLogin = new GoogleLogin(activity, listener);
//        }
//        googleLogin.signIn();
//    }
//
//    public void signOut(Activity activity, final LoginCompleteListener listener) {
//        if (googleLogin == null) {
//            googleLogin = new GoogleLogin(activity, listener);
//        }
//
//        googleLogin.signOut();
//    }

    private People setUp(String serverAuthCode) throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // Redirect URL for web based applications.
        // Can be empty too.
        String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";

        // STEP 1
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                httpTransport,
                jsonFactory,
                CLIENT_ID,
                CLIENT_SECRET,
                serverAuthCode,
                redirectUrl).execute();

        // STEP 2
        GoogleCredential credential = new GoogleCredential.Builder()
                .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .build();

        credential.setFromTokenResponse(tokenResponse);

        // STEP 3
        return new People.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Qoo10 Android")
                .build();
    }

    public void onActivityResult(Intent data) {
        if (googleLogin != null) {

            HashMap<String, String> map = googleLogin.onActivityResult(data);
            if (!map.isEmpty()) {
                if (currentAPI != null) {
                    execute(map.get("ServerAuthCode"));
                }
            }
        }
    }

    private void requestPeople(People peopleService, List<Person> list, String nextPageToken) throws IOException {

        ListConnectionsResponse response = null;
        if (!TextUtils.isEmpty(nextPageToken)) {
            response = peopleService.people().connections()
                    .list(API.CONTACTS.getUrl())
                    .setRequestMaskIncludeField("person.names,person.emailAddresses,person.phoneNumbers,person.genders,person.photos")
                    .setPageToken(nextPageToken)
                    .execute();
        } else {
            // 첫페이지
            response = peopleService.people().connections()
                    .list(API.CONTACTS.getUrl())
                    .setRequestMaskIncludeField("person.names,person.emailAddresses,person.phoneNumbers,person.genders,person.photos")
                    .execute();
        }


        List<Person> connections = null;
        if (response != null) {
            connections = response.getConnections();
        }

        if (connections != null) {
            list.addAll(connections);
        }

        if (!TextUtils.isEmpty(response.getNextPageToken())) {
            requestPeople(peopleService, list, response.getNextPageToken());
        }
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            if (currentAPI == API.CONTACTS) {
                List<Person> connections = new ArrayList<>();
                requestPeople(setUp((String) params[0]), connections, null);
                if (connections != null && connections.size() > 0) {
//                    AddressSyncManager.getInstance(context).updateMember(connections);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

//        ToastUtil.Companion.showToastCenter(context.getString(R.string.google_sync_success));
    }
}
