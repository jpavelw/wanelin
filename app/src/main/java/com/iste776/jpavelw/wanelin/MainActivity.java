package com.iste776.jpavelw.wanelin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 1);
                } else {
                    if(doStuff()){
                        //Toast.makeText(getApplicationContext(), "Got location", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed location", Toast.LENGTH_LONG).show();
                    }
                    if(!getStrength()){
                        Toast.makeText(getApplicationContext(), "Failed signal", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        this.textView = (TextView) findViewById(R.id.main_txt);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length == 2){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(this.doStuff()){
                    Toast.makeText(getApplicationContext(), "Got location", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed location", Toast.LENGTH_LONG).show();
                }
            }
            if(grantResults[1] == PackageManager.PERMISSION_GRANTED){
                if(!getStrength()){
                    Toast.makeText(getApplicationContext(), "Failed signal", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean doStuff(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        textView.append(dateFormat.format(date) + "\n");
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
            textView.append("getLongitude " + String.valueOf(location.getLongitude()) + "\n");
            textView.append("getLatitude " + String.valueOf(location.getLatitude()) + "\n");
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
                    CellSignalStrengthLte signalStrengthLte = ((CellInfoLte)cellInfo).getCellSignalStrength();
                    textView.append("LTE getDbm " + String.valueOf(signalStrengthLte.getDbm()) + "\n");
                    textView.append("LTE getAsuLevel " + String.valueOf(signalStrengthLte.getAsuLevel()) + "\n");
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
                            textView.append(mthd.getName() + " " + mthd.invoke(signalStrength) + "\n");
                        }
                        if(mthd.getName().equals("getLteRsrq")){
                            textView.append(mthd.getName() + " " + mthd.invoke(signalStrength) + "\n");
                        }
                        if(mthd.getName().equals("getLteRsrp")){
                            textView.append(mthd.getName() + " " + mthd.invoke(signalStrength) + "\n");
                        }
                        if(mthd.getName().equals("getLteRssnr")){
                            textView.append(mthd.getName() + " " + mthd.invoke(signalStrength) + "\n");
                        }
                        if (mthd.getName().equals("getLteCqi")) {
                            textView.append(mthd.getName() + " " + mthd.invoke(signalStrength) + "\n");
                            //Toast.makeText(getApplicationContext(), mthd.getName() + " " + mthd.invoke(signalStrength), Toast.LENGTH_LONG).show();
                        }
                    }
                    //Toast.makeText(getApplicationContext(), "Point 2", Toast.LENGTH_LONG).show();
                } catch (Exception e) {}

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
    }
}
