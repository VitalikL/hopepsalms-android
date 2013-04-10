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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.cryart.hopepsalms.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.view.View;

public class MainActivity extends SherlockActivity {
	private SlidingMenu menu;
	private ListView m_listview;
	private SimpleFilterableAdapter<String> adapter = null;
	private EditText filterText = null;	
	private TextView hymnal_content;
	private TextView action_bar_style = null;
	private ScrollView hymnal_scroll;
	private SharedPreferences mPreferences;

	private String current_hymn_number;

	public final static String EXTRA_MESSAGE = "com.vitalik.hopepsalms.MESSAGE";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// Setting up sliding menu
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);

		// Connecting left menu to slider
		menu.setMenu(R.layout.activity_sidebar);

		// Looking for the components by ID
		hymnal_content = (TextView) findViewById(R.id.hymnal_content);
		hymnal_scroll = (ScrollView) findViewById(R.id.hymnal_scroll);
		action_bar_style = (TextView) findViewById(R.id.action_bar_style);
		m_listview = (ListView) findViewById(R.id.id_list_view);
		filterText = (EditText) findViewById(R.id.search_box);

		mPreferences =  this.getSharedPreferences("com.cryart.hopepsalms", Context.MODE_PRIVATE);

		// Setting up initial title
		getSupportActionBar().setTitle(R.string.app_name);

		// Changing default icon
		getSupportActionBar().setIcon(R.drawable.ic_btn_list_up);

		// Hiding title, but showing an icon
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		// Enabling the 
		getSupportActionBar().setHomeButtonEnabled(true);

		List<String> objects = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.hymnal_list)));

		// Using custom Filterable Adapter to perform smarter filtering
		m_listview.setAdapter(adapter = new SimpleFilterableAdapter<String>(
				this,
				R.layout.list_view,
				objects)
				);

		m_listview.setOnItemClickListener(new OnItemClickListener(){
			@Override public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3){
				Object listItem = m_listview.getItemAtPosition(position);

				// Assumption is that our selected item has a number
				// and this number is referring to the hymnal order number
				Pattern intsOnly = Pattern.compile("\\d+");
				Matcher makeMatch = intsOnly.matcher(listItem.toString());
				makeMatch.find();

				String hymn_number = makeMatch.group();
				try{
					openHymn(hymn_number);
				} catch (Exception e){
					// Something went terribly wrong
				}
			}
		});

		filterText.addTextChangedListener(filterTextWatcher);

		// Hiding keyboard
		menu.setOnCloseListener(new SlidingMenu.OnCloseListener() {
			@Override
			public void onClose() {
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(filterText.getWindowToken(), 0);
			}
		});

		String lastHymn = mPreferences.getString("lastHymn", "1");
		openHymn(lastHymn);
	}

	/*
	 * Setting the selected hymnal text to
	 * the main activity TextView
	 * 
	 * As well as performs minor tweaks,
	 * such as set the actionbar title and
	 * slide back to the main activity
	 */
	public void openHymn(String hymnNumber){
		current_hymn_number = hymnNumber;

		// Setting title text as the current hymnal number
		action_bar_style.setText("№ "+hymnNumber);

		// Closing the sliding menu
		menu.showContent();

		// Getting hymnal's text
		String hymnal = getString(getResources().getIdentifier("hp_"+hymnNumber, "string", "com.cryart.hopepsalms"));

		// Setting up hymnal text as HTML
		hymnal_content.setText(Html.fromHtml(hymnal));

		// Scrolling up the main activity
		hymnal_scroll.fullScroll(View.FOCUS_UP);

		mPreferences.edit().putString("lastHymn", hymnNumber).commit();
	}

	/*
	 * Text watcher to perform filtering
	 * for the ListView
	 * 
	 */
	private TextWatcher filterTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable s) {
			adapter.getFilter().filter(s.toString().trim());
		}

		public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
		public void onTextChanged(CharSequence s, int start, int before,int count) {}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Catching click on the home icon
		case android.R.id.home:
			menu.toggle();
			return true;

			// Proudly showing "fancy" about dialog
		case R.id.menu_about:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.about)
			.setMessage(Html.fromHtml(getString(R.string.about_description)))
			.setCancelable(false)
			.setNegativeButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			((TextView) alert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
			return true;
		case R.id.menu_scores:
			Intent intent = new Intent(MainActivity.this, ScoresActivity.class);
			intent.putExtra(EXTRA_MESSAGE, current_hymn_number);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
