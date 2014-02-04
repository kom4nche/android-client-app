package cl.molt.app.cliente;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import cl.molt.app.cliente.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
        //Estaticos
        static String NAME = "admin";
        static String PASS = "1234";
        
        //Elementos layout
        EditText editName;
        EditText editPass;
        Button btnLogin;
        
        static final String TAG = "Login";
        boolean login = false;
        
    	private Context context;
    	private ProgressDialog pd;

        @Override
        protected void onCreate(Bundle savedInstanceState) 
        {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                
                context = this;
                
                //Recuperamos los elementos del layout
                editName = (EditText) findViewById(R.id.editText1);
                editPass = (EditText) findViewById(R.id.editText2);
                btnLogin = (Button) findViewById(R.id.button1);
                
    	        SharedPreferences settings = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("name", NAME);
                editor.putString("pass", PASS);
                
                //Confirmamos el almacenamiento.
                editor.commit();
                
                //Recuperamos las preferencias almacenadas
                SharedPreferences prefs = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
                String name = prefs.getString("name", "");
                String pass = prefs.getString("pass", "");
                
                SharedPreferences prefs2 = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
                String namedb = prefs2.getString("namedb", "");
                String passdb = prefs2.getString("passdb", "");
                
                //Comprobamos nombre y clave de ususario
                if(name.equals(namedb) && pass.equals(passdb)){
                        
                        //Si el usuario almacenado es correcto, entramos en la app
                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                }
                
                login = false;
                
                btnLogin.setOnClickListener(new View.OnClickListener() {
                        
                        @Override
                        public void onClick(View v) {
                        	
            				      TareaVerificaLogin tarea = new TareaVerificaLogin();
            				      tarea.execute(editName.getText().toString(),editPass.getText().toString());

                        }
                });
        }
        
    	private boolean verificaLogin(String usuario, String password)
    	{
    		boolean reg = false;
    		
    		final String NAMESPACE = "http://tesis.mobi/";
    		final String URL= "http://tesis.mobi/INFOServer/server.php";
    		final String METHOD_NAME = "LoginCliente";
    		final String SOAP_ACTION = "http://tesis.mobi/LoginCliente";

    		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
    		
    		
    		//request.addProperty("Content-Type", "text/xml; charset=utf-8");
    		
    		
    		request.addProperty("usuario", usuario); 
    		request.addProperty("password", password); 

    		
    		
    		SoapSerializationEnvelope envelope = 
    				new SoapSerializationEnvelope(SoapEnvelope.VER11);
    		
    		envelope.dotNet = true; 

    		envelope.setOutputSoapObject(request);

    		HttpTransportSE transporte = new HttpTransportSE(URL);
    		

    		try 
    		{
    			//transporte.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    			transporte.call(SOAP_ACTION, envelope);

				//SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
				//String res = resultado_xml.toString();
				
			    SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
			    String res=resultsRequestSOAP.getProperty("return").toString();
    			
    			Log.d(TAG, res);
    			
    			if(res.equals("1"))
    			{
    				Log.d(TAG, "Login OK. Usuario existe.");
    				reg = true;
    				login = true;
    			}
    		} 
    		catch (Exception e) 
    		{
    			Log.d(TAG, "Error en SOAP: " + e.getCause() + " || " + e.getMessage());
    		} 
    		
    		return reg;
    	}
    	
    	private class TareaVerificaLogin extends AsyncTask<String,Integer,String>
    	{

    		@Override
            protected String doInBackground(String... params) 
    		{
                String msg = "";
                
                try 
                {
                    //Nos registramos en nuestro servidor
                    boolean registrado = verificaLogin(params[0], params[1]);
                    
                    //Log.d(TAG, params[0]);
                    //Log.d(TAG, params[1]);
                    

                    //Guardamos los datos del registro
                    if(registrado)
                    {
                    	Log.d(TAG, "Login OK");
                    }
                } 
                catch (Exception ex) 
                {
                	Log.d(TAG, "Error en los datos del usuario:" + ex.getMessage());
                }
                
                return msg;
            }
    		
    	    @Override
    	    protected void onPreExecute() {
    			pd = new ProgressDialog(context, AlertDialog.THEME_HOLO_DARK);
    			//pd.setTitle("Processing...");
    			pd.setMessage("conectando...");
    			pd.setCancelable(false);
    			pd.setIndeterminate(true);
    			pd.show();
    	    }

    	    @Override
    	    protected void onPostExecute(String result) {
    	        // Lets the second task to know that first has finished
    			if (pd!=null) {
    				pd.dismiss();
    			}
    	        ejecutaLogin(login);
    	    }


    	}
    	
    	private boolean ejecutaLogin(boolean respuesta)
    	{
    		
    		boolean reg = false;
    		
    		if(respuesta)
    		{
    		
    			reg=true;
            
	        SharedPreferences settings = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("namedb", editName.getText().toString());
            editor.putString("passdb", editPass.getText().toString());
            
            editor.putString("name", editName.getText().toString());
            editor.putString("pass", editPass.getText().toString());
            
            //Confirmamos el almacenamiento.
            editor.commit();
            
            //Entramos en la app
 			    		
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
            }
    		else
    		{
    			
    			Toast.makeText(getApplicationContext(), "Los datos no son correctos", Toast.LENGTH_LONG).show();
    		}
    		
    		return reg;
    		
   
    	}

}