package cl.molt.app.cliente;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import cl.molt.app.cliente.R;

import org.apache.http.message.BasicNameValuePair;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Build;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.ToggleButton;
import android.widget.TwoLineListItem;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

public class WelcomeActivity  extends android.support.v4.app.FragmentActivity {
	
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	public static final String EXTRA_MESSAGE = "message";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_EXPIRATION_TIME = "onServerExpirationTimeMs";
    private static final String PROPERTY_USER = "user";
    
    static final String[] perro = {"a", "b", "c"};

    String GCM_URL = "";
    String SENDER_ID = "";    
   
    String GCM_URL_STATIC = "http://tesis.mobi/GCMServer/server.php";
    String SENDER_ID_STATIC = "1028098358894";
    

    public static final long EXPIRATION_TIME_MS = 1000 * 3600 * 24 * 7;

    static final String TAG = "GCM Message"; 
    
    private Context context;
    private String regid;
    private GoogleCloudMessaging gcm;
    //private EditText txtUsuario;
    //private Button btnRegistrar;
    private TextView welcome;
    private String namedb;
	private Context contexto;
	private ProgressDialog pd;
	private ProgressDialog pd2;
	private ListView lstAlertas;
	private ToggleButton button1;
	private TextView infotext;
	private ImageView imagen;
	
	private GoogleMap mapa = null;
	private int vista = 1;
	private int menuid = 2;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		welcome = (TextView)findViewById(R.id.welcome_text);
		lstAlertas = (ListView)findViewById(R.id.lstAlertas);
		mapa = ((SupportMapFragment) getSupportFragmentManager()
				   .findFragmentById(R.id.map)).getMap();		
		

		mapa.setInfoWindowAdapter(new InfoWindowAdapter() {

	        @Override
	        public View getInfoWindow(Marker arg0) {
	            return null;
	        }

	        @Override
	        public View getInfoContents(Marker marker) {

	            View v = getLayoutInflater().inflate(R.layout.marker, null);

	            TextView info= (TextView) v.findViewById(R.id.info);

	            info.setText(marker.getTitle());
	            
	            //marker.showInfoWindow();

	            return v;
	        }
	    });
		
		button1 = (ToggleButton) findViewById(R.id.toggleButton1);
		infotext = (TextView) findViewById(R.id.label_act);
		imagen = (ImageView) findViewById(R.id.imageView1);
		
		
		//txtUsuario = (EditText)findViewById(R.id.txtUsuario);
		//btnRegistrar = (Button)findViewById(R.id.btnGuadar);
		

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        GCM_URL = prefs.getString("gcm_url", "");
        SENDER_ID = prefs.getString("sender_id", "");
        
	    if (GCM_URL.length() == 0) 
	    {
	    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("gcm_url", GCM_URL_STATIC);
            
            editor.commit();
            
            GCM_URL = GCM_URL_STATIC;
	    }
	    
	    if (SENDER_ID.length() == 0) 
	    {
	    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("sender_id", SENDER_ID_STATIC);
            
            editor.commit();
            
            SENDER_ID = SENDER_ID_STATIC;
	    }
	    
        SharedPreferences prefs2 = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        namedb = prefs2.getString("namedb", "");
        //String passdb = prefs2.getString("passdb", ""); 
        
        welcome.setText("Bienvenido: " + namedb);
		
		/*btnRegistrar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				context = getApplicationContext();
				
				//Chequemos si está instalado Google Play Services
				//if(checkPlayServices())
				//{
			        gcm = GoogleCloudMessaging.getInstance(WelcomeActivity.this);
					
			        //Obtenemos el Registration ID guardado
			        regid = getRegistrationId(context);
			
			        //Si no disponemos de Registration ID comenzamos el registro
			        if (regid.equals("")) {
			    		TareaRegistroGCM tarea = new TareaRegistroGCM();
			    		tarea.execute(txtUsuario.getText().toString());
			        }
				//}
				//else 
				//{
		        //    Log.i(TAG, "No se ha encontrado Google Play Services.");
		        //}
			}
		});	 */
        
        contexto = this;
        
        Resources res = getResources();
        
        TabHost tabs=(TabHost)findViewById(android.R.id.tabhost);
        tabs.setup();
        
