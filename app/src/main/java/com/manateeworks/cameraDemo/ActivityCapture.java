/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * A Derivative Work, changed by Manatee Works, Inc.
 *
 */

package com.manateeworks.cameraDemo;

import com.manateeworks.BarcodeScanner;
import com.manateeworks.camera.CameraManager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

//import com.mdi.mdimobilelib.MdiUtils;

/**
 * The barcode reader activity itself. This is loosely based on the
 * CameraPreview example included in the Android SDK.
 */
public final class ActivityCapture extends Activity implements SurfaceHolder.Callback
{

	public static final boolean PDF_OPTIMIZED = false;
	
    // !!! Rects are in format: x, y, width, height !!!
    public static final Rect RECT_LANDSCAPE_1D = new Rect(3, 20, 94, 60);
    public static final Rect RECT_LANDSCAPE_2D = new Rect(20, 5, 60, 90);
    public static final Rect RECT_PORTRAIT_1D = new Rect(20, 3, 60, 94);
    public static final Rect RECT_PORTRAIT_2D = new Rect(20, 5, 60, 90);
    public static final Rect RECT_FULL_1D = new Rect(3, 3, 94, 94);
    public static final Rect RECT_FULL_2D = new Rect(20, 5, 60, 90);
    public static final Rect RECT_DOTCODE = new Rect(30, 20, 40, 60);
        
    private static final int ABOUT_ID = Menu.FIRST;
    private ActivityCaptureHandler handler;

    
    private View statusView;
    private View resultView;
    private byte[] lastResult;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private String versionName;
    public static String lastStringResult;
    private boolean copyToClipboard;
    
    // Scanning options (can be customized from config.xml)
    private int scanDirectionMask = BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL | BarcodeScanner.MWB_SCANDIRECTION_VERTICAL;
    private int effortLevel = 3;
    private int scanFormats = BarcodeScanner.MWB_CODE_MASK_PDF | BarcodeScanner.MWB_CODE_MASK_QR;
    private Rect scanArea = new Rect(0, 0, 100, 100);
    
    // Converts a format string code to integer value.
    public int stringToFormatCode(String s) {
    	
	    	if(s.compareToIgnoreCase("QR") == 0) {
	    		return BarcodeScanner.MWB_CODE_MASK_QR;
	    	}
	    	else if(s.compareToIgnoreCase("PDF") == 0 || s.compareToIgnoreCase("PDF417") == 0) {
	    		return BarcodeScanner.MWB_CODE_MASK_PDF;
	    	}
	    	else if(s.compareToIgnoreCase("CODE_25") == 0) {
	    		return BarcodeScanner.MWB_CODE_MASK_25;
	    	}
	    	else if(s.compareToIgnoreCase("CODE_39") == 0) {
	    		return BarcodeScanner.MWB_CODE_MASK_39;
	    	}
	    	else if(s.compareToIgnoreCase("CODE_128") == 0) {
	    		return BarcodeScanner.MWB_CODE_MASK_128;
	    	}
	    	else if(s.compareToIgnoreCase("Aztec") == 0) {
	    		return BarcodeScanner.MWB_CODE_MASK_AZTEC;
	    	}
	    	else if(s.compareToIgnoreCase("DataMatrix") == 0) {
	    		return BarcodeScanner.MWB_CODE_MASK_DM;
	    	}
            else if(s.compareToIgnoreCase("Data_Matrix") == 0) {
                return BarcodeScanner.MWB_CODE_MASK_DM;
            }
	    	else if(s.compareToIgnoreCase("UPC") == 0 || s.compareToIgnoreCase("EAN") == 0 || s.compareToIgnoreCase("ISBN") == 0) {
	    		return BarcodeScanner.MWB_CODE_MASK_EANUPC;
	    	}
	    	else if(s.compareToIgnoreCase("DataBar") == 0 || s.compareToIgnoreCase("GS1") == 0 || s.compareToIgnoreCase("RSS") == 0) {
	    		return BarcodeScanner.MWB_CODE_MASK_RSS;
	    	}
            else if(s.compareToIgnoreCase("Data_Bar") == 0 || s.compareToIgnoreCase("GS1") == 0 || s.compareToIgnoreCase("RSS") == 0) {
                return BarcodeScanner.MWB_CODE_MASK_RSS;
            }
	    	
	    	return 0;
    }

    
    public int dirStringToCode(String s) {
    	
	    	if(s.compareToIgnoreCase("horizontal") == 0) {
	    		return BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL;
	    	}
	    	else if(s.compareToIgnoreCase("vertical") == 0) {
	    		return BarcodeScanner.MWB_SCANDIRECTION_VERTICAL;
	    	}
	    	else if(s.compareToIgnoreCase("both") == 0) {
	    		return BarcodeScanner.MWB_SCANDIRECTION_VERTICAL | BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL;
	    	}
	    	else if(s.compareToIgnoreCase("omni") == 0) {
	    		return BarcodeScanner.MWB_SCANDIRECTION_OMNI;
	    	}
	    	
	    	return 0;
    }
    

