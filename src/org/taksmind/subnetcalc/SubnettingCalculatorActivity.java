/*******************************************************************************
 * Copyright (c) 2010 Tak's Mind
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 * Contributors:
 *
 *    Tak's Mind - Open Subnet Calculator
 *
 ******************************************************************************/
package org.taksmind.subnetcalc;

import org.taksmind.subnet.Subnet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SubnettingCalculatorActivity extends Activity {

	private AlertDialog priorityNoDataError;
	private AlertDialog noDataError;
	
	private Subnet subnet;
	private EditText[] data;

	private EditText ipaddress;
	private EditText maskedbits;
	private EditText subnetbits;
	private EditText subnetmask;
	private EditText totalhosts;
	private EditText totalsubnets;
	private EditText subnetaddress;
	private EditText broadcastaddress;
	private EditText range;
	
	private TextView priority;
	private int calcby;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Please enter an IP plus one other field.")
		       .setCancelable(false)
		       .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		noDataError = builder.create();

		builder = new AlertDialog.Builder(this);
		builder.setMessage("The last selected field contained no data.")
		       .setCancelable(false)
		       .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		priorityNoDataError = builder.create();
		
		subnet = new Subnet();

		ipaddress = (EditText) findViewById(R.id.ipaddr);
		ipaddress.setKeyListener(IPListener);
		
		maskedbits = (EditText) findViewById(R.id.maskbits);
		maskedbits.setOnFocusChangeListener(editableListener);
		maskedbits.setTag(getString(R.string.maskbits));
		
		subnetbits = (EditText) findViewById(R.id.subbits);
		subnetbits.setOnFocusChangeListener(editableListener);
		subnetbits.setTag(getString(R.string.subbits));
		
		subnetmask = (EditText) findViewById(R.id.submask);
		subnetmask.setOnFocusChangeListener(editableListener);
		subnetmask.setTag(getString(R.string.submask));
		subnetmask.setKeyListener(IPListener);
		
		totalhosts = (EditText) findViewById(R.id.totalhost);
		totalhosts.setOnFocusChangeListener(editableListener);
		totalhosts.setTag(getString(R.string.totalhost));
		
		totalsubnets = (EditText) findViewById(R.id.totalsubnet);
		totalsubnets.setOnFocusChangeListener(editableListener);
		totalsubnets.setTag(getString(R.string.totalsubnet));
		
		subnetaddress = (EditText) findViewById(R.id.subnetaddr);
		broadcastaddress = (EditText) findViewById(R.id.broadcastaddr);
		range = (EditText) findViewById(R.id.range);
		
		priority = (TextView) findViewById(R.id.priority);
		
		/* Array holding all edit text objects for easier manipulation */
		data = new EditText[] { 
				ipaddress, maskedbits, subnetbits, subnetmask,
				totalhosts, totalsubnets, subnetaddress, broadcastaddress,
				range 
		};

		Button submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(submitListener);

		Button reset = (Button) findViewById(R.id.reset);
		reset.setOnClickListener(resetListener);
	}
	 
	private OnFocusChangeListener editableListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			priority.setText((CharSequence) v.getTag());
			calcby = v.getId();
		}
	};
	
	private OnClickListener submitListener = new OnClickListener() {
		public void onClick(View v) {
			calculate();
		}
	};

	private void calculate() {
		if ( priority.getText() != "" ) {
			subnet.setIPAddress(ipaddress.getText().toString());
			try {
				/*If statements, duck tape it all together.*/
				if ( priority.getText().toString().equals(getString(R.string.maskbits)) ) {
					//This is so hacky I regret writing it, but I want this done NOW! xD
					subnet.setMaskedBits(Integer.parseInt(((EditText) findViewById(calcby)).getText().toString()));
				}
				else if ( priority.getText().toString().equals(getString(R.string.subbits))) {
					subnet.setSubnetBits(Integer.parseInt(((EditText) findViewById(calcby)).getText().toString()));
				}
				else if ( priority.getText().toString().equals(getString(R.string.submask)) ) {
					subnet.setSubnetMask(((EditText) findViewById(calcby)).getText().toString());
				}
				else if ( priority.getText().toString().equals(getString(R.string.totalhost)) ) {
					subnet.setTotalHosts(Integer.parseInt(((EditText) findViewById(calcby)).getText().toString()));
				}
				else if ( priority.getText().toString().equals(getString(R.string.totalsubnet)) ) {
					subnet.setTotalSubnets(Integer.parseInt(((EditText) findViewById(calcby)).getText().toString()));
				}
				
				fillOutData();
			} catch(NumberFormatException nfe) {
				priorityNoDataError.show();
				CharSequence temp = ipaddress.getText();
				for (EditText field : data) {
					field.setText("");
				}
				ipaddress.setText(temp);
				temp = null;
			}
		} else {
			noDataError.show();
		}
	}
	
	private void fillOutData() {
		maskedbits.setText((CharSequence) Integer.toString(subnet.getMaskedBits()));
		subnetbits.setText((CharSequence) Integer.toString(subnet.getSubnetBits()));
		subnetmask.setText((CharSequence) subnet.getSubnetMask());
		totalhosts.setText((CharSequence) Integer.toString(subnet.getTotalHosts()));
		totalsubnets.setText((CharSequence) Integer.toString(subnet.getTotalSubnets()));
		subnetaddress.setText((CharSequence) subnet.getSubnetAddress());
		broadcastaddress.setText((CharSequence) subnet.getBroadcastAddress());
		range.setText((CharSequence) subnet.getMinimumHostAddressRange() + "-" + subnet.getMaximumHostAddressRange());
	}

	private OnClickListener resetListener = new OnClickListener() {
		public void onClick(View v) {
			for (EditText field : data) {
				field.setText("");
			}
			priority.setText("");
		}
	};
	
	NumberKeyListener IPListener = new NumberKeyListener() {

        public int getInputType() {
        	return InputType.TYPE_CLASS_NUMBER;
        }

        @Override
        protected char[] getAcceptedChars() {
        	return new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.' };
        }
    };
}