package com.iste776.jpavelw.wanelin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private String fileName;
    private String [] data = new String[12];
    private Button btnStart, btnStop;
    private EditText txtUploadSpeed, txtDownloadSpeed, txtBand;
    private boolean hasChanged = false;
    private int lastDbm = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        this.fileName = intent.getStringExtra(FileNameActivity.FILE_NAME_KEY);

        this.btnStart = (Button) findViewById(R.id.btn_start);
        this.btnStop = (Button) findViewById(R.id.btn_stop);
        this.txtUploadSpeed = (EditText) findViewById(R.id.upload_speed_input);
        this.txtDownloadSpeed = (EditText) findViewById(R.id.download_speed_input);
        this.txtBand = (EditText) findViewById(R.id.band_input);

        //this.btnStart.setEnabled(false);
        this.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 1);
                } else {
                    if(!txtUploadSpeed.getText().toString().trim().equals("") && !txtDownloadSpeed.getText().toString().trim().equals("") && !txtBand.getText().toString().trim().equals("")){
                        //while(!hasChanged){
                            if(!doStuff()){
                                Toast.makeText(getApplicationContext(), "Failed location", Toast.LENGTH_LONG).show();
                            }
                            /*try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }*/
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_empty_speed), Toast.LENGTH_LONG).show();
                    }
                    /*if(!getStrength()){
                        Toast.makeText(getApplicationContext(), "Failed signal", Toast.LENGTH_LONG).show();
                    }
                    saveCSV();*/
                }
            }
        });

        this.btnStop.setEnabled(false);

        /*this.txtUploadSpeed.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if((keyEvent.getAction() == KeyEvent.ACTION_UP) || (keyEvent.getAction() == KeyEvent.KEYCODE_DEL)){
                    if(!txtUploadSpeed.getText().toString().trim().equals("") && !txtDownloadSpeed.getText().toString().trim().equals("")){
                        btnStart.setEnabled(true);
                    } else {
                        btnStart.setEnabled(false);
                    }
                }
                return false;
            }
        });

        this.txtDownloadSpeed.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if((keyEvent.getAction() == KeyEvent.ACTION_UP) || (keyEvent.getAction() == KeyEvent.KEYCODE_DEL)){
                    if (!txtUploadSpeed.getText().toString().trim().equals("") && !txtDownloadSpeed.getText().toString().trim().equals("")) {
                        btnStart.setEnabled(true);
                    } else {
                        btnStart.setEnabled(false);
                    }
                }
                return false;
            }
        });*/

        /*this.txtUploadSpeed.setKeyListener(new KeyListener() {
            @Override
            public int getInputType() { return 0; }

            @Override
            public boolean onKeyDown(View view, Editable editable, int i, KeyEvent keyEvent) { return false; }

            @Override
            public boolean onKeyUp(View view, Editable editable, int i, KeyEvent keyEvent) {
                if(!txtUploadSpeed.getText().toString().trim().equals("") && !txtDownloadSpeed.getText().toString().trim().equals("")){
                    btnStart.setEnabled(true);
                }
                else {
                    btnStart.setEnabled(false);
                }
                return false;
            }

            @Override
            public boolean onKeyOther(View view, Editable editable, KeyEvent keyEvent) { return false; }

            @Override
            public void clearMetaKeyState(View view, Editable editable, int i) {}
        });

        this.txtDownloadSpeed.setKeyListener(new KeyListener() {
            @Override
            public int getInputType() { return InputType.TYPE_CLASS_NUMBER; }

            @Override
            public boolean onKeyDown(View view, Editable editable, int i, KeyEvent keyEvent) { return false; }

            @Override
            public boolean onKeyUp(View view, Editable editable, int i, KeyEvent keyEvent) {
                if(!txtUploadSpeed.getText().toString().trim().equals("") && !txtDownloadSpeed.getText().toString().trim().equals("")){
                    btnStart.setEnabled(true);
                } else {
                    btnStart.setEnabled(false);
                }
                return false;
            }

            @Override
            public boolean onKeyOther(View view, Editable editable, KeyEvent keyEvent) { return false; }

            @Override
            public void clearMetaKeyState(View view, Editable editable, int i) {}
        });*/
        //this.textView = (TextView) findViewById(R.id.main_txt);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length == 2){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                if(!txtUploadSpeed.getText().toString().trim().equals("") && !txtDownloadSpeed.getText().toString().trim().equals("") && !txtBand.getText().toString().trim().equals("")){
                    if(!doStuff()){
                        Toast.makeText(getApplicationContext(), "Failed location", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_empty_speed), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean doStuff(){
        this.btnStop.setEnabled(true);
        this.btnStart.setEnabled(false);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        //textView.append(dateFormat.format(date) + "\n");
        //data[0] - date
        data[0] = dateFormat.format(date);
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        try {
            List<String> providers = locationManager.getProviders(true);
            for(String provider : providers){
                Location loc = locationManager.getLastKnownLocation(provider);
                if(loc == null){
                    continue;
                }
                if(location == null || loc.getAccuracy() < location.getAccuracy()){
                    location = loc;
                }
            }
        } catch (SecurityException e){
            Log.e("MainActivity", "Fail to request location", e);
        }
        if(location != null){
            //textView.append("getLongitude " + String.valueOf(location.getLongitude()) + "\n");
            //textView.append("getLatitude " + String.valueOf(location.getLatitude()) + "\n");
            //data[1] - getLongitude, data[2] - getLatitude
            data[1] = String.valueOf(location.getLongitude());
            data[2] = String.valueOf(location.getLatitude());
            if(!getStrength()){
                //Toast.makeText(getApplicationContext(), "Failed signal", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Different BDM", Toast.LENGTH_LONG).show();
            }
        }
        return (location != null);
    }

    private boolean getStrength(){
        this.telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfoList = this.telephonyManager.getAllCellInfo();
        if(cellInfoList != null){
            for (CellInfo cellInfo : cellInfoList)
            {
                if (cellInfo instanceof CellInfoLte)
                {
                    // cast to CellInfoLte and call all the CellInfoLte methods you need
                    CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                    CellSignalStrengthLte signalStrengthLte = cellInfoLte.getCellSignalStrength();
                    //textView.append("LTE getDbm " + String.valueOf(signalStrengthLte.getDbm()) + "\n");
                    //textView.append("LTE getAsuLevel " + String.valueOf(signalStrengthLte.getAsuLevel()) + "\n");
                    //data[3] - getDbm, data[4] - getAsuLevel
                    /*int currentDBm = signalStrengthLte.getDbm();
                    if(currentDBm < (this.lastDbm - 5) || currentDBm > (this.lastDbm + 5)){
                        this.lastDbm = currentDBm;
                        this.hasChanged = true;
                        return false;
                    }*/
                    data[3] = String.valueOf(this.lastDbm);
                    data[4] = String.valueOf(signalStrengthLte.getAsuLevel());
                    //textView.append("LTE getEarfcn " + String.valueOf(cellInfoLte.getCellIdentity().getEarfcn()) + "\n");
                    /*if(Build.VERSION.SDK_INT == 24) {
                        textView.append("LTE getEarfcn " + String.valueOf(cellInfoLte.getCellIdentity().getEarfcn()) + "\n");
                    }*/
                    //Toast.makeText(getApplicationContext(), "Here " + String.valueOf(value), Toast.LENGTH_LONG).show();
                }
            }
        }

        this.phoneStateListener = new PhoneStateListener(){
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                //textView.setText(signalStrength.toString());
                //Toast.makeText(getApplicationContext(), signalStrength)

                try {
                    Method[] methods = android.telephony.SignalStrength.class.getMethods();
                    //Toast.makeText(getApplicationContext(), "Point 1", Toast.LENGTH_LONG).show();
                    for (Method mthd : methods) {
                        if(mthd.getName().equals("getLteSignalStrength")){
                            //textView.append(mthd.getName() + " " + mthd.invoke(signalStrength) + "\n");
                        }
                        if(mthd.getName().equals("getLteRsrq")){
                            //data[5] - getDbm
                            data[5] = String.valueOf(mthd.invoke(signalStrength));
                            //textView.append(mthd.getName() + " " + mthd.invoke(signalStrength) + "\n");
                        }
                        if(mthd.getName().equals("getLteRsrp")){
                            //data[6] - getDbm
                            data[6] = String.valueOf(mthd.invoke(signalStrength));
                            ///textView.append(mthd.getName() + " " + mthd.invoke(signalStrength) + "\n");
                        }
                        if(mthd.getName().equals("getLteRssnr")){
                            //data[7] - getDbm
                            data[7] = String.valueOf(mthd.invoke(signalStrength));
                            //textView.append(mthd.getName() + " " + mthd.invoke(signalStrength) + "\n");
                        }
                        if (mthd.getName().equals("getLteCqi")) {
                            //data[8] - getDbm
                            data[8] = String.valueOf(mthd.invoke(signalStrength));
                            //textView.append(mthd.getName() + " " + mthd.invoke(signalStrength) + "\n");
                            //Toast.makeText(getApplicationContext(), mthd.getName() + " " + mthd.invoke(signalStrength), Toast.LENGTH_LONG).show();
                        }
                    }
                    //Toast.makeText(getApplicationContext(), "Point 2", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //textView.append("signalStrength clean " + signalStrength.getGsmSignalStrength() + "\n");
                stopListening((2 * signalStrength.getGsmSignalStrength()) - 113);
            }
        };
        try {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void stopListening(int signalStrength){
        //Toast.makeText(getApplicationContext(), String.valueOf(signalStrength), Toast.LENGTH_LONG).show();
        //textView.append("signalStrength " + String.valueOf(signalStrength) + "\n");
        this.telephonyManager.listen(this.phoneStateListener, PhoneStateListener.LISTEN_NONE);
        //data[9] - uploadSpeed, data[10] - downlaodSpeed
        this.data[9] = txtUploadSpeed.getText().toString();
        this.data[10] = txtDownloadSpeed.getText().toString();
        this.data[11] = txtBand.getText().toString();
        this.saveCSV();
    }

    private void saveCSV(){
        String baseDir = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String filePath = baseDir + File.separator + this.fileName;
        File f = new File(filePath);
        CSVWriter writer;
        try {
            if(f.exists() && !f.isDirectory()){
                writer = new CSVWriter(new FileWriter(filePath, true));
            } else {
                writer = new CSVWriter(new FileWriter(filePath));
                String header [] = {"date", "longitude", "latitude", "dbm", "asuLevel", "lteRsrq", "lteRsrp", "lteRssnr", "lteCqi", "uploadSpeed", "downloadSpeed", "band"};
                writer.writeNext(header);
            }
            writer.writeNext(this.data);
            writer.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
