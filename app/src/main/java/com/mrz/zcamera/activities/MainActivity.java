package com.mrz.zcamera.activities;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import android.media.*;
import android.net.*;
import android.text.*;
import android.util.*;
import android.webkit.*;
import android.animation.*;
import android.view.animation.*;
import java.util.*;
import java.text.*;
import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import android.view.View;
import android.Manifest;
import android.content.pm.PackageManager;

import com.mrz.zcamera.R;
import com.mrz.zcamera.activities.FileUtil;


public class MainActivity extends Activity {

    private Timer _delay = new Timer();
	
	private String folderPath = "";
	private String path = "";
	private boolean cameraFacingBack = false;
	private boolean hasCountDown = false;
	private double countdown_int = 0;
	private boolean hasFlashLight = false;
	private double cdtClicks = 0;
	private double timer = 0;
	
	private LinearLayout base_view;
	private LinearLayout cam_controls;
	private LinearLayout layout_controls;
	private LinearLayout layout_controls2;
	private LinearLayout bottom_controls;
	private LinearLayout top_controls;
	private TextView text_countdown;
	private ImageView flash;
	private ImageView countdown;
	private ImageView ic_info;
	private LinearLayout bg_takephoto;
	private ImageView ic_facing;
	private ImageView ic_takephoto;
	
	private Calendar cal = Calendar.getInstance();
	 
