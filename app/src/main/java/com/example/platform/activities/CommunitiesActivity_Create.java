package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.platform.R;
import com.example.platform.models.Community;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CommunitiesActivity_Create extends AppCompatActivity {

    public static final String TAG = "CommunitiesActivity_Create";
    public final static int PICK_PHOTO_CODE = 1046;
    Context context;
    EditText etName;
    EditText etKeywords;
    EditText etDescription;
    Button btnCreate;

    CardView cvImageContainer;
    ImageView ivCover;
    File photoFile;
    String photoFileName = "community_image.jpg";

    TextView tvGenreDisplay;
    RelativeLayout rlGenreSelection, rlGenreSpace;
    boolean genresVisible = false;
    ListView lvGenre;
    Set<String> genresSelected;
    
    SweetAlertDialog sweetAlertDialogMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities_create);
        context = getApplicationContext();

        etName = findViewById(R.id.etNameInput_Communities);
        tvGenreDisplay = findViewById(R.id.tvGenreDisplay);
        lvGenre = findViewById(R.id.lvGenre);
        rlGenreSelection = findViewById(R.id.rlGenre_Communities);
        rlGenreSpace = findViewById(R.id.rlGenreSpace);
        etKeywords = findViewById(R.id.etKeywordsInput_Communities);
        etDescription = findViewById(R.id.etDescriptionInput_Communities);
        btnCreate = findViewById(R.id.btnCreate_Communities);
        cvImageContainer = findViewById(R.id.cvImageContainer);
        ivCover = findViewById(R.id.ivCover_Communities);

        // Allow users to select a photo fraom their gallery
        // Source: https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media#accessing-stored-media
        cvImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick to add image for community");
                selectPhoto();
            }
        });

        // Allow users to select from a list of genres
        genresSelected = new TreeSet<>();
        setupGenreSelection();

        // Create community on user submission
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: description must have a minimum of 100 words

                showPopUp();
            }
        });
    }

    // Source: https://android--examples.blogspot.com/2016/03/android-listview-multiple-selection.html
    // Source: https://www.codegrepper.com/code-examples/java/set+height+of+layout+programmatically+android
    private void setupGenreSelection() {
        tvGenreDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! genresVisible) {
                    openGenreSelection();
                } else {
                    closeGenreSelection();
                }
            }
        });


        List<String> genreOptions = Arrays.asList(
                "Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary", "Drama",
                "Family", "Fantasy", "History", "Horror", "Music", "Reality", "Romance",
                "Science Fiction", "Soap", "Talk", "Thriller", "War", "Western"
        );

        // Initialize a new ArrayAdapter
        // Change context to 'this' if there are issues
        ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_multiple_choice, genreOptions);
        lvGenre.setAdapter(adapter);
        lvGenre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SparseBooleanArray clickedItemPositions = lvGenre.getCheckedItemPositions();
                for(int index = 0; index<clickedItemPositions.size(); index++){
                    // Get the checked status of the current item
                    boolean checked = clickedItemPositions.valueAt(index);

                    // If the item has been checked by the user
                    if(checked){
                        int key = clickedItemPositions.keyAt(index);
                        String genre = (String) lvGenre.getItemAtPosition(key);

                        if (index == 0) {
                            tvGenreDisplay.setText(genre);
                        } else {
                            tvGenreDisplay.setText(tvGenreDisplay.getText() + ", " + genre);
                        }
                        genresSelected.add(genre);
                    }
                }
            }
        });
    }

    public void showPopUp() {
        sweetAlertDialogMain = new SweetAlertDialog(CommunitiesActivity_Create.this, SweetAlertDialog.NORMAL_TYPE);

        //Ensure no field is empty
        if (etName.getText().toString().isEmpty() || etDescription.getText().toString().isEmpty() ||
                etKeywords.getText().toString().isEmpty() || genresSelected.isEmpty()) {
            sweetAlertDialogMain.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialogMain.setTitleText("Error")
                    .setContentText("All field must be filled")
                    .setConfirmText("OK")
                    .setConfirmClickListener(null)
                    .show();
            return;
        }

        sweetAlertDialogMain.changeAlertType(SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialogMain.setTitleText("Are you sure?")
                .setContentText("Make sure all information regarding the community has been entered properly to avoid issues later on.")

                .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Log.i(TAG, "User is onTrack to delete the comment");
                        // Change SweetAlert to loading type
                        sweetAlertDialogMain.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        sweetAlertDialogMain.getProgressHelper().setBarColor(Color.parseColor("#171717"));
                        sweetAlertDialogMain.setTitleText("Creating...");
                        sweetAlertDialogMain.setContentText("Your community is being created");
                        sweetAlertDialogMain.setCancelable(false);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    createCommunity();
                                } catch (ParseException e) {
                                    Log.d(TAG, "Issue creating community for the user");
                                    e.printStackTrace();
                                }
                            }
                        }, 4000);
                    }
                })
                .show();
    }


    public void createCommunity() throws ParseException {
        // Ensure this community does not already exist
        String name = etName.getText().toString();
        ParseQuery<Community> parseQuery = ParseQuery.getQuery(Community.class);
        parseQuery.whereEqualTo(Community.KEY_NAME, name);
        if (parseQuery.count() != 0) {
            Log.i(TAG, "Community has already been created");
            sweetAlertDialogMain.setTitleText("Unable to Create")
                    .setContentText("A community with this name already exist. Please pick a different name.")
                    .setConfirmText("OK")
                    .setConfirmClickListener(null)
                    .changeAlertType(SweetAlertDialog.WARNING_TYPE);
        } else {
            // Get keywords
            String[] keywordsArray = etKeywords.getText().toString().split(", ");
            List<String> keywords = new ArrayList<>();
            for (String s : keywordsArray) {
                String keyword = s.toLowerCase();
                keywords.add(keyword);
            }

            String currentUser = ParseUser.getCurrentUser().getUsername();
            Community community = new Community();
            community.setName(name);
            community.setDescription(etDescription.getText().toString());
            community.setCreator(currentUser);
            community.setMembers(Arrays.asList(currentUser));
            community.setGenres(new ArrayList<>(genresSelected));
            community.setKeywords(keywords);
            community.setNumberOfMembers(1);
            community.setImage(new ParseFile(photoFile));

            // Saves the new object.
            // Notice that the SaveCallback is totally optional!
            community.saveInBackground(e -> {
                if (e == null) {
                    Log.i(TAG, "Community was saved successfully");
                    sweetAlertDialogMain.setTitleText("Created!")
                            .setContentText("Your Platform community has been successfully created!")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    finish();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                } else {
                    Log.d(TAG, "Issue saving community /Error: " + e.getMessage());
                    sweetAlertDialogMain.setTitleText("Sorry!")
                            .setContentText("There was an issue creating your community")
                            .setConfirmText("OK")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
            });
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Community genres - outside of
        Rect genreRect = new Rect();
        rlGenreSpace.getGlobalVisibleRect(genreRect);
        if (!genreRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
            Log.i(TAG, "Outside of genre space");
            if (genresVisible) {
                closeGenreSelection();
            }
        }

        // Community genres - inside of
        Rect genreDisplayRect = new Rect();
        tvGenreDisplay.getGlobalVisibleRect(genreDisplayRect);
        if (genreDisplayRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
            Log.i(TAG, "Inside of genre space");

            etName.clearFocus();
            etKeywords.clearFocus();
            etDescription.clearFocus();

            // Close the keyboard
            // Source: https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-programmatically
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = findViewById(android.R.id.content).getRootView();
            if (view == null) {
                view = new View(context);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            Log.i(TAG, "Genre view is visible: " + genresVisible);
            if (!genresVisible) {
                openGenreSelection();
            }
            return true;
        }

        // Community name input
        Rect nameRect = new Rect();
        etName.getGlobalVisibleRect(nameRect);
        if (nameRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
            Log.i(TAG, "Inside of name space");
            etName.requestFocus();
            if (genresVisible) {
                closeGenreSelection();
            }
        }

        // Community keywords input
        Rect keywordsRect = new Rect();
        etKeywords.getGlobalVisibleRect(keywordsRect);
        if (keywordsRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
            Log.i(TAG, "Inside of keywords space");
            etKeywords.requestFocus();
            if (genresVisible) {
                closeGenreSelection();
            }
        }

        // Community description input
        Rect descriptionRect = new Rect();
        etDescription.getGlobalVisibleRect(descriptionRect);
        if (descriptionRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
            Log.i(TAG, "Inside of description space");
            etDescription.requestFocus();
            if (genresVisible) {
                closeGenreSelection();
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    public void closeGenreSelection() {
        ViewGroup.LayoutParams params = rlGenreSelection.getLayoutParams();
        params.height = 115;
        rlGenreSelection.setLayoutParams(params);
        lvGenre.setVisibility(View.GONE);
        genresVisible = false;
    }

    public void openGenreSelection() {
        ViewGroup.LayoutParams params = rlGenreSelection.getLayoutParams();
        params.height = 600;
        rlGenreSelection.setLayoutParams(params);
        lvGenre.setVisibility(View.VISIBLE);
        genresVisible = true;
    }

    // Trigger gallery selection for a photo
    public void selectPhoto() {
        Log.d(TAG, "Entered selectPhoto");
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Log.d(TAG, "Entered loadFromUri");
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Entered onActivityResult for image");
        super.onActivityResult(requestCode, resultCode, data);

        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Log.d(TAG, "Entered if-statement of onActivityResult");

            Uri photoUri = data.getData();

            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            // Load the selected image into a preview
            ivCover.setImageBitmap(selectedImage);

        // Convert selectedImage BitMap to a File to save in Parse
        // Source: https://stackoverflow.com/questions/7769806/convert-bitmap-to-file
        // Source: https://stackoverflow.com/questions/11144783/how-to-access-an-image-from-the-phones-photo-gallery

            //create a file to write bitmap data
            photoFile = new File(context.getCacheDir(), photoFileName);
            try {
                photoFile.createNewFile();
            } catch (IOException e) {
                Log.d(TAG, "Issue creating new file for image");
                e.printStackTrace();
            }

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(photoFile);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "Issue writing the file -- file could not be found");
                e.printStackTrace();
            }

            try {
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                Log.d(TAG, "Issue writing the file");
                e.printStackTrace();
            }
        }
    }
}