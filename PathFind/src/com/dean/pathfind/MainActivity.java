package com.dean.pathfind;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	int[][] map;
	public static final int WIDTH_COUNT = 10;
	public static final int HEIGHT_COUNT = 15;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(new PathFindView(this));
	}

	private int[][] generateMap() {
		int[][] map = new int[HEIGHT_COUNT][HEIGHT_COUNT];
		for (int i = 0; i < HEIGHT_COUNT - 1; i++) {
			for (int j = 0; j < WIDTH_COUNT - 1; j++) {

			}
		}
		return map;
	}

}