	private TimerTask countDown;
	private static int picture_width = 720;
    private static int picture_height = 1280;
    private static ZCameraView zcameraView;
    private android.hardware.Camera.PictureCallback mPicture;
    private static int picture_quality = 100;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializeVars(savedInstanceState);
		//Ask and Grant Permission Uri
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
			|| checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
				requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
			}
			else {
				initializeCreate();
			}
		}
		else {
			initializeCreate();
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeCreate();
		}
	}
	

   private void initializeVars(Bundle savedInstanceState) {
		
		//Layout initializing
		base_view = (LinearLayout) findViewById(R.id.base_view);
		cam_controls = (LinearLayout) findViewById(R.id.cam_controls);
		layout_controls = (LinearLayout) findViewById(R.id.layout_controls);
		layout_controls2 = (LinearLayout) findViewById(R.id.layout_controls2);
		bottom_controls = (LinearLayout) findViewById(R.id.bottom_controls);
		top_controls = (LinearLayout) findViewById(R.id.top_controls);
		text_countdown = (TextView) findViewById(R.id.text_countdown);
		flash = (ImageView) findViewById(R.id.flash);
		countdown = (ImageView) findViewById(R.id.countdown);
		ic_info = (ImageView) findViewById(R.id.ic_info);
		bg_takephoto = (LinearLayout) findViewById(R.id.bg_takephoto);
		ic_facing = (ImageView) findViewById(R.id.ic_facing);
		ic_takephoto = (ImageView) findViewById(R.id.ic_takephoto);

        
        //TODO:Enable and Unable Camera Torch
	    flash.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (hasFlashLight) {
					//on
					hasFlashLight = false;
					flash.setImageResource(R.drawable.flash_off);
				}
				else {
					//off
					hasFlashLight = true;
					flash.setImageResource(R.drawable.flash_on);
				}
			}
		});
		//end
		
		//Enable countdown timer for camera delay
		countdown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
			    //countdownClicks to detect the Delay type
				cdtClicks++;
				if (cdtClicks == 1) {
					//3seconds delay
					countdown_int = 3;
					hasCountDown = true;
					countdown.setImageResource(R.drawable.timer_3s);
					cdtClicks = 1;
					showToast("3 Seconds");
				}
				else {
					if (cdtClicks == 2) {
						//10seconds delay
						countdown_int = 10;
						hasCountDown = true;
						countdown.setImageResource(R.drawable.timer_10s);
						cdtClicks = 2;
						showToast("10 Seconds");
					}
					else {
						if (cdtClicks == 0) {
							//Delay is off
							countdown_int = 0;
							hasCountDown = false;
							countdown.setImageResource(R.drawable.timer_off);
							cdtClicks = 0;
							showToast("Timer is off");
						}
						else {
							//also off when delay type is not detected
							countdown_int = 0;
							hasCountDown = false;
							countdown.setImageResource(R.drawable.timer_off);
							cdtClicks = 0;
							showToast("Timer is off");
						}
					}
				}
			}
		});
		//end
		
		//Taking picture
		bg_takephoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (hasCountDown) {
				//detect if CountDown is enabled
					startCount(countdown_int);
					hasCountDown = false;
				}
				else {
					if (hasFlashLight) {
					//if camera torch is enabled
						flashCameraFunction(hasFlashLight);
						hasFlashLight = false;
					}
					else {
						if (hasFlashLight && hasCountDown) {
						//if uses the two features which is Torch
						//and Timer
						
							flashCameraFunction(hasFlashLight);
							startCount(countdown_int);
							hasFlashLight = false;
							hasCountDown = false;
						}
						else {
							savePicture(bg_takephoto);
						}
					}
				}
			}
		});
       //end
		
		//change camera facing which is
		//Front and Back Camera
		
		ic_facing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (cameraFacingBack) {
					cameraFacingBack = false;
					ic_facing.setImageResource(R.drawable.facing_front);
					showToast("Front Camera");
				}
				else {
					cameraFacingBack = true;
					ic_facing.setImageResource(R.drawable.facing_rear);
					showToast("Rear Camera");
				}
				zcameraView.close();
				zcameraView.openAsync(ZCameraView.findCameraId(cameraFacingBack ? android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK : android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT));
				
         }
       });
       //About me
       ic_info.setOnClickListener(new View.OnClickListener(){
       public void onClick(View v){
       
       }
      });
        
       
	 }
	 
	private void initializeCreate() {
		 getActionBar().hide();
		
		//UI Design
		setRoundOutline(bg_takephoto, 60);
		setColorFilter(ic_info, "#e040fb");
		setColorFilter(ic_facing, "#e040fb");
		setColorFilter(ic_takephoto, "#e040fb");
		setColorFilter(flash, "#e040fb");
		setColorFilter(countdown, "#e040fb");
		//hide the timer
		text_countdown.setVisibility(View.GONE);
		//setup ZCameraView configurations
		//This is where the captured jpg file will be placed to this folder
		//NOTE: JPG has no transparency
		folderPath = FileUtil.getExternalStorageDir().concat("/DCIM/ZCamera");
		//camera facing
		//if back = true
		//if front = false
		cameraFacingBack = true;
		//other functions
		countdown_int = 0;
		cdtClicks = 0;
		hasFlashLight = false;
		hasCountDown = false;
		/***
		*setup ZCameraView with Container to be used as a camera
		*NOTE: ZCameraView is a Custom Layout, and it is Library
		***/
		//Remove the Parent Layout first
		removeView(cam_controls);
		//Create and setup ZCameraView
		zcameraView = new ZCameraView(this);
		
		zcameraView.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
			zcameraView.setUseOrientationListener(true);
		
		zcameraView.setTapToFocus();
		//Setup the layouts
		android.widget.RelativeLayout containerv = new android.widget.RelativeLayout(this);
		
		containerv.setLayoutParams(new LinearLayout.LayoutParams(-1,-1));
		
		base_view.addView(containerv);
		
		containerv.addView(zcameraView);
		
		containerv.addView(cam_controls);
		mPicture = new android.hardware.Camera.PictureCallback() {
			
			@Override
			public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
				
				java.io.File pictureFile = new java.io.File(path);
				
				try {
					java.io.FileOutputStream fos = new java.io.FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
				} catch (Exception e) {
					showToast(e.toString());
				};
				
				camera.stopPreview();
				
				camera.startPreview();
				
			}
		};
		zcameraView.setOnCameraListener(new ZCameraView.OnCameraListener() {
			
			public void onConfigureParameters(android.hardware.Camera.Parameters parameters) {
			}
			//Error while capturing
			public void onCameraError() {
				showToast("Error while capturing!");
			}
			//When the permission is granted
			public void onCameraReady(android.hardware.Camera camera) {
			}
			//When ZCameraView is ready to stream Media
			public void onPreviewStarted(android.hardware.Camera camera) {
			}
			//When leaving the app or else
			public void onCameraStopping(android.hardware.Camera camera) {
			}
			
		});
	}
     //
     @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
			
			default:
			break;
		}
	}
	
	//On Activity becoming visible
	@Override
	public void onStart() {
		super.onStart();
		//Setup camera facing
		zcameraView.openAsync(ZCameraView.findCameraId(cameraFacingBack ? android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK : android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT));
	}
     //When user pressed back or exit the activity
     @Override
	public void onBackPressed() {
	    //close camera for future usage
	    finishAffinity();
		zcameraView.close();
	}
	
