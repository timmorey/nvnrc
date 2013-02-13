package nvn.nvnrc;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

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
    
    private int m_SampleSize = 100;
    private int m_SmoothingSize = 7;
    private int m_CurA = 0;
    private int m_CurR = 0;
    private float[] m_AX = new float[m_SampleSize];
    private float[] m_AY = new float[m_SampleSize];
    private float[] m_AZ = new float[m_SampleSize];
    private float[] m_RX = new float[m_SampleSize];
    private float[] m_RY = new float[m_SampleSize];
    private float[] m_RZ = new float[m_SampleSize];

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        m_SensorManager.registerListener(this, m_Accelerometer, SensorManager.SENSOR_DELAY_UI);
        m_SensorManager.registerListener(this, m_Gyroscope, SensorManager.SENSOR_DELAY_UI);
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
    	
    	if(Sensor.TYPE_LINEAR_ACCELERATION == event.sensor.getType()) {
    		m_AX[m_CurA] = event.values[0];
    		m_AY[m_CurA] = event.values[1];
    		m_AZ[m_CurA] = event.values[2];
    		updateA();
	        
    	} else if(Sensor.TYPE_GYROSCOPE == event.sensor.getType()) {
    		m_RX[m_CurR] = event.values[0];
    		m_RY[m_CurR] = event.values[1];
    		m_RZ[m_CurR] = event.values[2];
    		updateR();
    	}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    
    protected void updateA() {
    	float curx, minx, maxx, smoothedx;
    	float cury, miny, maxy, smoothedy;
    	float curz, minz, maxz, smoothedz;
    	
    	curx = m_AX[m_CurA];
    	cury = m_AY[m_CurA];
    	curz = m_AZ[m_CurA];
    	minx = Float.MAX_VALUE;
    	miny = Float.MAX_VALUE;
    	minz = Float.MAX_VALUE;
    	maxx = Float.MIN_VALUE;
    	maxy = Float.MIN_VALUE;
    	maxz = Float.MIN_VALUE;
    	for(int i = 0; i < m_SampleSize; i++) {
    		if(m_AX[i] < minx) minx = m_AX[i];
    		if(m_AY[i] < miny) miny = m_AY[i];
    		if(m_AZ[i] < minz) minz = m_AZ[i];
    		if(m_AX[i] > maxx) maxx = m_AX[i];
    		if(m_AY[i] > maxy) maxy = m_AY[i];
    		if(m_AZ[i] > maxz) maxz = m_AZ[i];    		
    	}
    	
    	smoothedx = 0.0f;
    	smoothedy = 0.0f;
    	smoothedz = 0.0f;
    	for(int i = 0; i < m_SmoothingSize; i++) {
    		int pos = m_CurA - i;
    		if(pos < 0) pos += m_SampleSize;
    		smoothedx += m_AX[pos];
    		smoothedy += m_AY[pos];
    		smoothedz += m_AZ[pos];
    	}
    	
    	smoothedx /= m_SmoothingSize;
    	smoothedy /= m_SmoothingSize;
    	smoothedz /= m_SmoothingSize;
    	
    	m_AXCurTV.setText(String.format("%.2f", curx));
    	m_AXMinTV.setText(String.format("%.2f", minx));
    	m_AXMaxTV.setText(String.format("%.2f", maxx));
    	m_AXSmoothedTV.setText(String.format("%.2f", smoothedx));
    	
    	m_AYCurTV.setText(String.format("%.2f", cury));
    	m_AYMinTV.setText(String.format("%.2f", miny));
    	m_AYMaxTV.setText(String.format("%.2f", maxy));
    	m_AYSmoothedTV.setText(String.format("%.2f", smoothedy));
    	
    	m_AZCurTV.setText(String.format("%.2f", curz));
    	m_AZMinTV.setText(String.format("%.2f", minz));
    	m_AZMaxTV.setText(String.format("%.2f", maxz));
    	m_AZSmoothedTV.setText(String.format("%.2f", smoothedz));
    	
		m_CurA = (m_CurA + 1)  % m_SampleSize;
    }
    
    protected void updateR() {
    	float curx, minx, maxx, smoothedx;
    	float cury, miny, maxy, smoothedy;
    	float curz, minz, maxz, smoothedz;
    	
    	curx = m_RX[m_CurR];
    	cury = m_RY[m_CurR];
    	curz = m_RZ[m_CurR];
    	minx = Float.MAX_VALUE;
    	miny = Float.MAX_VALUE;
    	minz = Float.MAX_VALUE;
    	maxx = Float.MIN_VALUE;
    	maxy = Float.MIN_VALUE;
    	maxz = Float.MIN_VALUE;
    	for(int i = 0; i < m_SampleSize; i++) {
    		if(m_RX[i] < minx) minx = m_RX[i];
    		if(m_RY[i] < miny) miny = m_RY[i];
    		if(m_RZ[i] < minz) minz = m_RZ[i];
    		if(m_RX[i] > maxx) maxx = m_RX[i];
    		if(m_RY[i] > maxy) maxy = m_RY[i];
    		if(m_RZ[i] > maxz) maxz = m_RZ[i];    		
    	}
    	
    	smoothedx = 0.0f;
    	smoothedy = 0.0f;
    	smoothedz = 0.0f;
    	for(int i = 0; i < m_SmoothingSize; i++) {
    		int pos = m_CurR - i;
    		if(pos < 0) pos += m_SampleSize;
    		smoothedx += m_RX[pos];
    		smoothedy += m_RY[pos];
    		smoothedz += m_RZ[pos];
    	}
    	
    	smoothedx /= m_SmoothingSize;
    	smoothedy /= m_SmoothingSize;
    	smoothedz /= m_SmoothingSize;
    	
    	m_RXCurTV.setText(String.format("%.2f", curx));
    	m_RXMinTV.setText(String.format("%.2f", minx));
    	m_RXMaxTV.setText(String.format("%.2f", maxx));
    	m_RXSmoothedTV.setText(String.format("%.2f", smoothedx));
    	
    	m_RYCurTV.setText(String.format("%.2f", cury));
    	m_RYMinTV.setText(String.format("%.2f", miny));
    	m_RYMaxTV.setText(String.format("%.2f", maxy));
    	m_RYSmoothedTV.setText(String.format("%.2f", smoothedy));
    	
    	m_RZCurTV.setText(String.format("%.2f", curz));
    	m_RZMinTV.setText(String.format("%.2f", minz));
    	m_RZMaxTV.setText(String.format("%.2f", maxz));
    	m_RZSmoothedTV.setText(String.format("%.2f", smoothedz));

		m_CurR = (m_CurR + 1) % m_SampleSize;
    }
}
