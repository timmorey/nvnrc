package nvn.nvnrc;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity 
	extends Activity
	implements SensorEventListener {
	
    private SensorManager m_SensorManager;
    private Sensor m_Accelerometer;
    private Sensor m_Gyroscope;
    
    private TextView m_AXCurTV;
    private TextView m_AXMinTV;
    private TextView m_AXMaxTV;
    private TextView m_AXSmoothedTV;
    
    private TextView m_AYCurTV;
    private TextView m_AYMinTV;
    private TextView m_AYMaxTV;
    private TextView m_AYSmoothedTV;
    
    private TextView m_AZCurTV;
    private TextView m_AZMinTV;
    private TextView m_AZMaxTV;
    private TextView m_AZSmoothedTV;
    
    private TextView m_RXCurTV;
    private TextView m_RXMinTV;
    private TextView m_RXMaxTV;
    private TextView m_RXSmoothedTV;
    
    private TextView m_RYCurTV;
    private TextView m_RYMinTV;
    private TextView m_RYMaxTV;
    private TextView m_RYSmoothedTV;
    
    private TextView m_RZCurTV;
    private TextView m_RZMinTV;
    private TextView m_RZMaxTV;
    private TextView m_RZSmoothedTV;
    
    private float m_AX, m_AY, m_AZ;
    private float m_MinAX, m_MinAY, m_MinAZ;
    private float m_MaxAX, m_MaxAY, m_MaxAZ;
    private float m_RX, m_RY, m_RZ;
    private float m_MinRX, m_MinRY, m_MinRZ;
    private float m_MaxRX, m_MaxRY, m_MaxRZ;
    
    private SendTask m_SendTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        m_SensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        m_Accelerometer = m_SensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        m_Gyroscope = m_SensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        
        setContentView(R.layout.activity_main);
        
        m_AXCurTV = (TextView)findViewById(R.id.AXCurTextView);
        m_AXMinTV = (TextView)findViewById(R.id.AXMinTextView);
        m_AXMaxTV = (TextView)findViewById(R.id.AXMaxTextView);
        m_AXSmoothedTV = (TextView)findViewById(R.id.AXSmoothedTextView);
        
        m_AYCurTV = (TextView)findViewById(R.id.AYCurTextView);
        m_AYMinTV = (TextView)findViewById(R.id.AYMinTextView);
        m_AYMaxTV = (TextView)findViewById(R.id.AYMaxTextView);
        m_AYSmoothedTV = (TextView)findViewById(R.id.AYSmoothedTextView);
        
        m_AZCurTV = (TextView)findViewById(R.id.AZCurTextView);
        m_AZMinTV = (TextView)findViewById(R.id.AZMinTextView);
        m_AZMaxTV = (TextView)findViewById(R.id.AZMaxTextView);
        m_AZSmoothedTV = (TextView)findViewById(R.id.AZSmoothedTextView);
        
        m_RXCurTV = (TextView)findViewById(R.id.RXCurTextView);
        m_RXMinTV = (TextView)findViewById(R.id.RXMinTextView);
        m_RXMaxTV = (TextView)findViewById(R.id.RXMaxTextView);
        m_RXSmoothedTV = (TextView)findViewById(R.id.RXSmoothedTextView);
        
        m_RYCurTV = (TextView)findViewById(R.id.RYCurTextView);
        m_RYMinTV = (TextView)findViewById(R.id.RYMinTextView);
        m_RYMaxTV = (TextView)findViewById(R.id.RYMaxTextView);
        m_RYSmoothedTV = (TextView)findViewById(R.id.RYSmoothedTextView);
        
        m_RZCurTV = (TextView)findViewById(R.id.RZCurTextView);
        m_RZMinTV = (TextView)findViewById(R.id.RZMinTextView);
        m_RZMaxTV = (TextView)findViewById(R.id.RZMaxTextView);
        m_RZSmoothedTV = (TextView)findViewById(R.id.RZSmoothedTextView);
        
        m_MinAX = m_MinAY = m_MinAZ = Float.MAX_VALUE;
        m_MaxAX = m_MaxAY = m_MaxAZ = Float.MIN_VALUE;
        m_MinRX = m_MinRY = m_MinRZ = Float.MAX_VALUE;
        m_MaxRX = m_MaxRY = m_MaxRZ = Float.MIN_VALUE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        m_SensorManager.unregisterListener(this);
        
        if(m_SendTask != null)
        {
        	m_SendTask.cancel(false);
        	m_SendTask = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        m_SensorManager.registerListener(this, m_Accelerometer, SensorManager.SENSOR_DELAY_UI);
        m_SensorManager.registerListener(this, m_Gyroscope, SensorManager.SENSOR_DELAY_UI);
        
        m_SendTask = new SendTask();
        m_SendTask.execute(new Object[1]);
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
    	
    	if(Sensor.TYPE_LINEAR_ACCELERATION == event.sensor.getType()) {
    		m_AX = event.values[0];
    		m_AY = event.values[1];
    		m_AZ = event.values[2];

    		if(m_AX < m_MinAX) m_MinAX = m_AX;
    		if(m_AY < m_MinAY) m_MinAY = m_AY;
    		if(m_AZ < m_MinAZ) m_MinAZ = m_AZ;
    		if(m_AX > m_MaxAX) m_MaxAX = m_AX;
    		if(m_AY > m_MaxAY) m_MaxAY = m_AY;
    		if(m_AZ > m_MaxAZ) m_MaxAZ = m_AZ;
        	
        	m_AXCurTV.setText(String.format("%.2f", m_AX));
        	m_AXMinTV.setText(String.format("%.2f", m_MinAX));
        	m_AXMaxTV.setText(String.format("%.2f", m_MaxAX));
        	
        	m_AYCurTV.setText(String.format("%.2f", m_AY));
        	m_AYMinTV.setText(String.format("%.2f", m_MinAY));
        	m_AYMaxTV.setText(String.format("%.2f", m_MaxAY));
        	
        	m_AZCurTV.setText(String.format("%.2f", m_AZ));
        	m_AZMinTV.setText(String.format("%.2f", m_MinAZ));
        	m_AZMaxTV.setText(String.format("%.2f", m_MaxAZ));
	        
    	} else if(Sensor.TYPE_GYROSCOPE == event.sensor.getType()) {
    		m_RX = event.values[0];
    		m_RY = event.values[1];
    		m_RZ = event.values[2];

    		if(m_RX < m_MinRX) m_MinRX = m_RX;
    		if(m_RY < m_MinRY) m_MinRY = m_RY;
    		if(m_RZ < m_MinRZ) m_MinRZ = m_RZ;
    		if(m_RX > m_MaxRX) m_MaxRX = m_RX;
    		if(m_RY > m_MaxRY) m_MaxRY = m_RY;
    		if(m_RZ > m_MaxRZ) m_MaxRZ = m_RZ;
        	
        	m_RXCurTV.setText(String.format("%.2f", m_RX));
        	m_RXMinTV.setText(String.format("%.2f", m_MinRX));
        	m_RXMaxTV.setText(String.format("%.2f", m_MaxRX));
        	
        	m_RYCurTV.setText(String.format("%.2f", m_RY));
        	m_RYMinTV.setText(String.format("%.2f", m_MinRY));
        	m_RYMaxTV.setText(String.format("%.2f", m_MaxRY));
        	
        	m_RZCurTV.setText(String.format("%.2f", m_RZ));
        	m_RZMinTV.setText(String.format("%.2f", m_MinRZ));
        	m_RZMaxTV.setText(String.format("%.2f", m_MaxRZ));
    	}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    
    private class SendTask extends AsyncTask {

        @Override
		protected Object doInBackground(Object... arg0) {

        	Socket socket = null;
        	DataOutputStream socketWriter = null;
        	
	        while(! this.isCancelled()) {
	        	
	        	try {
		        	socket = new Socket("tessy.umcs.maine.edu", 16661);
		        	socketWriter = new DataOutputStream(socket.getOutputStream());
		        	
	        		String msg = String.format("%f,%f,%f,%f,%f,%f",
	                                           m_AX, m_AY, m_AZ, m_RX, m_RY, m_RZ);
	        		byte[] b = msg.getBytes("US-ASCII");
	        		
	    			socketWriter.writeInt(b.length);
	    			socketWriter.write(b);
	    			socketWriter.flush();
	    			
	    			socketWriter.close();
	    			socketWriter = null;
	    			socket.close();
	    			socket = null;
	    			
	        	} catch(Exception ex) {
	        		break;
	        	}
	        }

	        return null;
		}
    	
    }
}
