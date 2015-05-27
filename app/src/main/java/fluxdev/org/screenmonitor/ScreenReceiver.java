package fluxdev.org.screenmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {
    public ScreenReceiver() {
    }

    private boolean screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            screenOff = false;
        } else if ((intent.getAction().equals(Intent.ACTION_SCREEN_OFF))||(intent.getAction().equals(Intent.ACTION_SHUTDOWN))) {
            screenOff = true;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            screenOff = false;
        }

        Intent i = new Intent(context, UpdateService.class);
        i.putExtra("screen_state", screenOff);
        context.startService(i);
    }
}
