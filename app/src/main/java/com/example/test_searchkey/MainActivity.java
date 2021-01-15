package com.example.test_searchkey;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import android.os.Bundle;
import android.speech.RecognizerIntent;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;



import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MakeSuggestion {
    private FloatingSearchView mSearchView;
    private asyn a = null;
    MakeSuggestion makeSuggestion = this;
    @Override
    public void getSuggestion(List<Suggestion> suggestions) {
        mSearchView.swapSuggestions(suggestions);
        mSearchView.hideProgress();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchView = findViewById(R.id.floating_search_view);
        final int VOICE_SEARCH_CODE = 3012;
        checkVoiceRecognition();
        final MenuItem mActionVoice = findViewById(R.id.action_voice_rec);

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {
                    mSearchView.showProgress();
                    if (a!= null){
                        a.cancel(true);
                    }
                    a = (asyn) new asyn(makeSuggestion).execute(" http://suggestqueries.google.com/complete/search?output=toolbar&hl=en&q="+newQuery);
                    //Toast.makeText(getApplicationContext(), ""+a, Toast.LENGTH_LONG).show();
                }

            }
        });
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.action_voice_rec)
                {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    //... put other settings in the Intent
                    startActivityForResult(intent, 0);
                }
            }
        });

        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            mSearchView.setSearchFocused(true);
            mSearchView.setSearchText(results.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void checkVoiceRecognition() {
        // Check if voice recognition is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            Toast.makeText(this, "Voice recognizer not present",
                    Toast.LENGTH_SHORT).show();
        }
    }


}