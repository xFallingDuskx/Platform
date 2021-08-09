package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.example.platform.R;

public class CommunitiesActivity_ByGenre extends AppCompatActivity {

    public static final String TAG = "CommunitiesActivity_ByGenre";
    RelativeLayout rlAction, rlAdventure, rlAnimation, rlComedy, rlCrime, rlDocumentary, rlDrama, rlFamily, rlFantasy, rlHistory, rlHorror, rlMusic, rlReality, rlRomance, rlScienceFiction, rlSoap, rlTalk, rlThriller, rlWar, rlWestern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities_by_genre);

        rlAction = findViewById(R.id.rlAction_Genre);
        rlAdventure = findViewById(R.id.rlAdventure_Genre);
        rlAnimation = findViewById(R.id.rlAnimation_Genre);
        rlComedy = findViewById(R.id.rlComedy_Genre);
        rlCrime = findViewById(R.id.rlCrime_Genre);
        rlDocumentary = findViewById(R.id.rlDocumentary_Genre);
        rlDrama = findViewById(R.id.rlDrama_Genre);
        rlFamily = findViewById(R.id.rlFamily_Genre);
        rlFantasy = findViewById(R.id.rlFantasy_Genre);
        rlHistory = findViewById(R.id.rlHistory_Genre);
        rlHorror = findViewById(R.id.rlHorror_Genre);
        rlMusic = findViewById(R.id.rlMusic_Genre);
        rlReality = findViewById(R.id.rlReality_Genre);
        rlRomance = findViewById(R.id.rlRomance_Genre);
        rlScienceFiction = findViewById(R.id.rlScienceFiction_Genre);
        rlSoap = findViewById(R.id.rlSoap_Genre);
        rlTalk = findViewById(R.id.rlTalk_Genre);
        rlThriller = findViewById(R.id.rlThriller_Genre);
        rlWar = findViewById(R.id.rlWar_Genre);
        rlWestern = findViewById(R.id.rlWestern_Genre);

        //todo
    }
}