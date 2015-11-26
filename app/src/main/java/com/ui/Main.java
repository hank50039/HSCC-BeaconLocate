package com.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.student.bluetooth.ui.R;

public class Main extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        TextView activity_main_button_locate = (TextView) this
                .findViewById(R.id.activity_main_button_locate);
        activity_main_button_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean okGoNext = true;
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    Toast.makeText(Main.this, R.string.title_devices, Toast.LENGTH_SHORT).show();
                    okGoNext = false;
                }
                final BluetoothManager bluetoothManager =
                        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(Main.this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
                    okGoNext = false;
                }


                if (okGoNext) {
                    Intent intent = new Intent(Main.this,
                            ScanMyPositionActivity.class);


                    startActivity(intent);

               //     finish();


                }

            }
        });



        TextView activity_main_button_test = (TextView) this
                .findViewById(R.id.activity_main_button_test);
        activity_main_button_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean okGoNext = true;
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    Toast.makeText(Main.this, R.string.title_devices, Toast.LENGTH_SHORT).show();
                    okGoNext = false;
                }
                final BluetoothManager bluetoothManager =
                        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(Main.this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
                    okGoNext = false;
                }


                if (okGoNext) {

                    Intent intent = new Intent(Main.this,
                            TestPositionActivity.class);

                    startActivity(intent);

                //    finish();


                }

            }
        });



        TextView activity_main_button_mark = (TextView) this
                .findViewById(R.id.activity_main_button_mark);
        activity_main_button_mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean okGoNext = true;
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    Toast.makeText(Main.this, R.string.title_devices, Toast.LENGTH_SHORT).show();
                    okGoNext = false;
                }
                final BluetoothManager bluetoothManager =
                        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(Main.this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
                    okGoNext = false;
                }


                if (okGoNext) {

                    Intent intent = new Intent(Main.this,
                            MarkSignalActivity.class);

                    startActivity(intent);

                 //   finish();


                }

            }
        });





    }


}
