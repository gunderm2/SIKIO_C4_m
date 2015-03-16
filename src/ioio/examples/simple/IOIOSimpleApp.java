/*
 * Copyright 2015 Michael Gunderson. All rights reserved.
 *
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ARSHAN POURSOHI OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied.
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