    public Handler getHandler()
    {
        return handler;
    }
    
    // Load scanner settings specified in the bundle.
    public void loadSettingsFromBundle(Bundle bundle){
    	
	    	if(bundle == null)
	    		return;
	    	
	    	if(bundle.containsKey("effortLevel"))
	    	{
	    		this.effortLevel = Integer.parseInt(bundle.getString("effortLevel"));
	    	}
	    	
	    	if(bundle.containsKey("direction"))
	    	{
	    		String dirs = bundle.getString("direction");    	
	    		this.scanDirectionMask = this.dirStringToCode(dirs);
	    		if(this.scanDirectionMask == 0)
	    		{
	    			this.scanDirectionMask = BarcodeScanner.MWB_SCANDIRECTION_AUTODETECT;
	    		}
	    	}
	    	
	    	if(bundle.containsKey("scanAreaX"))
	    	{
	    		this.scanArea.left = Integer.parseInt(bundle.getString("scanAreaX"));
	    	}
	    	if(bundle.containsKey("scanAreaY"))
	    	{
	    		this.scanArea.top = Integer.parseInt(bundle.getString("scanAreaY"));
	    		
	    	}
	    	if(bundle.containsKey("scanAreaWidth"))
	    	{
	    		this.scanArea.right = Integer.parseInt(bundle.getString("scanAreaWidth")) + this.scanArea.left;
	    		
	    	}
	    	if(bundle.containsKey("scanAreaHeight"))
	    	{
	    		this.scanArea.bottom = Integer.parseInt(bundle.getString("scanAreaHeight")) + this.scanArea.top;    		
	    	}
	    	
	    	if(bundle.containsKey("formats"))
	    	{
	    		ArrayList<String> formats = bundle.getStringArrayList("formats");
	    		
	    		this.scanFormats = BarcodeScanner.MWB_CODE_MASK_NONE;
	    		for(String s : formats)
	    		{
	    			this.scanFormats |= this.stringToFormatCode(s);
	    		}
	    	}
    	
    }

    @Override
    public void onCreate(Bundle icicle)
    {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar bar = getActionBar();
        if (bar != null){
            getActionBar().hide();
        }

        super.onCreate(icicle);

//      Run time permission check to allow Camera on devices supporting OS 6(Marshmallow) and above
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.CAMERA},
//                    0);
//        }

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setContentView(MdiUtils.getResourceIdByName(this.getPackageName(), "layout", "capture"));
        
        
        // set up our cancel button
//        int id = MdiUtils.getResourceIdByName(this.getPackageName(), "id", "cancelBtn");
//
//        final Button button = (Button) findViewById(id);
//        final ActivityCapture me = this;
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // cancel pressed...
//            		me._cancelScan();
//            }
//        });
        
