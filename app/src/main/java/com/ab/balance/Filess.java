package com.ab.balance;

import android.content.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class Filess
{

	public static String read(Context ctx, String fileName, int lineNumber, boolean fromAssets)
	{
		String res="";
		int count=0;
		//Toast.makeText(ctx,"strtd",Toast.LENGTH_LONG).show();
		if (fromAssets)
		{
			BufferedReader reader = null;
			try
			{
				reader = new BufferedReader(
					new InputStreamReader(ctx.getAssets().open("1arabic.txt")));

				// do reading, usually loop until end of file reading  
				String mLine;
				while ((mLine = reader.readLine()) != null)
				{
					//process line
					count++;
					if (count == lineNumber)
					{
						res = reader.readLine();
						break;
					}	
				}
			}
			catch (IOException e)
			{
				//log the exception
			}
			finally
			{
				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (IOException e)
					{
						//log the exception
					}
				}
			}
		}
		return res;
	}

}
