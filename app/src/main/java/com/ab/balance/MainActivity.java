package com.ab.balance;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity 
{
	static Activity ctx;    
	static TextView data;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		ctx = this;
		
		data = findViewById(R.id.data);
		//String dat=Filess.read(getApplicationContext(),"1arabic.txt",5000,true);
		//data.setText(dat);
		
		
		Switch sw = findViewById(R.id.start);
		
		sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					if(isChecked){
						startService(new Intent(MainActivity.this, FloatWindowService.class));  
					}else{
						stopService(new Intent(MainActivity.this, FloatWindowService.class));  
					}
				}
			});
		Intent i = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
		Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).setData(Uri.parse("package:" + getPackageName()));
		
		boolean granted = false;
		AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
		int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, 
										 android.os.Process.myUid(), getPackageName());

		if (mode == AppOpsManager.MODE_DEFAULT) {
			granted = (checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
		} else {
			granted = (mode == AppOpsManager.MODE_ALLOWED);
		}
		if(!granted)requestPermission(i);
		//startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
		if(!Settings.canDrawOverlays(this)){
			requestPermission(myIntent);
		}
		
    }
	private void requestPermission(Intent intent) {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
			return;
		}
		
			startActivity(intent);
		
	}
}
