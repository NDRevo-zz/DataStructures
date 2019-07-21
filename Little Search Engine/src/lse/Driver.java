package lse;

import java.io.*;
import java.util.*;

public class Driver 
{
	static Scanner sc = new Scanner(System.in);
	
	static String getOption() 
	{
		System.out.print("getKeyWord(): ");
		String response = sc.next();
		return response;
	}
	
	public static void main(String args[])
	{
		LittleSearchEngine lse = new LittleSearchEngine();
		
		try
		{
			lse.makeIndex("docs.txt", "noisewords.txt");
		} 
		catch (FileNotFoundException e)
		{
		}		

		System.out.println(lse.top5search("infinite", "world"));
	}
}