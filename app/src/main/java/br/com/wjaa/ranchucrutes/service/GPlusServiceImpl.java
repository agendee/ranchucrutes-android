package br.com.wjaa.ranchucrutes.service;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import br.com.wjaa.ranchucrutes.R;
import br.com.wjaa.ranchucrutes.utils.AndroidUtils;

/**
 * Created by wagner on 25/09/15.
 */
public class GPlusServiceImpl implements GPlusService, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "MainActivity";

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    private GoogleApiClient mClient;
    private Activity context;
    private Fragment fragment;
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    @Override
    public void onCreate(Fragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getActivity();
        // Builds single client object that connects to Drive and Google+
        mClient = new GoogleApiClient.Builder(context)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mClient.connect();
    }


    @Override
    public void onViewCreated(View view){
        Button btnLogin = (Button) view.findViewById(R.id.btnGPlus);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSignInClicked){
                    signInWithGplus();
                    mSignInClicked = true;
                }else{
                    mClient.connect();
                }
            }
        });



    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;
        Toast.makeText(context, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getProfileInformation();


    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mClient.connect();
    }



    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), context,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;


            resolveSignInError();

        }

    }


    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mClient.isConnecting()) {
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(context, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mClient.connect();
            }
        }
    }



    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mClient);

                Log.e("GPLUSSERVICE", "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                //txtName.setText(personName);
                //txtEmail.setText(email);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                          + PROFILE_PIC_SIZE;

                //new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

            } else {
                Toast.makeText(context,
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
