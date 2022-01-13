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

import com.manateeworks.camera.CameraManager;
//import com.mdi.mdimobilelib.MdiUtils;

import android.os.Handler;
import android.os.Message;

/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 */
public final class ActivityCaptureHandler extends Handler
{

    private final ActivityCapture activity;
    private final DecodeThread decodeThread;
    private State state;

    private enum State
    {
        PREVIEW, SUCCESS, DONE
    }

    ActivityCaptureHandler(ActivityCapture activity)
    {
        this.activity = activity;
        decodeThread = new DecodeThread(activity);
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message)
    {
//	    	int id_auto_focus =  MdiUtils.getResourceIdByName(this.activity.getPackageName(), "id", "auto_focus");
//	    	int id_restart_preview =  MdiUtils.getResourceIdByName(this.activity.getPackageName(), "id", "restart_preview");
//	    	int id_decode_failed =  MdiUtils.getResourceIdByName(this.activity.getPackageName(), "id", "decode_failed");
//	    	int id_decode_succeeded =  MdiUtils.getResourceIdByName(this.activity.getPackageName(), "id", "decode_succeeded");
//	    	int id_decode =  MdiUtils.getResourceIdByName(this.activity.getPackageName(), "id", "decode");
//
//	    	if (message.what == id_auto_focus) {
//	    		// When one auto focus pass finishes, start another. This is the
//            // closest thing to
//            // continuous AF. It does seem to hunt a bit, but I'm not sure what
//            // else to do.
//            if (state == State.PREVIEW)
//            {
//                CameraManager.get().requestAutoFocus(this, id_auto_focus);
//            }
//
//		} else if (message.what == id_restart_preview) {
//			restartPreviewAndDecode();
//		} else if (message.what == id_decode_succeeded) {
//			state = State.SUCCESS;
//            activity.handleDecode((byte[]) message.obj);
//		} else if (message.what == id_decode_failed) {
//			// We're decoding as fast as possible, so when one decode fails,
//            // start another.
//            state = State.PREVIEW;
//            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), id_decode);
//		}
    
    
    }

    public void quitSynchronously()
    {
//        state = State.DONE;
//        CameraManager.get().stopPreview();
//        Message quit = Message.obtain(decodeThread.getHandler(), MdiUtils.getResourceIdByName(this.activity.getPackageName(), "id", "quit"));
//        quit.sendToTarget();
//        try
//        {
//            decodeThread.join();
//        }
//        catch (InterruptedException e)
//        {
//        }
//
//        // Be absolutely sure we don't send any queued up messages
//        removeMessages(MdiUtils.getResourceIdByName(this.activity.getPackageName(), "id", "decode_succeeded"));
//        removeMessages(MdiUtils.getResourceIdByName(this.activity.getPackageName(), "id", "decode_failed"));
    }

    private void restartPreviewAndDecode()
    {
        if (state == State.SUCCESS)
        {
            state = State.PREVIEW;
//            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), MdiUtils.getResourceIdByName(this.activity.getPackageName(), "id", "decode"));
//            CameraManager.get().requestAutoFocus(this, MdiUtils.getResourceIdByName(this.activity.getPackageName(), "id", "auto_focus"));
        }
    }
    
    public void resetAutoFocus()
    {
//	    	CameraManager.get().camera.cancelAutoFocus();
//	    	int id_auto_focus =  MdiUtils.getResourceIdByName(this.activity.getPackageName(), "id", "auto_focus");
//	    	CameraManager.get().requestAutoFocus(this, id_auto_focus);
	    	
    }


}