        // Setup the Light toggle button.
//        id = MdiUtils.getResourceIdByName(this.getPackageName(), "id", "toggleButton1");
//        final ToggleButton lightButton = (ToggleButton)findViewById(id);
        
//        if(lightButton != null ) {
//            lightButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//         			@Override
//         			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//         				me._toggleLight(arg1);
//                        if (me.handler != null) {
//                            me.handler.resetAutoFocus();
//                        }
//
//         			}
//
//            });
//
//            // Disable by default. It will be enabled later when the camera is enabled and if there is a light.
//           lightButton.setVisibility(View.GONE);
//
//        }

        this.loadSettingsFromBundle(icicle == null ? getIntent().getExtras() : icicle);

        // register your copy of the mobiScan SDK with the given user name / key
        /*
        SG.Android.PDF.4DL	67145DFE6112EF527ECB15A8E8DBFCB957B3E1E4C8BC78E50D653DA991D35969
		SG.Android.QR.UDL	F4BE0ADD1DF16AD05C42731EA07C7E0476C48191F68F70409CF06B2E6D8022C5
		SG.Android.C39.UDL	79E1652140F159596D59471E445DE468E7A529505E1D77B2F97ECBF1AF721B40
		SG.Android.C93.UDL	FA515BE6703993DAC6A65BE8A73EFFC049D9CA2BAF36CA7F734FE76E55640851
		SG.Android.DM.UDL	2247C70E0A9F665FA919F1912559138E8AE94CE544E12F67BCEE4B8E42B0E9E8
		SG.Android.EANUPC.UDL	8F0D7233DF33E96526149264414040C0FC63A6BC7271BB702862E2F7F0464CD1
		SG.Android.C128.UDL	D439BE28534FDFB16913D0C584E9C4550D41232E795F0CAC03ED3ADB6FA07E42
		SG.Android.C25.UDL	F0790953014F4CB8D0F8EEA211D03589AE8F0F9CBAADEBC7E13579B0A4AE882E
		SG.Android.CB.UDL	FF8CFAFD6271BF7D2F4788578EDB9180C165ED40810157A2A8235734001509D4
        */

        BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_PDF,    "SG.Android.PDF.4DL", "67145DFE6112EF527ECB15A8E8DBFCB957B3E1E4C8BC78E50D653DA991D35969");
        BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_QR,    "SG.Android.QR.UDL", "F4BE0ADD1DF16AD05C42731EA07C7E0476C48191F68F70409CF06B2E6D8022C5");        

        BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_39,    "SG.Android.C39.UDL", "79E1652140F159596D59471E445DE468E7A529505E1D77B2F97ECBF1AF721B40");        
        BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_93,    "SG.Android.C93.UDL", "FA515BE6703993DAC6A65BE8A73EFFC049D9CA2BAF36CA7F734FE76E55640851");
        
        BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_DM,     "SG.Android.DM.UDL", "2247C70E0A9F665FA919F1912559138E8AE94CE544E12F67BCEE4B8E42B0E9E8");
        
        BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_EANUPC,     "SG.Android.EANUPC.UDL", "8F0D7233DF33E96526149264414040C0FC63A6BC7271BB702862E2F7F0464CD1");
        
        BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_128, "SG.Android.C128.UDL",	"D439BE28534FDFB16913D0C584E9C4550D41232E795F0CAC03ED3ADB6FA07E42");
        BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_25, "SG.Android.C25.UDL",	"F0790953014F4CB8D0F8EEA211D03589AE8F0F9CBAADEBC7E13579B0A4AE882E");
        BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_CODABAR, "SG.Android.CB.UDL",	"FF8CFAFD6271BF7D2F4788578EDB9180C165ED40810157A2A8235734001509D4");

		BarcodeScanner.MWBsetDirection(this.scanDirectionMask);
        
        BarcodeScanner.MWBsetActiveCodes(
                this.scanFormats
                );
        
        // TODO: Verify that this sets all the rectangles to be the same through QA
        BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_ALL,    this.scanArea);
        
        // set decoder effort level (1 - 5)
        // for live scanning scenarios, a setting between 1 to 3 will suffice
		// levels 4 and 5 are typically reserved for batch scanning 
        BarcodeScanner.MWBsetLevel(this.effortLevel);        

        CameraManager.init(getApplication());
        
