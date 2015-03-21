/*
*The MIT License (MIT)
*
*Copyright (c) 2015 Michael Gunderson
*
*Permission is hereby granted, free of charge, to any person obtaining a copy
*of this software and associated documentation files (the "Software"), to deal
*in the Software without restriction, including without limitation the rights
*to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
*copies of the Software, and to permit persons to whom the Software is
*furnished to do so, subject to the following conditions:
*
*The above copyright notice and this permission notice shall be included in
*all copies or substantial portions of the Software.
*
*THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
*IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
*FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
*AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
*LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
*OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
*THE SOFTWARE.
 */
package ioio.examples.simple;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class IOIOSimpleApp extends IOIOActivity {
	private TextView textViewHeader;
	private TextView dutyCycleLabel;
	private TextView dutyCycleValue;
	private TextView servoDegreesLabel;
	private TextView servoDegreesValue;
	private SeekBar seekBar;
	float servo_duty = .05f; // Servo Duty Cycle, range 0.01 - 0.09
	int servo_degrees = 90; // Servo Angle in degrees, range: 0 - 180

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		textViewHeader = (TextView) findViewById(R.id.title);
		dutyCycleLabel = (TextView) findViewById(R.id.dutyCycleLabel);
		dutyCycleValue = (TextView) findViewById(R.id.dutyCycleValue);
		servoDegreesLabel = (TextView) findViewById(R.id.servoDegLabel);
		servoDegreesValue = (TextView) findViewById(R.id.servoDegValue);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setMax(180);
		seekBar.setProgress(90);
	}

	class Looper extends BaseIOIOLooper {
		PwmOutput servo;  // Declaring Servo PWM output on the IOIO
		int servoPin = 5; // Pin for the Servo PWM signal

		@Override
		public void setup() throws ConnectionLostException {
			// Initialize PWM pin for Servo and set initial duty cycle
			servo = ioio_.openPwmOutput(servoPin, 50); // Pin 5, 50Hz (period = 20ms)
			servo.setDutyCycle(servo_duty); // Sets angle of Servo
		}

		@Override
		public void loop() throws ConnectionLostException, InterruptedException {
			try 
			  {
				// Set servo duty cycle between .01 and .09 (1% to 9%) 
				//  based on where you touched the screen
				servo_duty = (float) (.01+.08*((float)seekBar.getProgress()/seekBar.getMax()));				
			    // Set new Servo angle based on duty cycle set by cursor position
			    servo.setDutyCycle(servo_duty);
			    setDutyCycle(servo_duty);
			    setServoDegrees(seekBar.getProgress());
			    // Don't call this loop again for 100 milliseconds
			    Thread.sleep(100);
			  }
			  catch (InterruptedException e) 
			  {
			  }
		}
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
	
	private void setDutyCycle(final Float value) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dutyCycleValue.setText(value.toString());
			}
		});
	}
	
	private void setServoDegrees(final Integer value) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Calculate rough servo position based on duty cycle
//				servo_degrees = round(map(servo_duty,0,.1,0,180));
				servoDegreesValue.setText(value);
			}
		});
	}
}