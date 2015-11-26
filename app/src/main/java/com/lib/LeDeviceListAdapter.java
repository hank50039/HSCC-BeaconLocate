package com.lib;

import java.util.ArrayList;
import java.util.List;

import com.db.DBDataManager;
import com.db.MarkModel;
import com.student.bluetooth.ui.R;
import com.lib.iBeaconClass.iBeacon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LeDeviceListAdapter extends BaseAdapter {

    // Adapter for holding devices found through scanning.

    private ArrayList<iBeacon> mLeDevices;
    private LayoutInflater mInflator;
    private Activity mContext;
  //  private List<MarkModel> allMarklist = null;

    public LeDeviceListAdapter(Activity c) {
        super();
        mContext = c;
        mLeDevices = new ArrayList<iBeacon>();
        mInflator = mContext.getLayoutInflater();


    }

    public void addDevice(iBeacon device) {
        if (device == null)
            return;


        DBDataManager dbManager = new DBDataManager(mContext);
        List<MarkModel> allMarklist = dbManager.get_mark_list();

        for (int i = 0; i < mLeDevices.size(); i++) {
            String btAddress = mLeDevices.get(i).bluetoothAddress;
            if (btAddress.equals(device.bluetoothAddress)) {



                for (MarkModel mm : allMarklist) {
                    if (device.bluetoothAddress.equals(mm.address)) {
                        Log.d("beacon", "======mark :" + mm.mark);
                        device.mark = mm.mark;
                    }
                }


                mLeDevices.add(i + 1, device);
                mLeDevices.remove(i);
                return;
            }
        }

        for (MarkModel mm : allMarklist) {
            if (device.bluetoothAddress.equals(mm.address)) {
                Log.d("beacon", "======mark :" + mm.mark);
                device.mark = mm.mark;
            }
        }
        mLeDevices.add(device);

    }

    public iBeacon getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    public void reflashDataShow() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.listitem_device, null);
            viewHolder = new ViewHolder();
            viewHolder.device_txPower_train = (TextView) view.findViewById(R.id.device_txPower_train);
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceUUID = (TextView) view.findViewById(R.id.device_beacon_uuid);
            viewHolder.deviceMajor_Minor = (TextView) view.findViewById(R.id.device_major_minor);
            viewHolder.devicetxPower_RSSI = (TextView) view.findViewById(R.id.device_txPower_rssi);
            viewHolder.device_btn = (Button) view.findViewById(R.id.device_btn);

            viewHolder.device_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final iBeacon device = mLeDevices.get(view.getId());
                    final EditText et = new EditText(mContext);
                    et.setInputType(InputType.TYPE_CLASS_NUMBER);
                    new AlertDialog.Builder(mContext)
                            .setTitle("請輸入 數字 1-6")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(et)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    DBDataManager dbManager = new DBDataManager(mContext);
                                    dbManager.apppend_location_mark(et.getText().toString(), device.bluetoothAddress);


                                    dbManager.list_table_data3(Consts.TABLE_mark);

                                    reflashDataShow();


                                }
                            }).setNegativeButton("取消", null)
                            .setCancelable(false).show();


                }
            });

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.device_btn.setId(i);

        iBeacon device = mLeDevices.get(i);
        final String deviceName = device.name;
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(R.string.unknown_device);

        viewHolder.device_txPower_train.setText(device.mark);
        viewHolder.deviceAddress.setText(device.bluetoothAddress);
        viewHolder.deviceUUID.setText(device.proximityUuid);
        viewHolder.deviceMajor_Minor.setText("major:" + device.major + ",minor:" + device.minor);
        viewHolder.devicetxPower_RSSI.setText("txPower:" + device.txPower + ",rssi:" + device.rssi);
        return view;
    }

    class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceUUID;
        TextView deviceMajor_Minor;
        TextView devicetxPower_RSSI;
        TextView device_txPower_train;
        Button device_btn;
    }
}
