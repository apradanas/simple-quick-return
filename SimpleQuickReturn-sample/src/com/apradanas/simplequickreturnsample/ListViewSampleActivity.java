package com.apradanas.simplequickreturnsample;

import java.util.ArrayList;

import com.apradanas.simplequickreturn.SimpleQuickReturn;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListViewSampleActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		boolean withHeader = bundle.getBoolean("withHeader", true);
		boolean withFooter = bundle.getBoolean("withFooter", true);

		View view = null;
		
		if(withHeader && withFooter) {
			view = new SimpleQuickReturn(getApplicationContext())
					.setContent(R.layout.activity_list_view_sample)
					.setHeader(R.layout.header)
					.setFooter(R.layout.footer)
					.createView();
		} else if (withHeader) {
			view = new SimpleQuickReturn(getApplicationContext())
					.setContent(R.layout.activity_list_view_sample)
					.setHeader(R.layout.header)
					.createView();
		} else if (withFooter) {
			view = new SimpleQuickReturn(getApplicationContext())
					.setContent(R.layout.activity_list_view_sample)
					.setFooter(R.layout.footer)
					.createView();
		}
		setContentView(view);
		
		ListView listView = (ListView) findViewById(android.R.id.list);
		ArrayList<String> items = new ArrayList<String>();
		for(int i = 0; i < 100; i++) {
			items.add("Row " + i);
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
	}
}
