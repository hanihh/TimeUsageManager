package fluxdev.org.screenmonitor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateService extends Service {
    private static Timer heartbeatTimer = new Timer();
    private static CountDownTimer lockActivityTimer;
    private WindowManager windowManager;
    private FloatingActionButton button;

    private Context ctx;

    Session activeSession;
    public UpdateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        //TODO: get default time out to prevent always awake status
        Log.i("Service", "Service Started");
        ctx = this;
        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);

        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }


    private class HeartBeatTask extends TimerTask
    {
        public void run()
        {
            HeartBeat beat = HeartBeat.load(HeartBeat.class, 1);
            if (beat == null) {
                beat = new HeartBeat();
                beat.setSequence(1);
            } else {
                beat.setSequence(beat.getSequence() + 1);

            }
            //Toast.makeText(getBaseContext(), "Beat No " + beat.getSequence(), Toast.LENGTH_LONG).show();
            beat.setBeat(new Date());
            beat.save();
        }
    }

    Handler handler;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            boolean screenOff = intent.getBooleanExtra("screen_state", false);
            if (screenOff) {
                if (heartbeatTimer != null) { // 1.Stop Heatbeat
                    heartbeatTimer.cancel();
                    heartbeatTimer.purge();
                    heartbeatTimer = null;
                }
                if (lockActivityTimer != null) { // 2.Stop 20 minutes Service
                    lockActivityTimer.cancel();
                    lockActivityTimer = null;
                }
                Log.i("Service", "Screen Off");
                try { // 3.Remove the alert if exist !
                    if (button != null)
                        windowManager.removeView(button);
                } catch (final IllegalArgumentException e) {
                    Log.i("UpdateService", "IllegalArgumentException");
                } catch (final Exception e) {
                    Log.i("Service", "Exception");
                } finally {
                    button = null;
                }

                // 4.Save Session with end date
                activeSession = Session.getLast();
                activeSession.setEndDate(new Date());
                activeSession.setDuration(activeSession.getSessionSeconds());
                activeSession.save();
                Toast.makeText(this, "Screen Went Off", Toast.LENGTH_LONG).show();
            } else { // Screen On
                // 1. Handle unexpected shutdown
                Session last = Session.getLast();
                if (last != null) {
                    if (last.getEndDate() == null) {
                        HeartBeat beat = HeartBeat.load(HeartBeat.class, 1);
                        if (beat.getBeat() != null) {
                            last.setEndDate(beat.getBeat());
                            last.save();
                        }
                    }
                }

                // 2. Start Heartbeat Timer and schedule it every 30 sec
                heartbeatTimer = new Timer();
                heartbeatTimer.scheduleAtFixedRate(new HeartBeatTask(), 0, 30000);

                // 3. Start the 20 minutes alarm
                //lockActivityTimer = new CountDownTimer(18000000,12000000) // Timer for 5 hours with step 20 minutes

                lockActivityTimer = new CountDownTimer(1800000,12000) // Timer for 5 hours with step 20 minutes
                {
                    @Override
                    public void onTick(long millisUntilFinished)
                    {
                        if (millisUntilFinished <= 1800000 - 12000) {
                            Toast.makeText(ctx, String.valueOf(millisUntilFinished), Toast.LENGTH_LONG).show();
                            displayView();
                        }
                        // Display Data by Every Ten Second
                    }
                    @Override
                    public void onFinish()
                    {

                    }

                }.start();
                activeSession = new Session(new Date(), null);
                activeSession.save();
                Toast.makeText(this, new Date().toString(), Toast.LENGTH_LONG).show();
                Log.i("Service", "Screen On");
            }
        } else {
            Log.i("Service", "Intent Null");
        }
        return Service.START_STICKY;
    }

    public void displayView() {

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        try { // 3.Remove the alert if exist !
            if (button != null)
                windowManager.removeView(button);
        } catch (final IllegalArgumentException e) {
            Log.i("UpdateService", "IllegalArgumentException");
        } catch (final Exception e) {
            Log.i("Service", "Exception");
        } finally {
            button = null;
        }
        //alarmImage = new ImageView(ctx);
        //alarmImage.setImageResource(R.drawable.abc_ic_menu_cut_mtrl_alpha);
        button = new FloatingActionButton(ctx);//(FloatingActionButton) layout.findViewById(R.id.pink_icon);
        button.setSize(FloatingActionButton.SIZE_MINI);
        button.setColorNormalResId(R.color.pink);
        button.setColorPressedResId(R.color.pink_pressed);
        button.setIcon(R.drawable.abc_ic_menu_cut_mtrl_alpha);
        button.setStrokeVisible(false);
        Toast.makeText(ctx, "tick", Toast.LENGTH_LONG).show();
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        windowManager.addView(button, params);
        //windowManager.addView(alarmImage, params);

        button.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Toast.makeText(ctx, "tick", Toast.LENGTH_LONG).show();

                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(v, params);
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (button != null) windowManager.removeView(button);
    }
}
