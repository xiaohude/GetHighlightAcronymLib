package com.smarttiger.gethighlightacronymlib;


import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TestMain extends Activity {
	
	String[] testNames = {
			"张三",
			"李四",
			"张三丰",
			"哈哈郑爽",
			"Mrs Zhang Sims",
			"Steven Allan Spielberg"
	};
	String searchStr = "zs";

	TextView textView;
	String logtext = "";
	String name = "";
	ArrayList<HighlightIndex> indexs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textView = (TextView) findViewById(R.id.text);
		
		for (int i = 0; i < testNames.length; i++) {
			name = testNames[i];
			indexs = FormatUtils.indexOfName(name, searchStr);
			if(indexs.size() > 0)
				for (HighlightIndex index : indexs) {
					logtext = logtext +name+"--"+index.start+"--"+index.end+"\n";
				}
			else
				logtext = logtext +name+"--0"+"\n";
		}
		
		textView.setText(logtext);
	}
	
}