//        id = MdiUtils.getResourceIdByName(this.getPackageName(), "id", "result_view");
//        resultView = findViewById(id);
//
//        id = MdiUtils.getResourceIdByName(this.getPackageName(), "id", "status_view");
//        statusView = findViewById(id);
        
        handler = null;
        lastResult = null;
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        
    }
    public static int getResourceIdByName(String packageName, String className, String name) {
        Class r = null;
        int id = 0;
        try {
            r = Class.forName(packageName + ".R");

            Class[] classes = r.getClasses();
            Class desireClass = null;

            for (int i = 0; i < classes.length; i++) {
                if (classes[i].getName().split("\\$")[1].equals(className)) {
                    desireClass = classes[i];

                    break;
                }
            }

            if (desireClass != null) {
                id = desireClass.getField(name).getInt(desireClass);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return id;
    }
    @SuppressWarnings("deprecation")
	@Override
    protected void onResume()
    {
        super.onResume();

//        resetStatusView();

        SurfaceView surfaceView = new SurfaceView(this.getApplicationContext());
        ViewGroup.LayoutParams btnParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        surfaceView.setLayoutParams(btnParams);
        addContentView(surfaceView, btnParams);

//                (SurfaceView) findViewById(getResourceIdByName(this.getPackageName(), "id", "preview_view"));
//        SurfaceView surfaceView = new SurfaceView(this.getApplicationContext());
        MWOverlay.addOverlay(this, surfaceView);
        
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface)
        {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        }
        else
        {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

      
        int ver = BarcodeScanner.MWBgetLibVersion();
        int v1 = (ver >> 16);
        int v2 = (ver >> 8) & 0xff;
        int v3 = (ver & 0xff);
        String libVersion = "Lib version: " + String.valueOf(v1)+"."+String.valueOf(v2)+"."+String.valueOf(v3);
        //Toast.makeText(this, libVersion, Toast.LENGTH_LONG).show();
        
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        
        MWOverlay.removeOverlay();
        
        if (handler != null)
        {
        		
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();

    }
// Method called on click of alert dialog shown as part of runtime permission check for camera.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    this._cancelScan();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
        {
            if (lastResult != null)
            {
                if (handler != null)
                {
//                	int id = MdiUtils.getResourceIdByName(this.getPackageName(), "id", "restart_preview");
//                    handler.sendEmptyMessage(id);
                }
                return true;
            }
        }
        else
            if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA)
            {
                // Handle these events so they don't launch the Camera app
                return true;
            }
        
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        super.onCreateOptionsMenu(menu);
//         menu.add(0, ABOUT_ID, 0,
//         R.string.menu_about).setIcon(android.R.drawable.ic_menu_info_details);
//        return true;
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        switch (item.getItemId())
//        {
//        case ABOUT_ID:
//            PackageInfo info = null;
//			try {
//				info = getPackageManager().getPackageInfo(getPackageName(), 0);
//			} catch (NameNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            this.versionName = info.versionName;
//           
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(getString(R.string.title_about));
//            builder.setMessage(getString(R.string.msg_about));
//            builder.setIcon(R.drawable.launcher_icon);
//            builder.setPositiveButton(R.string.button_open_license, new OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface arg0, int arg1) {
//					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.license_url)));
//					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//					startActivity(intent);
//					finish();
//					
//				}
//			});
//            builder.setNeutralButton(R.string.button_open_mobi, new OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.mobi_url)));
//					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//					startActivity(intent);
//					finish();
//					
//				}
//			});
//            builder.setNegativeButton(R.string.button_cancel, null);
//            builder.show();
//            break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onConfigurationChanged(Configuration config)
    {
       
	    	Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
	    	int rotation = display.getRotation();
	    	
	    	CameraManager.get().updateCameraOrientation(rotation);
	    	
	    super.onConfigurationChanged(config);
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        if (!hasSurface)
        {
            hasSurface = true;
            initCamera(holder);
            
            // Now that the camera is initialized, we can check if there is a Light available.
            // Enable the togglebutton for the light if it is available.
//            int id = MdiUtils.getResourceIdByName(this.getPackageName(), "id", "toggleButton1");
//
//            final ToggleButton lightButton = (ToggleButton)findViewById(id);
//            if(lightButton != null) {
//            	if(CameraManager.get().isTorchAvailable()) {
//            		lightButton.setVisibility(View.VISIBLE);
//            	}
//            }
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        hasSurface = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    		// from phonegap example manatee
    		if (!hasSurface)
        {
            hasSurface = true;
            initCamera(holder);
        }
    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     * 
     * @param rawResult
     *            The contents of the barcode.
     */

    public void handleDecode(byte[] rawResult)
    {
//    		MdiUtils.log("handleDecode");
    	
        inactivityTimer.onActivity();
        lastResult = rawResult;
        statusView.setVisibility(View.GONE);
        resultView.setVisibility(View.VISIBLE);
        
//        int id = MdiUtils.getResourceIdByName(this.getPackageName(), "id", "format_text_view");
//
//        TextView formatTextView = (TextView) findViewById(id);
//        formatTextView.setVisibility(View.VISIBLE);
//
//        id = MdiUtils.getResourceIdByName(this.getPackageName(), "string", "msg_default_format");
//        formatTextView.setText(getString(id));
//
//        id = MdiUtils.getResourceIdByName(this.getPackageName(), "id", "contents_text_view");
//
//        TextView contentsTextView = (TextView) findViewById(id);
        String s = "";
        
        try {
			s = new String(rawResult, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			
			s = "";
			for (int i = 0; i < rawResult.length; i++)
		            s = s + (char) rawResult[i];	
			e.printStackTrace();
		}

//        contentsTextView.setText(s);
        
        
        int bcType = BarcodeScanner.MWBgetLastType();
        String typeName="";
        switch (bcType) {
            case BarcodeScanner.FOUND_25_INTERLEAVED: typeName = "CODE_25";break;
            case BarcodeScanner.FOUND_25_STANDARD: typeName = "CODE_25_STANDARD";break;
            case BarcodeScanner.FOUND_128: typeName = "CODE_128";break;
            case BarcodeScanner.FOUND_39: typeName = "CODE_39";break;
            case BarcodeScanner.FOUND_93: typeName = "CODE_93";break;
            case BarcodeScanner.FOUND_AZTEC: typeName = "AZTEC";break;
            case BarcodeScanner.FOUND_DM: typeName = "DATA_MATRIX";break;
            case BarcodeScanner.FOUND_EAN_13: typeName = "EAN_13";break;
            case BarcodeScanner.FOUND_EAN_8: typeName = "EAN_8";break;
            case BarcodeScanner.FOUND_NONE: typeName = "NONE";break;
            case BarcodeScanner.FOUND_RSS_14: typeName = "RSS_14";break;
            case BarcodeScanner.FOUND_RSS_14_STACK: typeName = "RSS_14_STACKED";break;
            case BarcodeScanner.FOUND_RSS_EXP: typeName = "RSS_EXPANDED";break;
            case BarcodeScanner.FOUND_RSS_LIM: typeName = "RSS_LIMITED";break;
            case BarcodeScanner.FOUND_UPC_A: typeName = "UPC_A";break;
            case BarcodeScanner.FOUND_UPC_E: typeName = "UPC_E";break;
            case BarcodeScanner.FOUND_PDF: typeName = "PDF_417";break;
            case BarcodeScanner.FOUND_QR: typeName = "QR";break;
            case BarcodeScanner.FOUND_CODABAR: typeName = "CODA_BAR";break;
            case BarcodeScanner.FOUND_128_GS1: typeName = "CODE_128_GS1";break;
            case BarcodeScanner.FOUND_DOTCODE: typeName = "DOT_CODE";break;
        }
        if (bcType >= 0){
//        		formatTextView.setText("Format: "+typeName);
        }


        if (copyToClipboard)
        {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            //clipboard.setText(s); // deprecated
            // not sure what label to use yet, still new to the code - Rob McConkey 8/18/14
            clipboard.setPrimaryClip(ClipData.newPlainText("label", s));
        }
        
        // return result to caller
        Intent i = new Intent();
        i.putExtra("result", s);
        i.putExtra("format", typeName);
        // we don't use the raw bytes so skipping for now
        //i.putExtra("bytes", rawResult);
        
        setResult(Activity.RESULT_OK, i);
        finish();
    }
    
    @Override
    public void onBackPressed(){
    		
    		this._cancelScan();
    }
    
    private void _cancelScan(){
    	
    		// return result to caller
        Intent i = new Intent();
        i.putExtra("result", "");
        i.putExtra("format", "USER_CANCELLED");
        
        setResult(Activity.RESULT_OK, i);
        
        if (handler != null)
        {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
        
        finish();
        
    }

    private void _toggleLight(boolean bValue) {
    	
	    	if(CameraManager.get().isTorchAvailable()) {
	    		CameraManager.get().setTorch(bValue);
	    	}
    	
    }

    private void initCamera(SurfaceHolder surfaceHolder)
    {
        try
        {
            // Select desired camera resoloution. Not all devices supports all resolutions, closest available will be chosen
            // If not selected, closest match to screen resolution will be chosen
            // High resolutions will slow down scanning proccess on slower devices
        	
	        	if (PDF_OPTIMIZED){
	        		CameraManager.setDesiredPreviewSize(1280, 720);
	        	} else {
	        		CameraManager.setDesiredPreviewSize(800, 480);	
	        	}
        	
            
            CameraManager.get().openDriver(surfaceHolder, (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT));
        }
        catch (IOException ioe)
        {
//            displayFrameworkBugMessageAndExit();
            return;
        }
        catch (RuntimeException e)
        {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
//            displayFrameworkBugMessageAndExit();
            return;
        }
        if (handler == null)
        {
            handler = new ActivityCaptureHandler(this);
        }
    }

//    private void displayFrameworkBugMessageAndExit()
//    {
//    		int id_app_name = MdiUtils.getResourceIdByName(this.getPackageName(), "string", "app_name");
//        int id_fwk_bug = MdiUtils.getResourceIdByName(this.getPackageName(), "string", "msg_camera_framework_bug");
//        int id_btn_ok = MdiUtils.getResourceIdByName(this.getPackageName(), "string", "button_ok");
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(getString(id_app_name));
//        builder.setMessage(getString(id_fwk_bug));
//        builder.setPositiveButton(id_btn_ok, new DialogInterface.OnClickListener()
//        {
//            public void onClick(DialogInterface dialogInterface, int i)
//            {
//                finish();
//            }
//        });
//        builder.show();
//    }

//    private void resetStatusView()
//    {
//        resultView.setVisibility(View.GONE);
//        statusView.setVisibility(View.VISIBLE);
//
//        int id = MdiUtils.getResourceIdByName(this.getPackageName(), "color", "status_view");
//        statusView.setBackgroundColor(getResources().getColor(id));
//
//        id = MdiUtils.getResourceIdByName(this.getPackageName(), "id", "status_text_view");
//
//        TextView textView = (TextView) findViewById(id);
//
//        int stringId =  MdiUtils.getResourceIdByName(this.getPackageName(), "string", "msg_default_status");
//        textView.setText(stringId);
//        lastResult = null;
//    }

}
