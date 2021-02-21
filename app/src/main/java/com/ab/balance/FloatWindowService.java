package com.ab.balance;

import android.app.*;
import android.app.usage.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.util.*;
import java.util.concurrent.*;

public class FloatWindowService extends Service
{
	WindowManager windowManager;
	WindowManager.LayoutParams params;
	LinearLayout linearLayout;
	TextView data, textView;
	Handler handler;
	Runnable runnable;
	int width,height,viewWidth,viewHeight,w,h;
	DisplayMetrics displayMetrics;
	Display display;
	
	@Override
	public static final String CHANNEL_ID = "ForegroundServiceChannel";
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		
		
		displayMetrics = new DisplayMetrics();
		MainActivity.ctx.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		height = displayMetrics.heightPixels;
		width = displayMetrics.widthPixels;
		
		display = MainActivity.ctx.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		if (Build.VERSION.SDK_INT >= 17) {
			display.getRealSize(size);
		} else {
			display.getSize(size); // correct for devices with hardware navigation buttons
		}
		w = display.getWidth();
		h = display.getHeight();
		
		String topPackageName ="";
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { 
			UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService("usagestats");                       
			long time = System.currentTimeMillis(); 
			// We get usage stats for the last 10 seconds
			List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*60*5, time);                                    
			// Sort the stats by the last time used
			if(stats != null) {
				SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>();
				for (UsageStats usageStats : stats) {
					mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
					if(usageStats.getTotalTimeInForeground() != 0){
						
						try{
							if(getPackageManager().getApplicationInfo(usageStats.getPackageName(), 0).FLAG_SYSTEM != 0)
								topPackageName += usageStats.getPackageName() + " : " + (float)usageStats.getTotalTimeInForeground()/(1000*60*60)+"\n";
							
						}catch(Exception e){}
					
					}
				}                    
				if(!mySortedMap.isEmpty()) {
					//topPackageName =  mySortedMap.get(mySortedMap.lastKey()).getPackageName();                                   
				}                                       
			}
		}
		
		//Toast.makeText(this, topPackageName, Toast.LENGTH_LONG).show();
		
        
		String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
			.setSmallIcon(R.drawable.icon)
			.setContentTitle("App is running in background")
			.setPriority(NotificationManager.IMPORTANCE_MIN)
			.setCategory(Notification.CATEGORY_SERVICE)
			.build();
			
        startForeground(1, notification);
        //do heavy work on a background thread

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);  
		linearLayout = new LinearLayout(this);  
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);  
		linearLayout.setBackgroundColor(Color.argb(100, 0, 0, 0));  
		linearLayout.setLayoutParams(layoutParams);  
		textView = new TextView(this);
		textView.setText("Text");  
		textView.setTextSize(20);
		textView.setTextColor(Color.WHITE);  
		textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);  
		LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);  
// linearLayout.setBackgroundColor(Color.argb(66, 255, 0, 0));  
		textView.setLayoutParams(layoutParamsText);  

		linearLayout.addView(textView);  
		
		int LAYOUT_FLAG;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		} else {
			LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
		}

		params = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
			PixelFormat.TRANSLUCENT);
		
		//WindowManager.LayoutParams params = new WindowManager.LayoutParams(400,150,WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);  
		params.x = 0;  
		params.y = 0;  
		params.gravity = Gravity.CENTER | Gravity.CENTER;  
		windowManager.addView(linearLayout, params);
		
		
		
		
		handler = new android.os.Handler();
		
		runnable=new Runnable() {
			public void run() {
				//Log.i("tag", "This'll run 300 milliseconds later");
				
				
				String dat=Filess.read(getApplicationContext(),"1arabic.txt",rand(1,10000),true);
				dat=dat.replaceAll("[0-9]+"," ").trim();
				dat=dat.replaceAll("\\s+"," ").trim();
				String[] datArr=dat.split(" ");
				//Toast.makeText(getApplicationContext(),dat.toString(),Toast.LENGTH_LONG).show();
				String ar = datArr[0];
				String en = dat.replaceAll("[^a-zA-Z]+"," ").trim();
				
				try{
					data = MainActivity.data;//ctx.findViewById(R.id.data);
					data.setText(ar+"\n"+en);
				}catch(Exception e){}
				textView.setText(ar+"\n"+en);

				textView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
				viewWidth=textView.getMeasuredWidth(); 
				viewHeight=textView.getMeasuredHeight();
				
				int orientation = getApplicationContext().getResources().getConfiguration().orientation;
				if (orientation == Configuration.ORIENTATION_PORTRAIT) {
					// code for portrait mode

					height = display.getHeight();//displayMetrics.heightPixels;
					width = display.getWidth();//displayMetrics.widthPixels;
					
				} else {
					// code for landscape mode

					width = display.getHeight();//displayMetrics.heightPixels;
					height = display.getWidth();//displayMetrics.widthPixels;
					
				}
				
				handler.postDelayed(
					runnable, 
					5000);
				
			}
		};

		
		handler.postDelayed(
				runnable, 
			0);
		
		linearLayout.setOnTouchListener(new View.OnTouchListener() {  
				WindowManager.LayoutParams updatedParams = params;  
				int x,y;  
				float touchX,touchY;  
				@Override  
				public boolean onTouch(View view, MotionEvent motionEvent) {  
					
					switch (motionEvent.getAction()){  
						case MotionEvent.ACTION_DOWN:  
							x= updatedParams.x;  
							y=updatedParams.y;  
							touchX = motionEvent.getRawX();  
							touchY = motionEvent.getRawY();  
							break;  
						case MotionEvent.ACTION_MOVE:  
							updatedParams.x = (int)(x+(motionEvent.getRawX() - touchX));  
							if(updatedParams.y-viewHeight/2<height/2)updatedParams.y = (int)(y+(motionEvent.getRawY() - touchY));  
							if(Math.abs(updatedParams.x)+viewWidth/2>width/2){
								if(updatedParams.x>0)
									updatedParams.x=width/2-viewWidth/2;
								else
									updatedParams.x=-(width/2-viewWidth/2);
								}
							if(Math.abs(updatedParams.y)+viewHeight/2>height/2){
								if(updatedParams.y>0)
									updatedParams.y=height/2-viewHeight/2;
								else
									updatedParams.y=-(height/2-viewHeight/2);
							}
							
							//Toast.makeText(getApplicationContext(),""+viewWidth+":"+viewHeight+":::"+updatedParams.x+":"+updatedParams.y,Toast.LENGTH_LONG).show();
							windowManager.updateViewLayout(linearLayout,updatedParams);  
							break;
						case MotionEvent.ACTION_UP:
						default:break;  
					}  

					return false;  
				}  
			})  
			; 

		
        //stopSelf();
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
		
		windowManager.removeView(linearLayout);
		
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
	
	public int rand(int min, int max){
		int randomNum = (int)(Math.random() * (max - min +1)) + min; 

		return randomNum;
	}
	
}