        TabHost.TabSpec spec=tabs.newTabSpec("mitab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("On/Off", 
        		res.getDrawable(android.R.drawable.ic_lock_power_off));
        tabs.addTab(spec);
        
        spec=tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Alertas", 
        		res.getDrawable(android.R.drawable.ic_dialog_alert));
        tabs.addTab(spec);
        
        spec=tabs.newTabSpec("mitab3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Mapa", 
        		res.getDrawable(android.R.drawable.ic_dialog_map));
        tabs.addTab(spec);
        
        tabs.setCurrentTab(1);
        
        tabs.setOnTabChangedListener(new OnTabChangeListener() {
			@SuppressLint("NewApi")
			public void onTabChanged(String tabId) {
				//Log.i("AndroidTabsDemo", "Pulsada pesta�a: " + tabId);
				
				if (tabId.equals("mitab1"))
				{
					int currentAPIVersion = android.os.Build.VERSION.SDK_INT;

					if (currentAPIVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) 
					{
						menuid = 1;
						invalidateOptionsMenu();
					}
					else  menuid = 1;

				}
				
				if (tabId.equals("mitab2"))
				{
					int currentAPIVersion = android.os.Build.VERSION.SDK_INT;

					if (currentAPIVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) 
					{
						menuid = 2;
						invalidateOptionsMenu();
					}
					else  menuid = 2;
					
					TareaAlertas tarea3_tab2 = new TareaAlertas();
			        tarea3_tab2.execute(namedb,"10");

				}
				
				if (tabId.equals("mitab3"))
				{
					int currentAPIVersion = android.os.Build.VERSION.SDK_INT;

					if (currentAPIVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) 
					{
						menuid = 3;
						invalidateOptionsMenu();
					}
					else  menuid = 3;
					
					mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);
					
					TareaUbicacion tarea2_tab3 = new TareaUbicacion();
			        tarea2_tab3.execute(namedb,"1");	
					

				}
			}
		});
        
        if (namedb.length() > 0)
        
        {registroGCM();}
        
        
        button1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          if (isChecked) {
           Log.d("info", "Button1 is on!");
           //textView2.setText("Button2 is ON");
           
           infotext.setText("Sistema OK");
           String uri = "@drawable/boton_verde";

           int imageResource = getResources().getIdentifier(uri, null, getPackageName());
           Drawable res = getResources().getDrawable(imageResource);
           imagen.setImageDrawable(res);
           
          } else {
           Log.d("info", "Button1 is off!");
           //textView2.setText("Button2 is OFF");
           
           infotext.setText("Alertas Desactivadas");
           String uri = "@drawable/boton_rojo";

           int imageResource = getResources().getIdentifier(uri, null, getPackageName());
           Drawable res = getResources().getDrawable(imageResource);
           imagen.setImageDrawable(res);
           
          }
            }
        });
        
		TareaAlertas tarea3_tab2 = new TareaAlertas();
        tarea3_tab2.execute(namedb,"10");
	}
	
//	@Override
//	protected void onResume() 
//	{
//	    super.onResume();
//	    
//	    checkPlayServices();
//	}
	
