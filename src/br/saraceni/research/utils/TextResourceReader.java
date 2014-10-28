package br.saraceni.research.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.Resources;

public class TextResourceReader {
	
	public static String readTextFileFromResource(Context context, int resourceId)
	{
		StringBuilder body = new StringBuilder();
		try
		{
			InputStream inputStream = 
					context.getResources().openRawResource(resourceId);
			BufferedReader bufferedReader =
					new BufferedReader(new InputStreamReader(inputStream));
			String nextLine;
			while( (nextLine = bufferedReader.readLine()) != null)
			{
				body.append(nextLine);
				body.append('\n');
				
			}
		}
		catch(IOException IOE)
		{
			throw new RuntimeException("Could not open resource: " + + resourceId, IOE);
		}
		catch(Resources.NotFoundException NFE)
		{
			throw new RuntimeException("Resource not found: " + resourceId, NFE);
		}
		
		return body.toString();
	}

}
