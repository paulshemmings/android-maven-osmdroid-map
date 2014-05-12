package com.razor.osm.activities;

import android.app.Activity;
import android.os.Bundle;

import razor.android.osm.R;

/*
 * https://code.google.com/p/osmdroid/wiki/HowToIncludeInYourProject
 */

public class MapActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
    }

}
