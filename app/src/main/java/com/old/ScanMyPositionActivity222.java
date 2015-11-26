/*
 * Copyright (C) 2013 The Android Open Source Project
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
 */

package com.old;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.db.DBDataManager;
import com.db.LocationValueModel;
import com.db.MarkModel;
import com.db.TestValueModel;
import com.lib.iBeaconClass;
import com.student.bluetooth.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class ScanMyPositionActivity222 extends Activity {
    private Timer timer_cnt_wait_time;
    private BluetoothAdapter mBluetoothAdapter;
    private static final long SCAN_PERIOD = 6000;
    private CustomView1 myDrawScreen = null;


    //        定位6個Beacon， 地址－編號－特徵值－記錄位置－記錄3組即時強度
    //  String[][] L_beacon = new String[6][2];

    private List<MarkModel> allMarklist; //beacon 的編號對應表
    private List<TestValueModel> allNowValueList = new ArrayList<TestValueModel>();
    private List<LocationValueModel> allMarkTestValueList = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanmyposition);


        //取得己經標記的beacon
        DBDataManager dbManager = new DBDataManager(this);
        allMarklist = dbManager.get_mark_list();

        for (int i = 1; i < 7; i++) {
            for (MarkModel mm : allMarklist) {
                if (mm.mark.equals(i + "")) {
                    TestValueModel tt = new TestValueModel();
                    tt.mark = i + "";
                    tt.address = mm.address;
                    tt.vv = 0;
                    allNowValueList.add(tt);
                    break;
                }
            }
        }

        Log.d("ibeacon", "  --->取得己經標記的beacon ");

        for (TestValueModel tt : allNowValueList) {
            Log.d("ibeacon", "  ---> " + tt.mark + " =  address:" + tt.address);
        }


        //   DBDataManager dbManager = new DBDataManager(TestPositionActivity.this);
        allMarkTestValueList = dbManager.get_location_value();

        String llv = "---------標記點 平均值－－ 值列 1-6-----------------------\n";

        for (LocationValueModel ll : allMarkTestValueList) {
            llv = llv + " 標記：> " + ll.no + " 值列：" + ll.value[0] + "," + ll.value[1] + "," + ll.value[2] + "," + ll.value[3] + "," + ll.value[4] + "," + ll.value[5] + "\n";
        }


        myDrawScreen = new CustomView1(ScanMyPositionActivity222.this, 200, 200);
        setContentView(myDrawScreen);


        myDrawScreen.drawCircle(300, 500);


        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mBluetoothAdapter.enable();

    }


    private void goCheckMyPositon() {
        int[] nowV = new int[6];  //當前6個點的值

        for (TestValueModel tesV : allNowValueList) {

            int in = Integer.parseInt(tesV.mark) - 1;
            if (in < 6 && in > -1) {
                nowV[in] = tesV.vv;
            }

        }

        double[] allDis = new double[100];
        for (double dd : allDis) {
            dd = -1;
        }
        //和全部位置比對,－－歐幾里德距離公式
        // tesV 資料庫中，全部點陣的 6個值
        for (LocationValueModel tesV : allMarkTestValueList) {
            //6個點，差值平方和 再開根號，記錄在  allDis

            double te1 = 0;
            for (int i = 0; i < 6; i++) {
                te1 = te1 + (tesV.value[i] - nowV[i]) * (tesV.value[i] - nowV[i]);
            }

            int in = Integer.parseInt(tesV.no);
            allDis[in] = Math.sqrt(te1);

        }


        //找出最值的點
        double minV = 100;
        int in = -1;
        for (int i = 0; i < 100; i++) {
            if (allDis[i] < minV) {
                minV = allDis[i];
                in = i;
            }
        }

        myDrawScreen.drawText("====答案＝＝＝＝＝=>" + in);
        Log.e("beacon", "====答案＝＝＝＝＝=>" + in);
        Log.e("beacon", "====答案＝＝＝＝＝=>" + in);
        Log.e("beacon", "====答案＝＝＝＝＝=>" + in);
        Log.e("beacon", "====答案＝＝＝＝＝=>" + in);

        myDrawScreen.drawCircle(in, 500);


    }

    private void newChatDownloadDataTimer() {
        timer_cnt_wait_time = new Timer();
        timer_cnt_wait_time.schedule(new TimerTask() {
            @Override
            public void run() {
                mBluetoothAdapter.startLeScan(mLeScanCallback);

            }
        }, SCAN_PERIOD, SCAN_PERIOD);//
    }

    class CustomView1 extends View {

        Paint paint;
        int screenPixWidth = 0;
        int screenPixHeight = 0;
        int startx = 50;
        int starty = 50;
        int perL = 60;

        int cirX = 100;
        int cirY = 100;
        String showText = "";

        public CustomView1(Context context, int cx, int cy) {
            super(context);

            cirX = cx;
            cirY = cy;

            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(3);


            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);

            screenPixWidth = dm.widthPixels - startx - 100;
            screenPixHeight = dm.heightPixels;


            perL = (int) ((float) screenPixWidth / 6);


        }

        public void drawCircle(int cx, int cy) {
            cirX = cx;
            cirY = cy;
        }

        public void drawText(String tt) {
            showText = tt;

        }


        @Override
        protected void onDraw(Canvas canvas) {

            for (int i = 0; i < 7; i++) {
                canvas.drawLine(startx + i * perL, starty, startx + i * perL, starty + perL * 9, paint);
            }


            for (int i = 0; i < 10; i++) {
                canvas.drawLine(startx, starty + i * perL, startx + perL * 6, starty + i * perL, paint);
            }

            if (cirX > 0 && cirY > 0) {
                canvas.drawCircle(cirX, cirY, perL / 2, paint);
            }


            if(showText.length() >0){

                canvas.drawText(showText, 60, 60, paint);
            }

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        newChatDownloadDataTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer_cnt_wait_time.cancel();
    }


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                    final iBeaconClass.iBeacon ibeacon = iBeaconClass.fromScanData(device, rssi, scanRecord);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("ibeacon", "====> " + ibeacon.bluetoothAddress + "---   " + ibeacon.rssi);
                            Log.d("ibeacon", "====>------------------------------------------------------- ");


                            for (TestValueModel tt : allNowValueList) {

                                if (tt.address.equals(ibeacon.bluetoothAddress)) {
                                    tt.vv = ibeacon.rssi;
                                    break;
                                }
                                //  Log.d("ibeacon", "====> " +L_beacon[i][1] + ": " +L_beacon[i][4]+","+L_beacon[i][5]+","+L_beacon[i][6]);
                            }

                            goCheckMyPositon();

                        }
                    });
                }
            };


}