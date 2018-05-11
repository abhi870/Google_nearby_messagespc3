package abhi870.com.pc3;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Api.ApiOptions.NoOptions;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.security.PermissionCollection;
import java.security.acl.Permission;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.common.api.GoogleApi.zza.zzfjz;

public class MainActivity extends AppCompatActivity {
String endPointId1="";
String endPointId2="";
    TextView sendText;
TextView incomingText;
    String client="";
int flag=0;
GoogleApiClient googleApiClient2;
    newAdvertisingTask n=new newAdvertisingTask();
    public void sendMsg(View view) {
        sendText=(TextView) findViewById(R.id.editText1);
        if (!client.equals("")) {
            String msg = sendText.getText().toString();
            byte[] b = msg.getBytes();
            Log.i("msg", client + " main");
            Nearby.getConnectionsClient(MainActivity.this).sendPayload(client, Payload.fromBytes(b));
            sendText.setText("");
            Toast.makeText(MainActivity.this, "bytes sent", Toast.LENGTH_LONG).show();
        } else {
            System.out.println(client);
            Log.i("msg", client.toString());
            Toast.makeText(MainActivity.this, client, Toast.LENGTH_LONG).show();
        }
    }
    public void startAdvertising1(View view)
    {
        n.execute();
    }
    public void startDiscovering1(View view)
    {
         newDiscoveringTask d=new newDiscoveringTask();
         d.execute();
    }

    public ConnectionInfo connectionInfo;
    public ConnectionLifecycleCallback connectionLife;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
    private final PayloadCallback mPayloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    // A new payload is being sent over.
                    incomingText=(TextView) findViewById(R.id.editText2);
                    incomingText.setText(new String(payload.asBytes()));
                    Log.i("logHere","ok");
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                               // Payload progress has updated.
                }
            };
    private  final ConnectionLifecycleCallback mConnectionLifecycleCallback=
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    // Automatically accept the connection on both sides.
                    Log.i("Log",endpointId);
                    Nearby.getConnectionsClient(MainActivity.this).acceptConnection(endpointId, mPayloadCallback);
                    Log.i("onConnectionInitialised","check");
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {

                    switch (result.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK: {
                            client=endpointId;
                            Toast.makeText(MainActivity.this,"Connected",Toast.LENGTH_LONG).show();
                            Log.i("onConnectionResult", "checkStatusOk");
                            // We're connected! Can now start sending and receiving data.
                            break;
                        }
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED: {
                            Log.i("onConnectionRejected","check");
                            // The connection was rejected by one or both sides.
                            break;
                        }
                        default: {
                            // The connection was broken before it was accepted.
                            Log.i("default","check");

                            break;
                        }
                    }
                }
                @Override
                public void onDisconnected(String endpointId) {
                    // We've been disconnected from this endpoint. No more data can be
                    // sent or received.
                    Log.i("onConnectionDisc","check");

                }
            };
    public final EndpointDiscoveryCallback endpointDiscoveryCallback=new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(String s, DiscoveredEndpointInfo discoveredEndpointInfo) {
            Log.i("onEndpointFound",s);
            Nearby.getConnectionsClient(MainActivity.this).requestConnection( discoveredEndpointInfo.getEndpointName(), s, mConnectionLifecycleCallback).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("log discover","Connection requested...");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("log discover","Connection request failure...");
                }
            });
        }

        @Override
        public void onEndpointLost(String s) {
            Log.i("onEndpointLost","check");
        }
    };
    protected class newAdvertisingTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] objects) {
            Nearby.getConnectionsClient(MainActivity.this).startAdvertising("DeviceA","abhi870.com.pc3",mConnectionLifecycleCallback,new AdvertisingOptions(Strategy.P2P_STAR)).addOnSuccessListener(new
                                                                                                                                                                                                             OnSuccessListener<Void>() {
                                                                                                                                                                                                                 @Override
                                                                                                                                                                                                                 public void onSuccess(Void aVoid) {
                                                                                                                                                                                                                     Toast.makeText(MainActivity.this,"started listening...",Toast.LENGTH_LONG).show();
                                                                                                                                                                                                                 }
                                                                                                                                                                                                             }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,"listening failure...",Toast.LENGTH_LONG).show();             }
            });

            return null;
        }
    }
    protected class newDiscoveringTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] objects) {
            Nearby.getConnectionsClient(MainActivity.this).startDiscovery("abhi870.com.pc3",endpointDiscoveryCallback,new DiscoveryOptions(Strategy.P2P_STAR)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(MainActivity.this,"trying to connect...",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,"connection failure...",Toast.LENGTH_LONG).show();
                    Log.i("startDiscoveryError",e.getMessage());
                }
            });
            return null;
        }
    }
}