//When user back to our app
	@Override
	public void onResume() {
		super.onResume();
		//Setup camera facing
		zcameraView.openAsync(ZCameraView.findCameraId(cameraFacingBack ? android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK : android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT));
	}
	
//When user uses other apps
	@Override
	public void onPause() {
		super.onPause();
		//close camera for future usage
		zcameraView.close();
	}
	
//When user exit the app
	@Override
	public void onDestroy() {
		super.onDestroy();
		//close camera for future usage
		zcameraView.close();
	}
	
//On Exit
	@Override
	public void onStop() {
		super.onStop();
		//close camera for future usage
		zcameraView.close();
	}
	
	
    //Camera Params
    public static void setParameters() {

android.hardware.Camera.Parameters pm = zcameraView.cam.getParameters()
;
pm.setJpegQuality(picture_quality)
;
//pm.setPreviewFpsRange(20,45);

pm.setPictureFormat(ImageFormat.JPEG);

//pm.setSceneMode(android.hardware.Camera.Parameters.SCENE_MODE_HDR);

List<android.hardware.Camera.Size> sizes = pm.getSupportedPictureSizes();
android.hardware.Camera.Size size = sizes.get(0);
for (int i = 0; i < sizes.size(); i++) {
if (sizes.get(i).width > size.width) size = sizes.get(i);
};
pm.setPictureSize(size.width, size.height);

zcameraView.cam.setParameters(pm);

      
    }
    //
	//end
 private void savePicture (final View v) {
		randomFileName("Pitik");
		if (!FileUtil.isExistFile(folderPath)) {
	        FileUtil.makeDir(folderPath);
		}
		if (zcameraView.isOpen) {
			
			zcameraView.cam.takePicture(null, null, mPicture);
			
			showToast("Sinave sa " + path);
			
		} else {
			
			showToast("Camera is not ready yet");
			
		};
	}
	
	//end
	private void randomFileName (final String pref) {
	//generate random file name for picture, base on Time and Seconds
		cal = Calendar.getInstance();
		path = folderPath.concat("/".concat(pref.trim().concat(" - ".concat(String.valueOf((long)(cal.getTimeInMillis())).concat(".jpg")))));
	}
	
	
	private void removeView (final View v) {
		if (v.getParent() != null) ((ViewGroup)v.getParent()).removeView(v);
	}
	
	
	private void setRoundOutline (final View view, final double round) {
		android.graphics.drawable.GradientDrawable drwbl = new android.graphics.drawable.GradientDrawable();
		
		drwbl.setColor(Color.TRANSPARENT);
		
		drwbl.setCornerRadius((int)round);
		
		drwbl.setStroke((int)getDip(1), Color.parseColor("#e040fb"));
		
		view.setBackground(drwbl);
	}
	
	
	private void showToast (final String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
		
	}
	
	
	private void setColorFilter (final ImageView iv, final String color) {
		try{
			iv.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_IN);
		} catch (Exception e){
		}
	}
	
	
	private void flashCameraFunction (final boolean hasAFlash) {
		
	}
	
	
	private void startCount (final double count) {
		text_countdown.setVisibility(View.VISIBLE);
		timer = count;
		countDown = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!(timer == -1)) {
							//still counting
							text_countdown.setVisibility(View.VISIBLE);
							text_countdown.setText(String.valueOf((long)(timer)).concat("s"));
							timer--;
						}
						else {
							//Countdown is done and lets take a picture
							text_countdown.setVisibility(View.GONE);
							countDown.cancel();
							showToast("Say Cheese!");
							savePicture(bg_takephoto);
							timer = count;
						}
					}
				});
			}
		};
		_delay.scheduleAtFixedRate(countDown, (int)(0), (int)(1000));
	}
	
	//I was coded it here because it is Static class
	
	public static class ZCameraView extends android.widget.FrameLayout {

public interface OnCameraListener {
void onConfigureParameters(android.hardware.Camera.Parameters parameters);
void onCameraError();
void onCameraReady(android.hardware.Camera camera);
void onPreviewStarted(android.hardware.Camera camera);
void onCameraStopping(android.hardware.Camera camera);
}

public final android.graphics.Rect previewRect = new android.graphics.Rect();

private final Runnable focusRunnable = new Runnable() {
@Override
public void run() {
setFocusArea(null);
}
};

private boolean isOpen = false;
private boolean useOrientationListener = false;
private OnCameraListener cameraListener;
private HandlerThread cameraCallbackThread;
private android.hardware.Camera cam;
private android.view.OrientationEventListener orientationListener;
private int tries = 0;
private int viewWidth;
private int viewHeight;
private int frameWidth;
private int frameHeight;
private int frameOrientation;

public static int findCameraId(int facing) {
for (int i = 0, l = android.hardware.Camera.getNumberOfCameras(); i < l; ++i) {
android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
android.hardware.Camera.getCameraInfo(i, info);
if (info.facing == facing) {
return i;
}
}
return -1;
}

public static int getRelativeCameraOrientation(Context context, int cameraId) {
android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
android.hardware.Camera.getCameraInfo(cameraId, info);
int orientation = info.orientation;
if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
orientation -= 180;
}
return (orientation - getDeviceRotation(context) + 360) % 360;
}

