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
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
public class TestPositionActivity extends Activity {
    private Timer timer_cnt_wait_time;
    private BluetoothAdapter mBluetoothAdapter;
    private static final long SCAN_PERIOD = 1000;
    private int now_test_tag_Location = 0;

    private TableLayout tabletTest;
    private ScrollView activity_test_location_scroll;
    private TextView activity_test_location_testvalue;
    private String testValueLog_all = "";

    private List<MarkModel> allMarklist; //beacon 的編號對應表
    private List<TestValueModel> allTestValueList = new ArrayList<TestValueModel>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_location);


        DBDataManager dbManager = new DBDataManager(this);
        allMarklist = dbManager.get_mark_list();




        for (int i = 1; i < Consts.NUM_beacon +1; i++) {
            for (MarkModel mm : allMarklist) {
                if (mm.mark.equals(i + "")) {
                    TestValueModel tt = new TestValueModel();
                    tt.mark = i + "";
                    tt.address = mm.address;
                    tt.vv = 0;
                    allTestValueList.add(tt);
                    break;
                }
            }
        }



        activity_test_location_scroll = (ScrollView) findViewById(R.id.activity_test_location_scroll);
        activity_test_location_testvalue = (TextView) findViewById(R.id.activity_test_location_testvalue);
        tabletTest = (TableLayout) findViewById(R.id.dictTable);
        appendTableRow(tabletTest);


        Button activity_main_button_locate_stop = (Button) findViewById(R.id.activity_test_location_stop);
        activity_main_button_locate_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now_test_tag_Location = 0;
                tabletTest.setVisibility(View.VISIBLE);
                activity_test_location_scroll.setVisibility(View.GONE);

            }
        });
        Button activity_test_location_show = (Button) findViewById(R.id.activity_test_location_show);
        activity_test_location_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletTest.setVisibility(View.GONE);
                activity_test_location_scroll.setVisibility(View.VISIBLE);


                activity_test_location_testvalue.setText("");

                DBDataManager dbManager = new DBDataManager(TestPositionActivity.this);
                List<LocationValueModel> alllist = dbManager.get_location_value();

                String llv = "---------點 －－ 值列 1-6-----------------------\n";

                for (LocationValueModel ll : alllist) {
                    String vv = "";
                    for(int i=0;i<ll.value.length;i++){
                        vv= vv+ll.value[i] + ",";

                    }

                    llv = llv + " 標記：> " + ll.no + " 值列：" +vv + "\n";
                }


                activity_test_location_testvalue.setText(llv);


            }
        });


        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mBluetoothAdapter.enable();

    }


    private void appendTableRow(final TableLayout table) {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int btnPixWidth = (dm.widthPixels - 100) / Consts.TABLE_col_num;
        int btnPixHeight = (dm.heightPixels - 400) / Consts.TABLE_row_num;

        int orders = 1;


        for (int i = 1; i < Consts.TABLE_row_num+1; i++) {
            TableRow row = new TableRow(this);
            for (int j = 1; j < Consts.TABLE_col_num+1; j++) {

                row.setId(0);
                Button oper = new Button(this);
                oper.setText("" + orders);
                oper.setTag(orders);
                oper.setTextSize(14);

                oper.setLayoutParams(new TableRow.LayoutParams(
                        btnPixWidth,
                        btnPixHeight));


                //  oper.setWidth(100);
                orders++;
                oper.setPadding(3, 3, 3, 3);
                //   order.setWidth(60);
                //   oper.setGravity(Gravity.CENTER);
                oper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (TestValueModel tt : allTestValueList) {
                            tt.vv = 0;
                        }

                        String tt = v.getTag() + "";
                        now_test_tag_Location = Integer.parseInt(tt);
                        Log.e("beacon", "=＝ 開始測試 =" + now_test_tag_Location);
                        Toast.makeText(TestPositionActivity.this, " 開始測試,每個標記點記錄10組後自動停止.", Toast.LENGTH_SHORT).show();
                        testValueLog_all = "";
                        tabletTest.setVisibility(View.GONE);
                        activity_test_location_scroll.setVisibility(View.VISIBLE);


                    }
                });

                row.addView(oper);


            }

            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            //  table.addView(row);
            table.addView(row, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
        }


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


    @Override
    protected void onResume() {
        super.onResume();
        newChatDownloadDataTimer();

        Toast.makeText(this, "目前有 " + allTestValueList.size() + " 個標記點", Toast.LENGTH_SHORT).show();
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
    當按下Test時   now_test_tag_Locationo值大於0 也就代表一個位置

     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {


                    final int pos_width_num = Consts.TABLE_col_num+1;//直數量  7
                    final int pos_height_num = Consts.TABLE_row_num+1;//橫數量  10

                    final iBeaconClass.iBeacon ibeacon = iBeaconClass.fromScanData(device, rssi, scanRecord);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String testValueLog = "=標記=> " + ibeacon.bluetoothAddress + " －值： " + ibeacon.rssi;
                            //    Log.d("ibeacon", testValueLog);


                            if (now_test_tag_Location > 0) {
                                testValueLog_all = testValueLog_all + testValueLog + "\n";

                                activity_test_location_testvalue.setText(testValueLog_all);

                                for (TestValueModel tt : allTestValueList) {
                                    if (tt.address.equals(ibeacon.bluetoothAddress)) {
                                        if (tt.vv < pos_height_num) {
                                            tt.value[tt.vv] = ibeacon.rssi;  //保存取到的beacono值
                                            tt.vv++;
                                            break;
                                        }

                                    }
                                }


                                //檢查是否完成每一個beacon 都記錄到10筆的 beacono值 資料
                                boolean dd = true;
                                for (TestValueModel tt : allTestValueList) {
                                    dd = dd && tt.vv > 9;
                                }


                                if (dd) {
                                    for (TestValueModel tt : allTestValueList) {
                                        float ff = 0;
                                        for (int i = 0; i < pos_height_num; i++) {
                                            ff = ff + (float) tt.value[i];
                                        }
                                        tt.value[0] = (int) (ff / pos_height_num);
                                    }
                                    int[] tv = new int[pos_width_num];
                                    Toast.makeText(TestPositionActivity.this, " 完成 ", Toast.LENGTH_SHORT).show();
                                    testValueLog_all = testValueLog_all + "－－－－－－－－－－－－－－－－－－－－\n";
                                    testValueLog_all = testValueLog_all + "－－觀測點記錄：" + now_test_tag_Location + "－－－－\n";
                                    testValueLog_all = testValueLog_all + "－－－－－－平均值－－－－－－\n";
                                    for (TestValueModel tt : allTestValueList) {
                                        float ff = 0;
                                        for (int i = 0; i < pos_height_num; i++) {
                                            ff = ff + (float) tt.value[i];
                                        }
                                        tt.value[0] = (int) (ff / pos_height_num);
                                        testValueLog_all = testValueLog_all + "－－> " + tt.address + "  測值： " + tt.value[0] + "\n";
                                        int mm = Integer.parseInt(tt.mark);
                                        if (mm > 0 && mm < pos_width_num)
                                            tv[mm] = tt.value[0];
                                    }

                                    activity_test_location_testvalue.setText(testValueLog_all);


                                    DBDataManager dbManager = new DBDataManager(TestPositionActivity.this);
                                    /*
                                      把這個位置的平均值記錄到 Database
                                      在這裡要記錄資料庫
                                     */
                                    dbManager.apppend_location_value(now_test_tag_Location + "", tv[1], tv[2], tv[3], tv[4], tv[5], tv[6]);


                                 //   Log.d("ibeacon", "保存：" + now_test_tag_Location + " － 值：" + tv[1] + "," + tv[2] + "," + tv[3] + "," + tv[4] + "," + tv[5] + "," + tv[6]);

                                    now_test_tag_Location = 0;

                                }

                            }


                        }
                    });
                }
            };


}