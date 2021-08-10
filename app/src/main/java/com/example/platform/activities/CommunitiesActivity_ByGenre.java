package com.example.platform.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.platform.R;

public class CommunitiesActivity_ByGenre extends AppCompatActivity {

    public static final String TAG = "CommunitiesActivity_ByGenre";
    Context context;
    RelativeLayout rlAction, rlAdventure, rlAnimation, rlComedy, rlCrime, rlDocumentary, rlDrama, rlFamily, rlFantasy, rlHistory, rlHorror, rlMusic, rlReality, rlRomance, rlScienceFiction, rlSoap, rlTalk, rlThriller, rlWar, rlWestern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities_by_genre);
        context = getApplicationContext();

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

        rlAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "action");
                startActivity(intent);
            }
        });

        rlAdventure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "adventure");
                startActivity(intent);
            }
        });

        rlAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "animation");
                startActivity(intent);
            }
        });

        rlComedy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "comedy");
                startActivity(intent);
            }
        });

        rlCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "crime");
                startActivity(intent);
            }
        });

        rlDocumentary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "documentary");
                startActivity(intent);
            }
        });

        rlDrama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "drama");
                startActivity(intent);
            }
        });

        rlFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "family");
                startActivity(intent);
            }
        });

        rlFantasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "fantasy");
                startActivity(intent);
            }
        });

        rlHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "history");
                startActivity(intent);
            }
        });

        rlHorror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "horror");
                startActivity(intent);
            }
        });

        rlMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "music");
                startActivity(intent);
            }
        });

        rlReality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "reality");
                startActivity(intent);
            }
        });

        rlRomance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "romance");
                startActivity(intent);
            }
        });

        rlScienceFiction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "science fiction");
                startActivity(intent);
            }
        });

        rlSoap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "soap");
                startActivity(intent);
            }
        });

        rlTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "talk");
                startActivity(intent);
            }
        });

        rlThriller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "thriller");
                startActivity(intent);
            }
        });

        rlWar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "war");
                startActivity(intent);
            }
        });

        rlWestern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunitiesActivity_Display.class);
                intent.putExtra("objective", "genre");
                intent.putExtra("genreName", "western");
                startActivity(intent);
            }
        });
    }
}