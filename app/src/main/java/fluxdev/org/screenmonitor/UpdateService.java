package fluxdev.org.screenmonitor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class UpdateService extends Service {
    public UpdateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Service", "Service Started");

        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);

        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Session session;
            boolean screenOff = intent.getBooleanExtra("screen_state", false);
            if (screenOff) {
                Log.i("Service", "Screen Off");
                //session = Session.findWithQuery(Session.class, "select * from session order by startDate limit 1");
                Toast.makeText(this, "Screen Went Off", Toast.LENGTH_LONG).show();
            } else {
                session = new Session( new Date(), new Date());
                session.save();
                Toast.makeText(this, new Date().toString(), Toast.LENGTH_LONG).show();
                Log.i("Service", "Screen On");
            }
        } else {
            Log.i("Service", "Intent Null");
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
