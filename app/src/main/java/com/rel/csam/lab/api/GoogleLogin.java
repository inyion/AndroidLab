package com.rel.csam.lab.api;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.api.services.people.v1.PeopleScopes;
import com.rel.csam.lab.R;

import java.util.HashMap;

public class GoogleLogin implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleLogin";

    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog mProgressDialog;
    private Activity mActivity;
//    private LoginCompleteListener mListener;

    private final String GENDER = "U";
    private final String BS_UID = "";

    public GoogleLogin(Context con) {
        initGoogleApiClient(con);
    }

    private void initGoogleApiClient(Context con) {
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(GoogleAPI.CLIENT_ID)
                .requestIdToken(con.getString(R.string.default_web_client_id))
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN),
                        new Scope(PeopleScopes.CONTACTS_READONLY),
                        new Scope(PeopleScopes.USER_PHONENUMBERS_READ))
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(con)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
//                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
    }

    private void showProgressDialog() {
        if (mActivity != null) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(mActivity);
//                mProgressDialog.setMessage(mActivity.getString(R.string.loading_text));
                mProgressDialog.setIndeterminate(true);
            }

            mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    // [START signin]
    public void signIn() {
        if (!mGoogleApiClient.isConnected() & !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
        showProgressDialog();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mActivity.startActivityForResult(signInIntent, GoogleAPI.RC_SIGN_IN);
    }
    // [END signin]

    public void signOut() {
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                // Firebase sign out
                // Google sign out
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    hideProgressDialog();
                                    disconnectGoogleApi();
                                }
                            });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
    }

    // [START onactivityresult]
    public HashMap<String, String> onActivityResult(Intent data) {

        HashMap<String, String> userInfo = new HashMap<>();
        hideProgressDialog();
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result != null && result.isSuccess()) {
            Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {
                String userId = account.getId();
                String userEmail = account.getEmail();
                userInfo.put("id", account.getId());
                userInfo.put("email", userEmail);
                userInfo.put("token", account.getIdToken());
                userInfo.put("ServerAuthCode", account.getServerAuthCode());
                String userName = account.getDisplayName();
                // Todo is signed in
                Log.i(TAG, "onAuthStateChanged:signed_in:" + account.getId());
            }
        } else {
            if (result != null && result.getStatus() != null) {
                Log.d(TAG, result.getStatus().toString() + "\nmsg: " + result.getStatus().getStatusMessage());
            }
            Log.e(TAG, "onActivityResult Google Login Error");
        }

        return userInfo;
    }
    // [END onactivityresult]

    public void disconnectGoogleApi() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            GoogleApiAvailability mGoogleApiAvailability = GoogleApiAvailability.getInstance();
            Dialog dialog = mGoogleApiAvailability.getErrorDialog(mActivity, connectionResult.getErrorCode(), GoogleAPI.RC_SIGN_IN);
            dialog.show();
        } catch (Exception e) {

        }
    }
}
