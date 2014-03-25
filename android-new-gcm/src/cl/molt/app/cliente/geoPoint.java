package cl.molt.app.cliente;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class geoPoint implements KvmSerializable {
	
	
	public double lat;
	public double lng;
	public String hora;
	
	public geoPoint()
	{
		lat = 0.0;
		lng = 0.0;
		hora = "00:00:00";
	}
	
	public geoPoint(double lat, double lng, String hora)
	{
		this.lat = lat;
		this.lng = lng;
		this.hora = hora;
	}
	
	@Override
	public Object getProperty(int arg0) {

		switch(arg0)
        {
        case 0:
            return lat;
        case 1:
            return lng;
        case 2:
            return hora;
        }
		
		return null;
	}
	
	@Override
	public int getPropertyCount() {
		return 3;
	}
	
	@Override
	public void getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
		switch(ind)
        {
	        case 0:
	            info.type = PropertyInfo.LONG_CLASS;
	            info.name = "Latitud";
	            break;
	        case 1:
	            info.type = PropertyInfo.LONG_CLASS;
	            info.name = "Longitud";
	            break;
	        case 2:
	            info.type = PropertyInfo.STRING_CLASS;
	            info.name = "Hora";
	            break;
	        default:break;
        }
	}
	
	@Override
	public void setProperty(int ind, Object val) {
		switch(ind)
        {
	        case 0:
	            lat = Double.parseDouble(val.toString());
	            break;
	        case 1:
	            lng = Double.parseDouble(val.toString());
	            break;
	        case 2:
	            hora = val.toString();
	            break;
	        default:
	            break;
        }
	}
}
