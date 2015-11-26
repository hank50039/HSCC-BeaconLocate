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

package com.ui;

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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.db.DBDataManager;
import com.db.LocationValueModel;
import com.db.MarkModel;
import com.db.TestValueModel;
import com.lib.Consts;
import com.lib.iBeaconClass;
import com.student.bluetooth.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class ScanMyPositionActivity extends Activity {
    private Timer timer_cnt_wait_time;
    private BluetoothAdapter mBluetoothAdapter;
    private static final long SCAN_PERIOD = 3000;
    private int perL = 60;
    private String ttString = "";

    private List<MarkModel> allMarklist; //beacon 的編號對應表
    private List<TestValueModel> allNowValueList = new ArrayList<TestValueModel>();
    private List<LocationValueModel> allMarkTestValueList = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanmyposition);


        //從Database 取得己經標記的beacon
        DBDataManager dbManager = new DBDataManager(this);
        allMarklist = dbManager.get_mark_list();

        for (int i = 1; i < Consts.NUM_beacon +1; i++) {
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

        //從Database中取出己經測過的每一個位置值
        allMarkTestValueList = dbManager.get_location_value();

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


        int[] nowV = new int[Consts.NUM_beacon];  //當前6個點的值

        for (TestValueModel tesV : allNowValueList) {

            int in = Integer.parseInt(tesV.mark) - 1;
            if (in < Consts.NUM_beacon && in > -1) {
                nowV[in] = tesV.vv;
            }

        }

        //全部的觀測點位置
        double[] allDis = new double[Consts.MAX_beacom_Test_Value_Num];
        for (int i = 0; i < Consts.MAX_beacom_Test_Value_Num; i++) {
            allDis[i] = -1;
        }


        //和全部位置比對,－－歐幾里德距離公式
        // test 資料庫中，全部點陣的 6個值
        for (LocationValueModel tesV : allMarkTestValueList) {
            //6個點，差值平方和 再開根號，記錄在  allDis

            double te1 = 0;
            for (int i = 0; i < Consts.NUM_beacon; i++) {
                te1 = te1 + (tesV.value[i] - nowV[i]) * (tesV.value[i] - nowV[i]);
            }

            int in = Integer.parseInt(tesV.no);
            allDis[in] = Math.sqrt(te1);
            Log.e("beacon", " allDis[in]===> " + allDis[in]);
        }


        //找出最接近值的點
        double minV = Consts.MAX_beacom_Test_Value_Num;
        int in = -1;
        for (int i = 0; i < Consts.MAX_beacom_Test_Value_Num; i++) {

            if (allDis[i] < minV && allDis[i] > -1) {
                minV = allDis[i];
                in = i;
            }
        }


        //   setContentView(myDrawScreen);
        //     myDrawScreen.drawText("====答案＝＝＝＝＝=>" + in);

        if (ttString.length() > 200) {
            ttString = "";
        }
        ttString += "  * 位置點-----> " + in + "\n";


        showNewContentView(in);



    }


    private void showNewContentView(int in) {
        CustomView1 myDrawScreen = new CustomView1(ScanMyPositionActivity.this, 200, 300);
        myDrawScreen.drawCircleText((((in - 1) % Consts.NUM_beacon) + 1) * perL, (((in - 1) / Consts.NUM_beacon) + 1) * perL, "====答案＝＝＝＝＝=>" + in);

        setContentView(myDrawScreen);

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
        int cirX = 100;
        int cirY = 100;
        String showText = "";

        int pos_width_num = Consts.TABLE_col_num+1;//直線數量
        int pos_height_num = Consts.TABLE_row_num+1;//橫線數量

        public CustomView1(Context context, int cx, int cy) {
            super(context);

            cirX = cx;
            cirY = cy;

            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(3);
            paint.setTextSize(40);


            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);

            screenPixWidth = dm.widthPixels - startx - 100;
            screenPixHeight = dm.heightPixels;


            perL = (int) ((float) screenPixWidth / Consts.NUM_beacon);


        }

        public void drawCircleText(int cx, int cy, String tt) {
            cirX = cx;
            cirY = cy;
            showText = tt;
        }


        @Override
        protected void onDraw(Canvas canvas) {
            for (int i = 0; i < pos_width_num; i++) {
                canvas.drawLine(startx + i * perL, starty, startx + i * perL, starty + perL * (pos_height_num-1), paint);
            }
            for (int i = 0; i < pos_height_num; i++) {
                canvas.drawLine(startx, starty + i * perL, startx + perL * (pos_width_num-1), starty + i * perL, paint);
            }
            if (cirX > 0 && cirY > 0) {
                canvas.drawCircle(cirX, cirY, perL / 2, paint);
            }
            if (showText.length() > 0) {
                canvas.drawText(showText, 60, 1000, paint);
            }
            super.onDraw(canvas);
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


    /*
      在這裡取beacon的值，當取到值就是去比對位置  goCheckMyPosito();
     */
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