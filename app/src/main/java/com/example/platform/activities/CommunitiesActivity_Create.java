package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.platform.R;
import com.example.platform.models.Community;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CommunitiesActivity_Create extends AppCompatActivity {

    public static final String TAG = "CommunitiesActivity_Create";
    Context context;
    EditText etName;
    EditText etKeywords;
    EditText etDescription;
    Button btnCreate;

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
        sweetAlertDialogMain.setTitleText("Are you sure?")
                .setContentText("Make sure all information regarding the community has been entered properly to avoid issues later on.")

                .setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Log.i(TAG, "User is onTrack to delete the comment");
                        // Change SweetAlert to loading type
                        sweetAlertDialogMain.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        sweetAlertDialogMain.getProgressHelper().setBarColor(Color.parseColor("#171717"));
                        sweetAlertDialogMain.setTitleText("Create...");
                        sweetAlertDialogMain.setContentText("Your community is being created");
                        sweetAlertDialogMain.setCancelable(false);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                createCommunity();
                            }
                        }, 4000);
                    }
                })
                .show();
    }


    public void createCommunity() {
        // Get keywords
        String[] keywordsArray = etKeywords.getText().toString().split(", ");
        List<String> keywords = new ArrayList<>();
        for (String s : keywordsArray) {
            String keyword = s.toLowerCase();
            keywords.add(keyword);
        }

        String currentUser = ParseUser.getCurrentUser().getUsername();
        Community community = new Community();
        community.setName(etName.getText().toString());
        community.setDescription(etDescription.getText().toString());
        community.setCreator(currentUser);
        community.setMembers(Arrays.asList(currentUser));
        community.setGenres(new ArrayList<>(genresSelected));
        community.setKeywords(keywords);

        // Saves the new object.
        // Notice that the SaveCallback is totally optional!
        community.saveInBackground(e -> {
            if (e==null){
                Log.i(TAG, "Community was saved successfully");
                sweetAlertDialogMain.setTitleText("Create!")
                        .setContentText("Your Platform community has been successfully created!")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            }else{
                Log.d(TAG, "Issue saving community /Error: " + e.getMessage());
                sweetAlertDialogMain.setTitleText("Sorry!")
                        .setContentText("There was an issue creating your community")
                        .setConfirmText("OK")
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        });
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
}