//	private boolean checkPlayServices() {
//	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//	    if (resultCode != ConnectionResult.SUCCESS) 
//	    {
//	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) 
//	        {
//	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
//	        } 
//	        else 
//	        {
//	            Log.i(TAG, "Dispositivo no soportado.");
//	            finish();
//	        }
//	        return false;
//	    }
//	    return true;
//	}
	
	private void registroGCM() 
	{
	
	context = getApplicationContext();
	
	//Chequemos si está instalado Google Play Services
	//if(checkPlayServices())
	//{
        gcm = GoogleCloudMessaging.getInstance(WelcomeActivity.this);
		
        //Obtenemos el Registration ID guardado
        regid = getRegistrationId(context);

        //Si no disponemos de Registration ID comenzamos el registro
        if (regid.equals("")) {
    		TareaRegistroGCM tarea = new TareaRegistroGCM();
    		tarea.execute(namedb);
        }
        
	}
	
	
	private String getRegistrationId(Context context) 
	{
	    SharedPreferences prefs = getSharedPreferences(
	    		WelcomeActivity.class.getSimpleName(), 
	            Context.MODE_PRIVATE);
	    
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    
	    if (registrationId.length() == 0) 
	    {
	        Log.d(TAG, "Registro GCM no encontrado.");
	        return "";
	    }
	    
	    String registeredUser = 
	    		prefs.getString(PROPERTY_USER, "user");
	    
	    int registeredVersion = 
	    		prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    
	    long expirationTime =
	            prefs.getLong(PROPERTY_EXPIRATION_TIME, -1);
	    
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
	    String expirationDate = sdf.format(new Date(expirationTime));
	    
	    Log.d(TAG, "Registro GCM encontrado (usuario=" + registeredUser + 
	    		", version=" + registeredVersion + 
	    		", expira=" + expirationDate + ")");
	    
	    int currentVersion = getAppVersion(context);
	    
	    if (registeredVersion != currentVersion) 
	    {
	        Log.d(TAG, "Nueva versión de la aplicación.");
	        return "";
	    }
	    else if (System.currentTimeMillis() > expirationTime)
	    {
	    	Log.d(TAG, "Registro GCM expirado.");
	        return "";
	    }
	    else if (!namedb.equals(registeredUser))
	    {
	    	Log.d(TAG, "Nuevo nombre de usuario.");
	        return "";
	    }
	    
	    return registrationId;
	}
	
	private static int getAppVersion(Context context) 
	{
	    try
	    {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        
	        return packageInfo.versionCode;
	    } 
	    catch (NameNotFoundException e) 
	    {
	        throw new RuntimeException("Error al obtener versión: " + e);
	    }
	}
	
	private class TareaRegistroGCM extends AsyncTask<String,Integer,String>
	{
		@Override
        protected String doInBackground(String... params) 
		{
            String msg = "";
            
            try 
            {
                if (gcm == null) 
                {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                
                //Nos registramos en los servidores de GCM
                regid = gcm.register(SENDER_ID);
                
                Log.d(TAG, "Registrado en GCM: registration_id=" + regid);

                //Nos registramos en nuestro servidor
                boolean registrado = registroServidor(params[0], regid);

                //Guardamos los datos del registro
                if(registrado)
                {
                	setRegistrationId(context, params[0], regid);
                }
            } 
            catch (IOException ex) 
            {
            	Log.d(TAG, "Error registro en GCM:" + ex.getMessage());
            }
            
            return msg;
        }
		
	    @Override
	    protected void onPreExecute() {
			pd2 = new ProgressDialog(contexto, AlertDialog.THEME_HOLO_DARK);
			//pd.setTitle("Processing...");
			pd2.setMessage("registrando...");
			pd2.setCancelable(false);
			pd2.setIndeterminate(true);
			pd2.show();
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        // Lets the second task to know that first has finished
			if (pd2!=null) {
				pd2.dismiss();
			}
	    }
	}
	
	private void setRegistrationId(Context context, String user, String regId) 
	{
	    SharedPreferences prefs = getSharedPreferences(
	    		WelcomeActivity.class.getSimpleName(), 
	            Context.MODE_PRIVATE);
	    
	    int appVersion = getAppVersion(context);
	    
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_USER, user);
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.putLong(PROPERTY_EXPIRATION_TIME, 
	    		System.currentTimeMillis() + EXPIRATION_TIME_MS);
	    
	    editor.commit();
	}
	
	private boolean registroServidor(String usuario, String regId)
	{
		boolean reg = false;
		
		final String NAMESPACE = "http://tesis.mobi/";
		final String URL= GCM_URL;
		final String METHOD_NAME = "RegistroCliente";
		final String SOAP_ACTION = "http://tesis.mobi/RegistroCliente";

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		
		request.addProperty("usuario", usuario); 
		request.addProperty("regGCM", regId); 
		request.addProperty("model", getDeviceName());
		request.addProperty("imei", getIMEI());

		SoapSerializationEnvelope envelope = 
				new SoapSerializationEnvelope(SoapEnvelope.VER11);
		
		envelope.dotNet = true; 

		envelope.setOutputSoapObject(request);

		HttpTransportSE transporte = new HttpTransportSE(URL);

		try 
		{
			transporte.call(SOAP_ACTION, envelope);

			//SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
			Object response = envelope.getResponse();
			String res = response.toString();
			
			if(res.equals("1"))
			{
				Log.d(TAG, "Registrado en mi servidor.");
				reg = true;
			}
		} 
		catch (Exception e) 
		{
			Log.d(TAG, "Error registro en mi servidor: " + e.getCause() + " || " + e.getMessage());
		} 
		
		return reg;
	}
	
	public String getDeviceName() {
		  String manufacturer = Build.MANUFACTURER;
		  String model = Build.MODEL;
		  
		  if (manufacturer.length() == 0) manufacturer = "none";
		  if (model.length() == 0) model = "no model";
		  
		  if (model.startsWith(manufacturer)) {
		    return capitalize(model);
		  } else {
		    return capitalize(manufacturer) + " " + model;
		  }
		}


		private String capitalize(String s) {
		  if (s == null || s.length() == 0) {
		    return "";
		  }
		  char first = s.charAt(0);
		  if (Character.isUpperCase(first)) {
		    return s;
		  } else {
		    return Character.toUpperCase(first) + s.substring(1);
		  }
		}
		
		public String getIMEI() 
		{
			
			String imei = "";
			
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		    
			if (tm.getDeviceId() != null){
		        imei = tm.getDeviceId(); //*** use for mobiles
		     }
		    else{
		        imei = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID); //*** use for tablets
		     }
			
			//String imei = tm.getDeviceId();
			//String phone = tm.getLine1Number();
			
			if (imei.length() == 0) imei = "none";
			
			return imei;
		}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
	    if (Build.VERSION.SDK_INT >= 11) {
	        selectMenu(menu);
	    }
	    return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	    if (Build.VERSION.SDK_INT < 11) {
	        selectMenu(menu);
	    }
	    return true;
	}

	private void selectMenu(Menu menu) {
	    menu.clear();
	    MenuInflater inflater = getMenuInflater();
	    
	    if (menuid==1) {
	        inflater.inflate(R.menu.activity_welcome, menu);
	    }
	    if (menuid==2) {
	        inflater.inflate(R.menu.activity_menu2, menu);
	    }
	    if (menuid==3) {
	        inflater.inflate(R.menu.activity_menu3, menu);
	    }
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {


            case R.id.opciones:

                //iniciamos la pantalla de opciones
                Intent opciones = new Intent(WelcomeActivity.this, OpcionesActivity.class);
                startActivity(opciones);
                break;
            
            case R.id.closessesion:
                    //Borramos el usuario almacenado en preferencias y volvemos a la pantalla de login
                    SharedPreferences settings = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("name", "");
                    editor.putString("pass", "");

                    //Confirmamos el almacenamiento.
                    editor.commit();
                    
                    //Volvemos a la pantalla de Login
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                    
			case R.id.menu_vista:
				alternarVista();
				break;
			case R.id.menu_ult3:
				//Centramos el mapa en Espa�a
				
				TareaAlertas tarea = new TareaAlertas();
		        tarea.execute(namedb,"3");	
				break;
				
			case R.id.menu_ult15:
				//Centramos el mapa en Espa�a
				
				TareaAlertas tarea3 = new TareaAlertas();
		        tarea3.execute(namedb,"15");	
				break;
			
			case R.id.menu_ultima_pos:
				
				TareaUbicacion tarea2 = new TareaUbicacion();
		        tarea2.execute(namedb,"2");	
				
				
				//CameraUpdate camUpd2 = 
				//	CameraUpdateFactory.newLatLngZoom(new LatLng(-39.8330422, -73.2449274), 5F);
				//mapa.animateCamera(camUpd2);
				
				break;
				
				
				
			case R.id.menu_3d:
				LatLng madrid = new LatLng(-39.8330422, -73.2449274);
				CameraPosition camPos = new CameraPosition.Builder()
					    .target(madrid)   //Centramos el mapa en Madrid
					    .zoom(13)         //Establecemos el zoom en 19
					    .bearing(90)      //Establecemos la orientaci�n con el noreste arriba
					    .tilt(70)         //Bajamos el punto de vista de la c�mara 70 grados
					    .build();
				
				CameraUpdate camUpd3 = 
						CameraUpdateFactory.newCameraPosition(camPos);
				
				mapa.animateCamera(camUpd3);
				break;
			case R.id.menu_posicion:
				CameraPosition camPos2 = mapa.getCameraPosition();
				LatLng pos = camPos2.target;
				Toast.makeText(WelcomeActivity.this, 
						"Lat: " + pos.latitude + " - Lng: " + pos.longitude, 
						Toast.LENGTH_LONG).show();
				break;
				
			case R.id.menu_actualizar1:

				break;

            }
            return super.onOptionsItemSelected(item);
    }
    
	private void alternarVista()
	{
		vista = (vista + 1) % 3;
		
		switch(vista)
		{
			case 0:
				mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				break;
			case 1:
				mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				break;
			case 2:
				mapa.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				break;
		}
	}
	
	
	private class TareaUbicacion extends AsyncTask<String,Integer,Boolean> {
		
		private geoPoint[] listaPuntos;
		private String datos;
		 
	    protected Boolean doInBackground(String... params) {
	    	
	    	boolean resul = true;
	 
	    	final String NAMESPACE = "http://tesis.mobi/";
			final String URL="http://tesis.mobi/INFOServer/server.php";
			final String METHOD_NAME = "getUbicacion";
			final String SOAP_ACTION = "http://tesis.mobi/getUbicacion";

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			
			int dos = 1;
			
			request.addProperty("usuario", params[0]);
			request.addProperty("cantidad", dos); 

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;

			envelope.setOutputSoapObject(request);

			HttpTransportSE transporte = new HttpTransportSE(URL);

			try 
			{
				transporte.call(SOAP_ACTION, envelope);

				//SoapObject resSoap =(SoapObject)envelope.getResponse();
				//SoapObject resSoap = (SoapObject) envelope.bodyIn;
				
				KvmSerializable resSoap = (KvmSerializable)envelope.bodyIn;
				Vector<?> responseVector = (Vector<?>) resSoap.getProperty(0);
				
				
				listaPuntos = new geoPoint[responseVector.size()];
				datos = "Hola: "+responseVector.size();
				
				for (int i = 0; i < listaPuntos.length; i++)
				{
			           //SoapObject ic = (SoapObject) resSoap.getProperty(i);
			           SoapObject ic=(SoapObject)responseVector.get(i);
			            
			           geoPoint cli = new geoPoint();
			           cli.lat = Double.parseDouble(ic.getProperty(0).toString());
			           cli.lng = Double.parseDouble(ic.getProperty(1).toString());
			           cli.hora = ic.getProperty(2).toString();
			            
			           listaPuntos[i] = cli;
			    }
			} 
			catch (Exception e) 
			{
				resul = false;
				Log.d(TAG, "Error registro en mi servidor: " + e.getCause() + " || " + e.getMessage());
			} 
	 
	        return resul;
	    }
	    
	    @Override
	    protected void onPreExecute() {
			pd = new ProgressDialog(contexto, AlertDialog.THEME_HOLO_DARK);
			//pd.setTitle("Processing...");
			pd.setMessage("localizando...");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
	    }
	    
	    @Override
	    protected void onPostExecute(Boolean result) {
	    	
	    	if (result)
	    	{
	    		//welcome.setText(datos+listaPuntos[0].lat);
			    mapa.addMarker(new MarkerOptions()
		         .position(new LatLng(listaPuntos[0].lat, listaPuntos[0].lng))
		         .title("Lat: "+listaPuntos[0].lat+" Long: "+listaPuntos[0].lng+"\n"+"Hora: "+listaPuntos[0].hora)
		         
		         ).showInfoWindow();
			    

	    		
				CameraUpdate camUpd2 = 
					CameraUpdateFactory.newLatLngZoom(new LatLng(listaPuntos[0].lat, listaPuntos[0].lng), 16);
				mapa.animateCamera(camUpd2);
				


	    	}
	    	else
	    	{
	    		welcome.setText("Error!");
	    	}
	    	
			if (pd!=null) {
				pd.dismiss();
			}
	    }
	    
	}
	
	
	
	private class TareaAlertas extends AsyncTask<String,Integer,Boolean> {
		
		private geoAlerta[] listaAlertas;
		 
	    protected Boolean doInBackground(String... params) {
	    	
	    	boolean resul = true;
	 
	    	final String NAMESPACE = "http://tesis.mobi/";
			final String URL="http://tesis.mobi/INFOServer/server.php";
			final String METHOD_NAME = "getAlertas";
			final String SOAP_ACTION = "http://tesis.mobi/getAlertas";

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			
			request.addProperty("usuario", params[0]);
			request.addProperty("cantidad", params[1]);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;

			envelope.setOutputSoapObject(request);

			HttpTransportSE transporte = new HttpTransportSE(URL);

			try 
			{
				transporte.call(SOAP_ACTION, envelope);

				//SoapObject resSoap =(SoapObject)envelope.getResponse();
				//SoapObject resSoap = (SoapObject) envelope.bodyIn;
				
				KvmSerializable resSoap = (KvmSerializable)envelope.bodyIn;
				Vector<?> responseVector = (Vector<?>) resSoap.getProperty(0);
				
				listaAlertas = new geoAlerta[responseVector.size()];
				
				for (int i = 0; i < listaAlertas.length; i++) 
				{
					
			           //SoapObject ic = (SoapObject) resSoap.getProperty(i);
			           SoapObject ic=(SoapObject)responseVector.get(i);
			            
			           geoAlerta cli = new geoAlerta();
			           cli.id = Integer.parseInt(ic.getProperty(0).toString());
			           cli.hora = ic.getProperty(1).toString();
			           cli.tipo = ic.getProperty(2).toString();
			            
			           listaAlertas[i] = cli;
			    }
			} 
			catch (Exception e) 
			{
				resul = false;
				Log.d(TAG, "Error registro en mi servidor: " + e.getCause() + " || " + e.getMessage());
			} 
	 
	        return resul;
	    }
	    
	    @Override
	    protected void onPreExecute() {
			pd = new ProgressDialog(contexto, AlertDialog.THEME_HOLO_DARK);
			//pd.setTitle("Processing...");
			pd.setMessage("conectando...");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
	    }
	    
	    @Override
	    protected void onPostExecute(Boolean result) {
	    	
	    	if (result)
	    	{
	    		//welcome.setText("Funciona CTM!!"+listaAlertas[0].id);
				//CameraUpdate camUpd2 = 
				//	CameraUpdateFactory.newLatLngZoom(new LatLng(listaPuntos[0].lat, listaPuntos[0].lng), 16);
				//mapa.animateCamera(camUpd2);
	    		
				final String[][] datos = new String[2][listaAlertas.length];
				
				
				 
				for(int i=0; i<listaAlertas.length; i++)
				{
					if (listaAlertas[i].tipo.equals("P") )
					{
						datos[0][i] = "Origen Alerta: Puertas";
					}
					
					else {
						
						datos[0][i] = "Origen Alerta: Desconocido";
					}
					
					datos[1][i] = listaAlertas[i].hora;
			    }
				
				final String[] lista = datos[0];
					 
				ArrayAdapter<String> adapter;
				
				adapter = new ArrayAdapter<String>(contexto, R.layout.list_item, R.id.text, lista) {
			        @Override
			        public View getView(int position, View convertView, ViewGroup parent) {
			            View view = super.getView(position, convertView, parent);
			            
			            //View row= convertView;  
			            TextView txt = null;
			            
			            if(convertView == null){
			            	LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			            	convertView = inflater.inflate(R.layout.list_item, null);
			            	txt =(TextView)convertView.findViewById(R.id.text);
			            	convertView.setTag(txt);                
			            }else{
			            	txt = (TextView) convertView.getTag();
			            }

			            String item = getItem(position);

			            TextView subTitleView = (TextView) view.findViewById(R.id.subtitle);
			            subTitleView.setText("Hora de activación: " + datos[1][position]);

			            return view;
			        }};
				 
				lstAlertas.setAdapter(adapter);
	    		
	    	}
	    	else
	    	{
	    		welcome.setText("Error!");
	    	}
	    	
				if (pd!=null) {
					pd.dismiss();
				}

	    }
	}

    @Override 
    protected void onDestroy() {
    	if (pd!=null) {
			pd.dismiss();
		}
    	
    	if (pd2!=null) {
			pd2.dismiss();
		}
    	super.onDestroy();
    }
}
