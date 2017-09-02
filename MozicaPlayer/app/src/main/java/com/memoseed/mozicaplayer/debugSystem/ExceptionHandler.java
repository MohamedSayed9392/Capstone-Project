package com.memoseed.mozicaplayer.debugSystem;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.analytics.StandardExceptionParser;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler implements
		java.lang.Thread.UncaughtExceptionHandler {
	private final Context myContext;
	private final String LINE_SEPARATOR = "\n";

	public ExceptionHandler(Context context) {
		myContext = context;
	}

	public void uncaughtException(Thread thread, Throwable exception)
	{
		Log.d("EXCEPTION HANDLER", "---> handling");
		
		
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		StringBuilder errorReport = new StringBuilder();
		errorReport.append("************ CAUSE OF ERROR ************\n\n");
		errorReport.append(stackTrace.toString());

		
		
		errorReport.append("\n************ DEVICE INFORMATION ***********\n");
		errorReport.append("Brand: ");
		errorReport.append(Build.BRAND);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Device: ");
		errorReport.append(Build.DEVICE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Model: ");
		errorReport.append(Build.MODEL);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Id: ");
		errorReport.append(Build.ID);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Product: ");
		errorReport.append(Build.PRODUCT);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("\n************ FIRMWARE ************\n");
		errorReport.append("SDK: ");
		errorReport.append(Build.VERSION.SDK);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Release: ");
		errorReport.append(Build.VERSION.RELEASE);
		errorReport.append(LINE_SEPARATOR);
		errorReport.append("Incremental: ");
		errorReport.append(Build.VERSION.INCREMENTAL);
		errorReport.append(LINE_SEPARATOR);

//		Toast.makeText(myContext, ":D", Toast.LENGTH_LONG).show();
		Log.d("ExcepionHandler", "----> "+errorReport.toString());
		
		String strException = (new StandardExceptionParser(myContext, null).getDescription(Thread.currentThread().getName(), exception));
		
		Log.d("ExcepionHandler", "----> "+strException);



    
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
		
		
	}



}