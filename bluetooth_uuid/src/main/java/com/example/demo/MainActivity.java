package com.example.demo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List <BluetoothDevice>   mLeDevices = new ArrayList<BluetoothDevice>();
    private BluetoothAdapter bluetoothAdapter;
    private LeDeviceListAdapter listAdapter;
    private TextView textViewState;
    BluetoothGattCharacteristic characteristic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.list_item );

        BluetoothManager bluetoothManager= (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        listAdapter = new LeDeviceListAdapter();
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(Lister);

        textViewState = ((TextView) findViewById(R.id.State));
     }
    //1  查找低功耗蓝牙设备
    public void ScanActivity(View view) {
        mLeDevices.clear();
         bluetoothAdapter.startLeScan(callback);
    }


    private String TAG="MainActivity";
    //2   蓝牙接口的回调。 扫描数据  然后的回调，
    private BluetoothAdapter.LeScanCallback callback=new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] values) {
            if (!mLeDevices.contains(bluetoothDevice)){
                mLeDevices.add(bluetoothDevice);
            }
            //广播是在异步线程里面获取的数据 ，  所以需要在主线程更新界面，也可以使用hander   方便起见。直接RUNONUI
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listAdapter.notifyDataSetChanged();
                }
            });
            Log.e(TAG, "onLeScan: +"+bluetoothDevice.getAddress()+bluetoothDevice.getName() );
        }
    };
    private BluetoothGatt bluetoothGatt;
    //listView 的点击方法，。。
    private AdapterView.OnItemClickListener Lister=new AdapterView.OnItemClickListener() {


        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice item = listAdapter.getItem(i);
            // 3  连接到点击的蓝牙设备中  里面有个回调方法，  注意这个对象是操作所有事情的根本 ，
            bluetoothGatt = item.connectGatt(MainActivity.this, false, bluetoothDeviceCallBack);
        }
    };
    //4.实现回调的方法， 并不需全部复写，但是为了严谨起见，全部写上去了，。
    private android.bluetooth.BluetoothGattCallback bluetoothDeviceCallBack =new BluetoothGattCallback() {


        private BluetoothGattService bluetoothGattService;

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override  // 5  表示状态已经改变了。  然后我们可以进行下一步操作了。
        public void onConnectionStateChange(BluetoothGatt gatt, int status, final int newState) {
             //参数 gatt 表示操作数据的对象
            //  status 表示连接的一个校验码
            //newState  连接状态的改变，  主要就是操作这个数， 如果返回的数据不同的话  那么我们可以进行不同的操作。
            //因为是异步的调用，所以需要在主线程进行更新界面，。
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (newState){
                        case BluetoothGatt.STATE_CONNECTED:
                            //已连接
                            textViewState.setText("已连接");
                            // 6在连接之后进行数据的交互   这个也是回调给你的， 。 寻找服务 在下面的回调里面进行的，。
                            bluetoothGatt.discoverServices();
                            break;
                        case BluetoothGatt.STATE_CONNECTING:
                            //正在连接
                            textViewState.setText("正在连接");
                            break;
                        case BluetoothGatt.STATE_DISCONNECTED:
                            //已断开
                            textViewState.setText("已断开");
                            break;
                        case BluetoothGatt.STATE_DISCONNECTING:
                            //正在断开
                            textViewState.setText("正在断开");
                            break;
                    }
                }
            });

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            // 7 这里需要进行判断   如果寻找成功了的话我们才进行操作。不然的话就不能进行操作
            if (status == BluetoothGatt .GATT_SUCCESS  ){
                //获取到所有的GATT服务，
                List<BluetoothGattService> gattServices = gatt.getServices();
                //进行打印下。  获取每个设备的UUID   只有获取了UUID才能点对点进行数据的传输
                 for (int i =0 ; i< gattServices.size() ;i ++){
                     Log.d("ONE",   "gattServices"+gattServices.get(i).getUuid().toString());
                     //下面进行获取里面的特征值。  核心值， 来进行写入到那个里面去。
                     bluetoothGattService = gattServices.get(i);

                     List<BluetoothGattCharacteristic>   characteristics = bluetoothGattService.getCharacteristics();
                     for (int i1 = 0; i1 < characteristics.size(); i1++) {
                         Log.d("ONE", "characteristics"+characteristics.get(i1).getUuid().toString());
                            //这一步就是数据的写入了， 如果这个UUID和里面的UUID一样的话那么我就直接赋值来进行控制数据的输入。
                         //其实所有的通信都是和特征值打交道的。
                         if ("0000fec7-0000-1000-8000-00805f9b34fb" ==characteristics.get(i1).getUuid().toString()){
                             characteristic = characteristics.get(i1) ;
                         }
                     }
                 }
            }
        }
    };
//发送的命令。
    public void Chicked(View view) {
        //这个就是把数据全部写进去了。
        if (characteristic!=null &&bluetoothGatt!=null){
            characteristic.setValue("xXXXX");
            bluetoothGatt.writeCharacteristic(characteristic);
        }
    }


    // 2.2 显示数据的 ，。普通的一个Adapter
    public class LeDeviceListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mLeDevices ==null ?  0 :mLeDevices.size();
        }

        @Override
        public BluetoothDevice getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            {
                ViewHolder viewHolder;
                // General ListView optimization code.
                if (view == null) {
                    view = View.inflate(MainActivity.this,R.layout.listitem_device,null );
                    viewHolder = new ViewHolder();
                    viewHolder.deviceAddress = (TextView) view
                            .findViewById(R.id.device_address);
                    viewHolder.deviceName = (TextView) view
                            .findViewById(R.id.device_name);
                    view.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) view.getTag();
                }

                BluetoothDevice device = mLeDevices.get(i);
                final String deviceName = device.getName();
                if (deviceName != null && deviceName.length() > 0)
                    viewHolder.deviceName.setText(deviceName);
                else
                    viewHolder.deviceName.setText("unknown_device");
                viewHolder.deviceAddress.setText(device.getAddress());
                return view;
            }
        }


    }
    class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
