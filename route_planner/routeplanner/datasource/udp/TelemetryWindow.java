package routeplanner.datasource.udp;

import javax.swing.JFrame; 
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TelemetryWindow extends JFrame
{
    
    JLabel timeLabel;
    JLabel hostLabel;
    JLabel ageLabel;
    JLabel sequenceLabel;
    
    JLabel latLabel;
    JLabel lonLabel;
    JLabel speedLabel;
    JLabel headingLabel;
    JLabel headingErrorLabel;
    JLabel xteLabel;
    JLabel automodeLabel;
    
    JLabel wpNumLabel;
    JLabel wpDistLabel;
    JLabel wpHeadingLabel;
    JLabel wpLatLabel;
    JLabel wpLonLabel;
    JLabel leftSpeedLabel;
    JLabel rightSpeedLabel;
    
    JLabel currentLabel[] = new JLabel[5];
    JLabel voltageLabel;
    JLabel warningLabel;
    
    UdpDataSource ds;
    
    //calculates difference between two headings taking wrap around into account
    private int getHdgDiff(int heading1,int heading2)
    {
        int result;
	
        result = heading1-heading2;
	    
        if(result<-180)
        {
            result = 360 + result;
            return result;
        } 
        if(result>180)
        {
            result = 0 - (360-result);
        }
        return result;
    }

        
    public TelemetryWindow(UdpDataSource ds)
    {
        JPanel leftPanel = new JPanel();
        JPanel centrePanel = new JPanel();
        JPanel rightPanel = new JPanel();
        Font textFont = new Font("SansSerif",Font.BOLD,16);

        leftPanel.setLayout(new GridLayout(10,1));
        centrePanel.setLayout(new GridLayout(10,1));
        rightPanel.setLayout(new GridLayout(10,1));

        leftPanel.setFont(textFont);
        centrePanel.setFont(textFont);
        rightPanel.setFont(textFont);

        timeLabel = new JLabel();
        ageLabel = new JLabel();
        sequenceLabel = new JLabel();
        latLabel = new JLabel();
        lonLabel = new JLabel();
        speedLabel = new JLabel();
        headingLabel = new JLabel();
        headingErrorLabel = new JLabel();
        xteLabel = new JLabel();
        automodeLabel = new JLabel();
        leftSpeedLabel = new JLabel();
        rightSpeedLabel = new JLabel();

        timeLabel.setFont(textFont);
        timeLabel.setForeground(Color.BLACK);
        
        ageLabel.setFont(textFont);
        ageLabel.setForeground(Color.BLACK);
        
        sequenceLabel.setFont(textFont);
        sequenceLabel.setForeground(Color.BLACK);
        
        latLabel.setFont(textFont);
        latLabel.setForeground(Color.BLACK);
        
        lonLabel.setFont(textFont);
        lonLabel.setForeground(Color.BLACK);
        
        speedLabel.setFont(textFont);
        speedLabel.setForeground(Color.BLACK);
        
        headingLabel.setFont(textFont);
        headingLabel.setForeground(Color.BLACK);
        
        headingErrorLabel.setFont(textFont);
        headingErrorLabel.setForeground(Color.BLACK);
       
        xteLabel.setFont(textFont);
        xteLabel.setForeground(Color.BLACK);

        leftSpeedLabel.setFont(textFont);
        leftSpeedLabel.setForeground(Color.BLACK);
        
        rightSpeedLabel.setFont(textFont);
        rightSpeedLabel.setForeground(Color.BLACK);

        automodeLabel.setFont(textFont);
        automodeLabel.setForeground(Color.BLACK);


        leftPanel.add(timeLabel);
        leftPanel.add(ageLabel);
        leftPanel.add(sequenceLabel);
        rightPanel.add(latLabel);
        rightPanel.add(lonLabel);
        leftPanel.add(speedLabel);
        leftPanel.add(headingLabel);
        leftPanel.add(headingErrorLabel);
        leftPanel.add(xteLabel);
        leftPanel.add(leftSpeedLabel);
        leftPanel.add(rightSpeedLabel);
        leftPanel.add(automodeLabel);

        wpNumLabel = new JLabel();
        wpDistLabel = new JLabel();
        wpHeadingLabel = new JLabel();
        wpLatLabel = new JLabel();
        wpLonLabel = new JLabel();


        for(int i=0;i<currentLabel.length;i++)
        {
          currentLabel[i] = new JLabel();
          currentLabel[i].setFont(textFont);
          currentLabel[i].setForeground(Color.BLACK);
        }
        voltageLabel = new JLabel();
        warningLabel = new JLabel();

        voltageLabel.setFont(textFont);
        voltageLabel.setForeground(Color.BLACK);

        warningLabel.setFont(textFont);
        warningLabel.setForeground(Color.RED);

        
        wpNumLabel.setFont(textFont);
        wpNumLabel.setForeground(Color.BLACK);
        
        wpDistLabel.setFont(textFont);
        wpDistLabel.setForeground(Color.BLACK);
        
        wpHeadingLabel.setFont(textFont);
        wpHeadingLabel.setForeground(Color.BLACK);
        
        wpLatLabel.setFont(textFont);
        wpLatLabel.setForeground(Color.BLACK);
        
        wpLonLabel.setFont(textFont);
        wpLonLabel.setForeground(Color.BLACK);
        

        
        rightPanel.add(wpNumLabel);
        rightPanel.add(wpDistLabel);
        rightPanel.add(wpHeadingLabel);
        rightPanel.add(wpLatLabel);
        rightPanel.add(wpLonLabel);

        for(int i=0;i<currentLabel.length;i++)
        {
          centrePanel.add(currentLabel[i]);
        }
        centrePanel.add(voltageLabel);
        centrePanel.add(warningLabel);
        
        this.setBounds(0,0,800,300);
        this.setTitle("Telemetry");
        this.setLayout(new GridLayout(1,3));
        this.ds = ds;

        this.add(leftPanel);       
        this.add(rightPanel);
        this.add(centrePanel);

        /*this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
            }
        });*/

        this.setVisible(true);
    }

    public void updateData()
    {
        GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(ds.getTime()*1000);

        timeLabel.setText("Time: " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND) + " " + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DATE));
        //timeLabel.setText("Time: " + ds.getTime());
        ageLabel.setText("Message Age: " + ((System.currentTimeMillis()/1000)-ds.getTime()));
        sequenceLabel.setText("Sequence Num: " + ds.getSequenceNumber());


        latLabel.setText("Lat: " + ds.getLat());
        lonLabel.setText("Lon: " + ds.getLon());
        speedLabel.setText("Speed: " + ds.getSpeed());
        headingLabel.setText("Heading: "+ ds.getHeading());
        headingErrorLabel.setText("Heading Error: "+ getHdgDiff((int)ds.getHeading(),ds.getWpHeading()));
        xteLabel.setText("Cross track err: " + ds.getXte());
        
        wpNumLabel.setText("Waypoint Number: " + ds.getWpnum());
        wpDistLabel.setText("Waypoint Distance: " + ds.getDistance());
        wpHeadingLabel.setText("Waypoint Heading: " + ds.getWpHeading());
        wpLatLabel.setText("Waypoint Lat: " + ds.getWpLat());
        wpLonLabel.setText("Waypoint Lon: " + ds.getWpLon());

        leftSpeedLabel.setText("Left Motor: " + ds.getLeftSpeed());
        rightSpeedLabel.setText("Right Motor: " + ds.getRightSpeed());

        switch (ds.getAutomode())
        {
            case 0:
                automodeLabel.setText("Mode: Manual");
                break;
            case 1:
                automodeLabel.setText("Mode: Autonomous");
                break;
            case 2:
                automodeLabel.setText("Mode: Heading Hold");
                break;
            default:
                automodeLabel.setText("Mode: " + ds.getAutomode());
                break;  
        }

        double currents[] = ds.getCurrent();

        for(int i=0;i<currentLabel.length;i++)
        {         
          currentLabel[i].setText("Current Sensor " + i + ": " + currents[i]);
        }

        voltageLabel.setText("Battery Voltage: " + ds.getVoltage());

        if(ds.getWarnings()==1)
        {
          warningLabel.setText("WARNING: GPS ERROR");
        }
        else if(ds.getWarnings()==2)
        {
          warningLabel.setText("WARNING: COMPASS ERROR");
        }
        else if(ds.getWarnings()==3)
        {
          warningLabel.setText("WARNING: GPS AND COMPASS ERROR");
        }
        else
        {
          warningLabel.setText("");
        }

        
    }
}