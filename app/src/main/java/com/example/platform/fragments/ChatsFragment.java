package com.example.platform.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.platform.R;
import com.example.platform.activities.ProfileActivity;
import com.parse.ParseUser;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.objects_api.uuid.PNGetUUIDMetadataResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNSetUUIDMetadataResult;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    public static final String TAG = "ChatsFragment";
    private ImageView ivProfile;
    PubNub pubnub;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Alert view that menu items exist
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.toolbar_chat_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setting up the toolbar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_Chats);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Take user to their profile
        ivProfile = getActivity().findViewById(R.id.ivProfileIcon_Chats);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Connect to PubNub and establish a session with the user
        establishPubnubConnection();
        pubnub.getUUIDMetadata().async(new PNCallback<PNGetUUIDMetadataResult>() {
            @Override
            public void onResponse(@Nullable final PNGetUUIDMetadataResult result, @NotNull final PNStatus status) {
                if (status.isError()) {
                    Log.d(TAG, "Issue getting PN user data / Error: " + status.toString());

                    if (status.toString().contains("Requested object was not found")) { // If the user does not yet been saved in PubNub
                        createPubnubUser(pubnub);
                    }
                }
                else {
                    Log.i(TAG, "Success getting PN user data");
                }
            }
        });
    }

    public void establishPubnubConnection() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-f3e50456-f16a-11eb-9d61-d6c76bc6f614");
        pnConfiguration.setPublishKey("pub-c-d5e6773e-3948-488a-a5cd-4976b5c9de45");
        pnConfiguration.setUuid(ParseUser.getCurrentUser().getObjectId()); // Set the PubNub unique user ID as the User's Object ID in the Parse server
        pubnub =  new PubNub(pnConfiguration);
    }

    // Save user in PubNub
    public void createPubnubUser(PubNub pubnub) {
        pubnub.setUUIDMetadata()
                .uuid(ParseUser.getCurrentUser().getObjectId())
                .name(ParseUser.getCurrentUser().getUsername())
                .email(ParseUser.getCurrentUser().getEmail())
                .async(new PNCallback<PNSetUUIDMetadataResult>() {
                    @Override
                    public void onResponse(@Nullable final PNSetUUIDMetadataResult result, @NotNull final PNStatus status) {
                        if (status.isError()) {
                            Log.d(TAG, "Issue create new Pubnub user object /Error: " + status.toString());
                        }
                        else {
                            Log.i(TAG, "Success creating new Pubnub user object");
                            establishPubnubConnection();
                        }
                    }
                });
    }
}