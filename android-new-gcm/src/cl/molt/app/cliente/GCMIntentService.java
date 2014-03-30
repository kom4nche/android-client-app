package cl.molt.app.cliente;

import java.util.Random;

import cl.molt.app.cliente.R;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class GCMIntentService extends IntentService 
{
	private static final int NOTIF_ALERTA_ID = 1;
	private int notify_id = 0;
	private String hora;

	public GCMIntentService() {
        super("GCMIntentService");
    }
	
	@Override
    protected void onHandleIntent(Intent intent) 
	{
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        
        String messageType = gcm.getMessageType(intent);
        Bundle extras = intent.getExtras();
        
        int number = (new Random().nextInt(100));

        if (!extras.isEmpty())
        {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
            	    hora = extras.getString("hora");
                    mostrarNotification(extras.getString("message"),number, hora);
            }
        }
        
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }
	
	
	private void mostrarNotification(String msg, int notificacion, String hora) 
	{
		NotificationManager mNotificationManager =    
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); 
		
		NotificationCompat.Builder mBuilder = 
			new NotificationCompat.Builder(this)
				.setSmallIcon(android.R.drawable.stat_sys_warning)  
				.setContentTitle("Alerta Carwatch!")  
				.setContentText(msg)
				.setTicker("Alerta!")
		        .setDefaults(-1)
		        .setAutoCancel(true);
		
		Intent notIntent =  new Intent(this, WelcomeActivity.class);    
		PendingIntent contIntent = PendingIntent.getActivity(     
				this, 0, notIntent, 0);   
		
		mBuilder.setContentIntent(contIntent);
		
		mNotificationManager.notify(notificacion, mBuilder.build());
    }
}
