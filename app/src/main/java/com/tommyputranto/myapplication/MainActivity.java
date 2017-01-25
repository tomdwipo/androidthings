package com.tommyputranto.myapplication;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.sql.Time;
import java.util.List;

public class MainActivity extends Activity {

   private static final String TAG = MainActivity.class.getSimpleName();
       private static final int INTERVAL_BETWEEN_BLINKS_MS = 2000;

       private Handler mHandler = new Handler();
       private Gpio mLedGpio;

int i = 1;
    boolean ledStatus= true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting BlinkActivity");
        PeripheralManagerService managerService = new PeripheralManagerService();
        try {
            mLedGpio = managerService.openGpio(BoardDefaults.getGPIOForLED());
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mLedGpio.setActiveType(Gpio.ACTIVE_HIGH);
            mLedGpio.setValue(ledStatus);
            mHandler.post(mBlinkRunnable);
            Log.e(TAG, mLedGpio.toString()+ " "+ mLedGpio.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove pending blink Runnable from the handler.
        mHandler.removeCallbacks(mBlinkRunnable);
        // Close the Gpio pin.
        Log.i(TAG, "Closing LED GPIO pin");
        try {
            mLedGpio.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            mLedGpio = null;
        }
    }

    Runnable test = new Runnable() {
        @Override
        public void run() {
            try {
                mLedGpio.setValue(true);
                mHandler.postDelayed(test, INTERVAL_BETWEEN_BLINKS_MS);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };
    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLedGpio == null) {
                return;
            }
            try {
                mLedGpio.setValue(!ledStatus);
                ledStatus = !ledStatus;
                Log.e(TAG, ledStatus+" ");
                mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };
}