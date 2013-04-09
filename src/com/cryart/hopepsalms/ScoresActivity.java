/*
 * The MIT License
 * Copyright (c) 2013 Vitalik Lim (lim.vitaliy@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.cryart.hopepsalms;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.cryart.hopepsalms.R;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ScoresActivity extends SherlockActivity {
	private WebView scores_view;
	private String current_hymn_number;
	private TextView action_bar_style;
	private ProgressBar loading_icon;

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scores);
		
		Intent intent = getIntent();
		current_hymn_number = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		
		// Looking for UI components
		scores_view = (WebView) findViewById(R.id.scores_view);
		action_bar_style = (TextView) findViewById(R.id.action_bar_style);
		loading_icon = (ProgressBar) findViewById(R.id.progressBar1);
		
	    // Setting up the back icon
	    getSupportActionBar().setIcon(R.drawable.ic_navigation_back);
	    
        // Hiding title, but showing an icon
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        
        // Enabling home button 
        getSupportActionBar().setHomeButtonEnabled(true);
        
        // Enabling click on this button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        
        // Trying to make the title in the center
        // by adjusting right side
        action_bar_style.setPadding(0, 0, 80, 0);
        
        // Setting the title
        action_bar_style.setText("№ "+current_hymn_number);
        
        // Configuring WebView to be pretty
	    scores_view.getSettings().setLoadWithOverviewMode(true);
        scores_view.getSettings().setUseWideViewPort(true);
        scores_view.getSettings().setJavaScriptEnabled(true);
        scores_view.getSettings().setBuiltInZoomControls(true);
        scores_view.getSettings().setSupportZoom(true);
        
        scores_view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            	// Page is loaded, hide the loading spinner icon
                loading_icon.setVisibility(View.INVISIBLE);
            }
        });
        
        // Magic happens here
        scores_view.loadUrl("http://hopepsalms.com/android/"+current_hymn_number);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