public static int getDeviceRotation(Context context) {
switch (((android.view.WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation()) {
case Surface.ROTATION_90:
return 90;
case Surface.ROTATION_180:
return 180;
case Surface.ROTATION_270:
return 270;
case Surface.ROTATION_0:
default: return 0;
}
}

@android.annotation.TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public static boolean setAutoFocus(android.hardware.Camera.Parameters parameters) {

String continuousPicture = android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;

String continuousVideo = android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;

String autoFocus = android.hardware.Camera.Parameters.FOCUS_MODE_AUTO;

java.util.List<String> focusModes = parameters.getSupportedFocusModes();

if (focusModes.contains(continuousPicture)) {
parameters.setFocusMode(continuousPicture);
} else if (focusModes.contains(continuousVideo)) {
parameters.setFocusMode(continuousVideo);
} else if (focusModes.contains(autoFocus)) {
parameters.setFocusMode(autoFocus);
} else {
return false;
}
return true;
}

@android.annotation.SuppressLint("ClickableViewAccessibility")
public void setTapToFocus() {
setOnTouchListener(new View.OnTouchListener() {
@Override
public boolean onTouch(View v, MotionEvent event) {
if (event.getActionMasked() == MotionEvent.ACTION_UP && !focusTo(v, event.getX(), event.getY())) {
v.setOnTouchListener(null);
return false;
}
v.performClick();
return true;
}
});
}

public boolean focusTo(final View v, float x, float y) {
if (cam == null) {
return false;
}
try {
cam.cancelAutoFocus();
if (!setFocusArea(calculateFocusRect(x, y, 100))) {
return false;
}
cam.autoFocus(new android.hardware.Camera.AutoFocusCallback() {
@Override
public void onAutoFocus(boolean success, android.hardware.Camera camera) {
v.removeCallbacks(focusRunnable);
v.postDelayed(focusRunnable, 3000);
}
});
} catch (RuntimeException e) {
return false;
}
return true;
}

public static android.hardware.Camera.Size findBestPreviewSize(java.util.List<android.hardware.Camera.Size> sizes, int width, int height) {
final double ASPECT_TOLERANCE = 0.1;
double targetRatio = (double) width / height;
double minDiff = Double.MAX_VALUE;
double minDiffAspect = Double.MAX_VALUE;
android.hardware.Camera.Size bestSize = null;
android.hardware.Camera.Size bestSizeAspect = null;


for (android.hardware.Camera.Size size : sizes) {
			double diff = (double) Math.abs(size.height - height) +
					Math.abs(size.width - width);

			if (diff < minDiff) {
				bestSize = size;
				minDiff = diff;
			}

			double ratio = (double) size.width / size.height;

			if (Math.abs(ratio - targetRatio) < ASPECT_TOLERANCE &&
					diff < minDiffAspect) {
				bestSizeAspect = size;
				minDiffAspect = diff;
			}
		}

		return bestSizeAspect != null ? bestSizeAspect : bestSize;
	}

	public ZCameraView(Context context) {
		super(context);
	}

	public ZCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ZCameraView(
			Context context,
			AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setUseOrientationListener(boolean use) {
		useOrientationListener = use;
	}

	public void openAsync(final int cameraId) {
		if (isOpen || cameraCallbackThread != null) {
			return;
		}
		isOpen = true;
		cameraCallbackThread = new HandlerThread(
				"CameraCallbackHandlerThread");
		cameraCallbackThread.start();
		Handler callbackThreadHandler = new Handler(
				cameraCallbackThread.getLooper());
		callbackThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				if (cam != null) {
					return;
				}
				final android.hardware.Camera camera = openCameraAndCatch(cameraId);
				ZCameraView.this.post(new Runnable() {
					@Override
					public void run() {
						initCamera(camera, cameraId);
					}
				});
			}
		});
	}

	public void close() {
		isOpen = false;
		if (orientationListener != null) {
			orientationListener.disable();
			orientationListener = null;
		}
		if (cam != null) {
			if (cameraListener != null) {
				cameraListener.onCameraStopping(cam);
			}
			cam.stopPreview();
			cam.setPreviewCallback(null);
			cam.release();
			cam = null;
		}
		if (cameraCallbackThread != null) {
			cameraCallbackThread.quit();
			try {
				cameraCallbackThread.join();
			} catch (InterruptedException ignore) {
			}
			cameraCallbackThread = null;
		}
		removeAllViews();
	}

	public void setOnCameraListener(OnCameraListener listener) {
		cameraListener = listener;
	}

	public android.hardware.Camera getCamera() {
		return cam;
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

	public int getFrameOrientation() {
		return frameOrientation;
	}

	public android.graphics.Rect calculateFocusRect(float x, float y, int radius) {
		int cx = Math.round(2000f / viewWidth * x - 1000f);
		int cy = Math.round(2000f / viewHeight * y - 1000f);
		return new Rect(
				Math.max(-1000, cx - radius),
				Math.max(-1000, cy - radius),
				Math.min(1000, cx + radius),
				Math.min(1000, cy + radius));
	}

	@android.annotation.TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public boolean setFocusArea(Rect area) {
		if (cam == null || Build.VERSION.SDK_INT <
				Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return false;
		}
		try {
			android.hardware.Camera.Parameters parameters = cam.getParameters();
			if (parameters.getMaxNumFocusAreas() > 0) {
				if (area != null) {
					java.util.List<android.hardware.Camera.Area> focusAreas =
							new ArrayList<android.hardware.Camera.Area>();
					focusAreas.add(new android.hardware.Camera.Area(area, 1000));
					parameters.setFocusAreas(focusAreas);
					parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO);
				} else {
					parameters.setFocusAreas(null);
					ZCameraView.setAutoFocus(parameters);
				}
			}
			cam.setParameters(parameters);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	@Override
	protected void onLayout(
			boolean changed,
			int left,
			int top,
			int right,
			int bottom) {
		if (!changed) {
			return;
		}
		viewWidth = right - left;
		viewHeight = bottom - top;
		if (cam != null && getChildCount() == 0) {
			Context context = getContext();
			if (context == null) {
				return;
			}
			addPreview(context);
		}
	}

	private static android.hardware.Camera openCameraAndCatch(int cameraId) {
		try {
			return android.hardware.Camera.open(cameraId);
		} catch (RuntimeException e) {
			return null;
		}
	}

	private void initCamera(android.hardware.Camera camera, int cameraId) {
		if (!isOpen) {
			if (camera != null) {
				camera.release();
			}
			return;
		}
		if (camera == null) {
			if (cameraListener != null &&
					cam == null) {
				if (tries < 3) {
					isOpen = false;
					openAsync(cameraId);
					++tries;
				} else {
					cameraListener.onCameraError();
				}
			}
			return;
		}
		tries = 0;
		cam = camera;
		camera.setErrorCallback(new android.hardware.Camera.ErrorCallback() {
			public void onError(int error, android.hardware.Camera camera) {
				if (cameraListener != null) {
					cameraListener.onCameraError();
				}
			}
		});
		Context context = getContext();
		if (context == null) {
			close();
			return;
		}
		if (useOrientationListener) {
			enableOrientationListener(context, cameraId);
		}
		frameOrientation = getRelativeCameraOrientation(context, cameraId);
		if (viewWidth > 0) {
			addPreview(context);
		}
	}

	private void enableOrientationListener(Context context,
			final int cameraId) {
		final android.view.Display defaultDisplay = ((android.view.WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		final int defaultOrientation = defaultDisplay.getRotation();
		orientationListener = new android.view.OrientationEventListener(context,
				android.hardware.SensorManager.SENSOR_DELAY_NORMAL) {
			@Override
			public void onOrientationChanged(int orientation) {
				if (Math.abs(defaultOrientation -
						defaultDisplay.getRotation()) == 2) {
					close();
					openAsync(cameraId);
				}
			}
		};
		orientationListener.enable();
	}

	private void addPreview(Context context) {
		boolean transpose;
		try {
			transpose = setCameraParameters();
		} catch (RuntimeException e) {
			if (cameraListener != null) {
				cameraListener.onCameraError();
			}
			return;
		};
		int childWidth;
		int childHeight;
		if (transpose) {
			childWidth = frameHeight;
			childHeight = frameWidth;
		} else {
			childWidth = frameWidth;
			childHeight = frameHeight;
		}
		addSurfaceView(context, childWidth, childHeight);
		if (cameraListener != null) {
			cameraListener.onCameraReady(cam);
		}
	}

	private boolean setCameraParameters() throws RuntimeException {
		boolean transpose = frameOrientation == 90 || frameOrientation == 270;
		android.hardware.Camera.Parameters parameters = cam.getParameters();
		parameters.setRotation(frameOrientation);
		setPreviewSize(parameters, transpose);
		if (cameraListener != null) {
			cameraListener.onConfigureParameters(parameters);
		}
		android.hardware.Camera.Size size = parameters.getPreviewSize();
		if (size != null) {
			frameWidth = size.width;
			frameHeight = size.height;
		}
		cam.setParameters(parameters);
		cam.setDisplayOrientation(frameOrientation);
		return transpose;
	}

	private void setPreviewSize(
			android.hardware.Camera.Parameters parameters,
			boolean transpose) {
		if (transpose) {
			frameWidth = viewHeight;
			frameHeight = viewWidth;
		} else {
			frameWidth = viewWidth;
			frameHeight = viewHeight;
		}
		android.hardware.Camera.Size size = findBestPreviewSize(
				// will always return at least one item
				parameters.getSupportedPreviewSizes(),
				frameWidth,
				frameHeight);
		parameters.setPreviewSize(size.width, size.height);
	}

	private void addSurfaceView(
			Context context,
			int surfaceWidth,
			int surfaceHeight) {
		android.view.SurfaceView surfaceView = new android.view.SurfaceView(context);
		android.view.SurfaceHolder holder = surfaceView.getHolder();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			holder.setType(android.view.SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		holder.setKeepScreenOn(true);
		holder.addCallback(new android.view.SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(android.view.SurfaceHolder holder) {
				// wait until the surface has dimensions
			}

			@Override
			public void surfaceChanged(
					android.view.SurfaceHolder holder,
					int format,
					int width,
					int height) {
				if (cam == null) {
					return;
				}
				try {
					cam.setPreviewDisplay(holder);
				} catch (java.io.IOException e) {
					return;
				};
setParameters();
				cam.startPreview();
				if (cameraListener != null) {
					cameraListener.onPreviewStarted(cam);
				}
			}

			@Override
			public void surfaceDestroyed(android.view.SurfaceHolder holder) {
				close();
			}
		});
		addView(surfaceView);
		setChildLayout(
				viewWidth,
				viewHeight,
				surfaceView,
				surfaceWidth,
				surfaceHeight,
				previewRect);
	}

	private static void setChildLayout(
			int width,
			int height,
			View child,
			int childWidth,
			int childHeight,
			Rect childRect) {
		int widthByHeight = width * childHeight;
		int heightByWidth = height * childWidth;
		boolean dontScaleBeyondScreen = Build.VERSION.SDK_INT <
				Build.VERSION_CODES.ICE_CREAM_SANDWICH;

		if (dontScaleBeyondScreen ?
				// center within parent view
				widthByHeight > heightByWidth :
				// scale to cover parent view
				widthByHeight < heightByWidth) {
			childWidth = childWidth * height / childHeight;
			childHeight = height;
		} else {
			childHeight = childHeight * width / childWidth;
			childWidth = width;
		}

		int l = (width - childWidth) >> 1;
		int t = dontScaleBeyondScreen ?
				(height - childHeight) >> 1 :
				0;

		childRect.set(
				l,
				t,
				l + childWidth,
				t + childHeight);

		child.layout(
				childRect.left,
				childRect.top,
				childRect.right,
				childRect.bottom);
	}
	}
		@Deprecated
	public float getDip(int _input){
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	

	
	
}

