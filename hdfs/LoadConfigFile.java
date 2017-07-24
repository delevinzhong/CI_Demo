package com.anz.td.ebd.clr.hdfs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**Class Name: LoadConfigFile
 * Description: Read all the properties from a configuration file and return its value
 * Date          Comments      UpdatedBy
 * 26/06/2017    Creation      Michael Wu
 */
public class LoadConfigFile
{

	private String configFilePath;  //the full path of the config file
	private Configuration conf;  //the Hadoop configurations
	private static FileSystem fs;
	
	/**
	 * Construct the class by passing the configuration file path and Hadoop configuration in.
	 * @param configFilePath is the full path of the configuration path
	 * @param conf is the Hadoop configuration
	 * @throws IOException 
	 */
	public LoadConfigFile(String configFilePath, Configuration conf, String userName, String keyTabPath) throws IOException
	{
		this.configFilePath = configFilePath;
		this.conf = conf;
		if(userName.trim().length() > 0)
		{
			UserGroupInformation.setConfiguration(conf);
			UserGroupInformation.loginUserFromKeytab(userName, keyTabPath);
			UserGroupInformation.getLoginUser();
		}
		this.fs = FileSystem.get(URI.create(configFilePath),conf);
	}
	
	
	/**
	 * Read the properties from the configuration file and return the value by a Map
	 * @return Map<String, String>, return the map contains all of the properties from the configuration file. The key of the map is the property name, 
	 * the value of the map is the property value.
	 * @throws IOException, FileNotFoundException.
	 */
	public Map<String, String> getConfigItems() throws IOException, FileNotFoundException
	{

		//if the config file didn't exist, throw a FileNotFoundException
		if (!fs.exists(new Path(configFilePath)))
		{
			throw new FileNotFoundException("Cannot find the file at " + configFilePath);
		}
		FSDataInputStream in = fs.open(new Path(configFilePath));  //generate a FSDataInputStream instance, open the config file to read
		InputStreamReader inStreamReader = new InputStreamReader(in);  //generate a InputStreamReader to wrap the FSDataInputStream.
		BufferedReader bufReader = new BufferedReader(inStreamReader); //wrap the InputStreamReader into a BufferedReader instance to read the config file

		Map<String, String> configItems = new HashMap<String, String>();  //generate a Map to store the config properties from the config file
		
		String line = null; //create an empty line to read the config file line by line
		while((line = bufReader.readLine()) != null)  //read a line from config file
		{
			String k = "";
			String v = "";
			//the format of the config file is property = value. split a line by "="
			//if there is more than 1 "=" in a line, only the 1st "=" is the delimiter
			String[] kv =  line.split("=", 2);
			if(kv.length > 1)
			{
			    
			    k = kv[0].trim(); //put the property to the key of the Map
			    v = kv[1].trim();  //put the value to the value of the Map
			}
			else
			{
				k = kv[0].trim(); //put the property to the key of the Map
			}
			
			configItems.put(k, v);  //put to config items map
		}
		
		fs.close();
		
		return configItems;

	}
	
	
	/**
	 * read the config file and convert the content of that file to a string and return the string
	 * @return the content string of the file
	 * @throws IOException
	 */
	public String configFileToString() throws IOException
	{
//		FileSystem fs = FileSystem.get(URI.create(configFilePath), conf);  //generate a FileSystem instance
		String configFileContent = "";
		BufferedReader reader = null;
		
		try
		{
			if (!fs.exists(new Path(configFilePath)))
			{
				throw new FileNotFoundException("Cannot find the file at " + configFilePath);
			}
			
			FSDataInputStream in = fs.open(new Path(configFilePath));  //generate a FSDataInputStream instance, open the config file to read
			InputStreamReader inStreamReader = new InputStreamReader(in);  //generate a InputStreamReader to wrap the FSDataInputStream.
			reader = new BufferedReader(inStreamReader); //wrap the InputStreamReader into a BufferedReader instance to read the config file
			String tempString;
			while((tempString = reader.readLine()) != null)
			{
				configFileContent += tempString;
			}
			
			reader.close();
			
			return configFileContent;
			
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e2)
				{
//					e2.printStackTrace();
					throw e2;
				}
			}
		}
	}
	
	
	/**
	 * read json string to get the specified array section
	 * the format of json string like {xxx:[], yyy:[]}
	 * @param jsonString
	 * @param itemName
	 * @return
	 */
	public JsonArray getJSONConfigItem(String jsonString, String itemName)
	{
		JsonObject rootObject = new JsonParser().parse(jsonString).getAsJsonObject();
//		JSONObject rootObject = new JSONObject(jsonString);
//		JSONArray itemArray = rootObject.getJSONArray(itemName);
		JsonArray itemArray = rootObject.get(itemName).getAsJsonArray();
		
		return itemArray;
	}
	
	
	/**
	 * read the json string to get the string property in under the root object
	 * @param jsonString
	 * @param itemName
	 * @return
	 */
	public String getJSONStringConfigItem(String jsonString, String itemName)
	{
//		JSONObject rootObject = new JSONObject(jsonString);
		JsonObject rootObject = new JsonParser().parse(jsonString).getAsJsonObject();
		String configItem = rootObject.get(itemName).getAsString();
		
		return configItem;
	}
	

}
