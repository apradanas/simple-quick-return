package com.apradanas.simplequickreturnsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        findViewById(R.id.main_button_listViewHeader).setOnClickListener(mGlobalClickListener);
        findViewById(R.id.main_button_listViewFooter).setOnClickListener(mGlobalClickListener);
        findViewById(R.id.main_button_listViewHeaderFooter).setOnClickListener(mGlobalClickListener);
    }
    
    private OnClickListener mGlobalClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = null;
			Bundle bundle = new Bundle();
			switch(v.getId()) {
			case R.id.main_button_listViewHeader:
				bundle.putBoolean("withHeader", true);
				bundle.putBoolean("withFooter", false);
				intent = new Intent(MainActivity.this, ListViewSampleActivity.class);
				break;
			case R.id.main_button_listViewFooter:
				bundle.putBoolean("withHeader", false);
				bundle.putBoolean("withFooter", true);
				intent = new Intent(MainActivity.this, ListViewSampleActivity.class);
				break;
			case R.id.main_button_listViewHeaderFooter:
				bundle.putBoolean("withHeader", true);
				bundle.putBoolean("withFooter", true);
				intent = new Intent(MainActivity.this, ListViewSampleActivity.class);
				break;
			}
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};
}
