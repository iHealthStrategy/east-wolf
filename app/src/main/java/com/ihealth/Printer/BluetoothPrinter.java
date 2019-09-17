package com.ihealth.Printer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.ihealth.bean.AppointmentsBean;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;


import net.xprinter.service.XprinterService;
import net.xprinter.xpinterface.IMyBinder;
import net.xprinter.xpinterface.UiExecute;


import static android.content.Context.BIND_AUTO_CREATE;
import static com.ihealth.Printer.BluetoothPrinterStatus.CONNECTED;
import static com.ihealth.Printer.BluetoothPrinterStatus.CONNECTING;
import static com.ihealth.Printer.BluetoothPrinterStatus.CONNECT_FAIL;
import static com.ihealth.Printer.BluetoothPrinterStatus.DISCONNECTED;
import static com.ihealth.Printer.BluetoothPrinterStatus.OPEN;
import static com.ihealth.Printer.BluetoothPrinterStatus.SEARCHING;
import static com.ihealth.Printer.BluetoothPrinterStatus.SEARCHING_CANCELED;
import static com.ihealth.Printer.BluetoothPrinterStatus.SEARCHING_STOPPED;
import static net.xprinter.utils.DataForSendToPrinterXp80.initializePrinter;
import static net.xprinter.utils.DataForSendToPrinterXp80.printAndFeed;
import static net.xprinter.utils.DataForSendToPrinterXp80.printerOrderBuzzingAndWarningLight;
import static net.xprinter.utils.DataForSendToPrinterXp80.selectChineseCharModel;
import static net.xprinter.utils.DataForSendToPrinterXp80.selectCutPagerModerAndCutPager;
import static net.xprinter.utils.DataForSendToPrinterXp80.strTobytes;


public class BluetoothPrinter {

    private String MAC = "";
    private String name = "";
    private BluetoothClient mClient;
    private BluetoothPrinterStatus status = DISCONNECTED;
    private PrinterStatusResponse response;
    private IMyBinder binder;
    private AppointmentsBean appointmentsBean;

    private UiExecute exe = new UiExecute() {
        @Override
        public void onsucess() {

        }

        @Override
        public void onfailed() {

        }
    };
    public BluetoothPrinter(Context context, AppointmentsBean appointmentsBean,
                            PrinterStatusResponse response){
        this.response = response;
        this.appointmentsBean = appointmentsBean;
        Intent intent=new Intent(context, XprinterService.class);
        ServiceConnection conn=new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // TODO Auto-generated method stub
                //绑定成功
                binder = (IMyBinder) service;
            }
        };
        context.bindService(intent, conn, BIND_AUTO_CREATE);

            mClient = new BluetoothClient(context);
        mClient.registerBluetoothStateListener(mBluetoothStateListener);

    }

    private void setStatus(BluetoothPrinterStatus status){
        this.status = status;
        BluetoothLog.v("status changed --> "+status.toString());
        this.response.onStatusChange(this.status);
    }
    public void searchAndConnect(){

        if(!mClient.isBluetoothOpened()){
            setStatus(OPEN);
        }
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 5)
                .build();

        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                setStatus(SEARCHING);
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                Beacon beacon = new Beacon(device.scanRecord);
                boolean isPrinter = device.getName().startsWith("Print");
                BluetoothLog.v(String.format("beacon for %s\n%s\n%s",device.getName(), device.getAddress(), beacon.toString()));
                if(isPrinter){

                    name = device.getName();
                    MAC = device.getAddress();
                    mClient.stopSearch();

                    binder.connectBtPort(MAC, new UiExecute() {
                        @Override
                        public void onsucess() {
                            setStatus(CONNECTED);
                            initPrinter();
                            beep();
                      String img =  "    *  ┏┓　    ┏┓\n" +
                                    "    *┏┛┻━━━┛┻┓\n" +
                                    "    *┃　　　　　　　┃ 　\n" +
                                    "    *┃　　　━　　　┃\n" +
                                    "    *┃　┳┛　┗┳　┃\n" +
                                    "    *┃　　　　　　　┃\n" +
                                    "    *┃　　　┻　　　┃\n" +
                                    "    *┃　　　　　　　┃\n" +
                                    "    *┗━┓　　　┏━┛\n" +
                                    "    *　　┃　　　┃神兽保佑\n" +
                                    "    *　　┃　　　┃代码无BUG！\n" +
                                    "    *　　┃　　　┗━━━┓\n" +
                                    "    *　　┃　　　　　　　┣┓\n" +
                                    "    *　　┃　　　　　　　┏┛\n" +
                                    "    *　　┗┓┓┏━┳┓┏┛\n" +
                                    "    *　　　┃┫┫　┃┫┫\n" +
                                    "    *　　　┗┻┛　┗┻┛ ";
//                            String[] arr= img.split("\n");
//                            for(String s: arr){
//                                printText(s);
//
//                            }
                            PrintContentUtils printContentUtils = new PrintContentUtils();
                            String content = printContentUtils.getPringContent(appointmentsBean);
                            String[] arr= content.split("\n");
                            for(String s: arr){
                                printText(s);
                            }
                            cut();
                        }

                        @Override
                        public void onfailed() {
                            setStatus(CONNECT_FAIL);
                        }
                    });

                }

            }

            @Override
            public void onSearchStopped() {
                setStatus(SEARCHING_STOPPED);
            }
            @Override
            public void onSearchCanceled() {
                setStatus(SEARCHING_CANCELED);
            }
        });
    }

    public void initPrinter() {
        binder.write(initializePrinter(), exe);
        binder.write(selectChineseCharModel(), exe);
    }

    public void printText(String text) {

        binder.write(strTobytes(text), exe);
        binder.write(printAndFeed(1),exe);
    }
    public void beep(){
        binder.write(printerOrderBuzzingAndWarningLight(3,1,1), exe);
    }
    public void cut(){
        binder.write(printAndFeed(200),exe);
        binder.write(selectCutPagerModerAndCutPager(66,8), exe);
    }

    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            status= openOrClosed? OPEN:BluetoothPrinterStatus.CLOSED;
            setStatus(status);
        }

    };


    public void destroy(){
        mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
    }
}
