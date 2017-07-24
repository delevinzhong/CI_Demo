package com.anz.td.ebd.clr.hdfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**Class Name: GetFileInfo
 * Description: Get source file information from NFS
 * Date          Comments      UpdatedBy
 * 26/06/2017    Creation      Michael Wu
 */
public class GetFileInfo
{
	private String kUserName = "";
	private String keyTab = "";
	
	public GetFileInfo(String userName, String keyTab)
	{
		kUserName = userName.trim();
		keyTab = keyTab.trim();
	}
	

/// get a list of source file from NFS
	public String[] GetFileList(String srcpath) throws IOException
	{
//		try
//		{
			Configuration conf = new Configuration();
			Path path = new Path(srcpath);
			FileSystem fs = path.getFileSystem(conf);
			List<String> files = new ArrayList<String>();
			if(fs.exists(path) && fs.isDirectory(path))
			{
				for(FileStatus status : fs.listStatus(path))
				{
					files.add(status.getPath().toString());
				}
			}
			return files.toArray(new String[]{});
			
//		}
//		catch (Exception e)
//		{
//			// TODO: handle exception
//		}
//		return null;
	}
	
	
	private static List<String> allFileNames = new ArrayList<String>();
	
	/// Get a list of tag files from NFS
	public List<String> getAllTagFiles(String srcpath, Configuration conf) throws IOException
	{
		Path path = new Path(srcpath);
		FileSystem fs = path.getFileSystem(conf);
//		allFileNames = new ArrayList<String>();
		
		for(FileStatus status : fs.listStatus(path))
		{
			if(fs.isDirectory(status.getPath()))
			{
				getAllTagFiles(status.getPath().toString(), conf);
			}
			else
			{
				String filename = status.getPath().toString();
				if(filename.toLowerCase().contains(".tag"))
				{
					allFileNames.add(filename);
				}
			}
		}
		
		return allFileNames;
	}
	
	/// Get a list of data files from NFS
	public String getDataFileName(String tagFilePath, Configuration conf) throws IOException
	{
		String dataFileName = "";
		String tagFileName = "";
		String folderPath;
		if(tagFilePath.toLowerCase().contains(".tag"))
		{
			folderPath = tagFilePath.substring(0, tagFilePath.lastIndexOf("/"));
			tagFileName = tagFilePath.substring(0, tagFilePath.lastIndexOf("."));
		}
		else
		{
			folderPath = tagFilePath;
		}
		Path path = new Path(folderPath);
		FileSystem fs = path.getFileSystem(conf);
		for(FileStatus status : fs.listStatus(path))
		{
			String fileName = status.getPath().toString();
			if(!fileName.toLowerCase().contains("tag") && fileName.contains(tagFileName))
			{
				dataFileName = fileName;
			}
		}
		
		return dataFileName;
		
		
	}
	


	/**
	 * get the file name without the full directory path
	 * @param fullFileName is the file name with it's directory path
	 * @return
	 */
	public String getShortFileName(String fullFileName)
	{
			String fileName = fullFileName.substring(fullFileName.lastIndexOf("/")+1, fullFileName.length());
			
			return fileName;
	
	}
	
	
	///to check if the path is exist
	public boolean isPathExist(String srcpath) throws IOException
	{
//		try
//		{
			Configuration conf = new Configuration();
			Path path = new Path(srcpath);
			FileSystem fs = path.getFileSystem(conf);
			
			if(fs.exists(path))
			{
				return true;
			}
			else
			{
				return false;
			}
//		}
//		catch (Exception e)
//		{
//			// TODO: handle exception
//		}
//		return false;
	}
	
	//delete a specified path in HDFS
	public void deletePath(String destPath) throws IOException
	{
//		try
//		{
			Configuration conf = new Configuration();
			Path path = new Path(destPath);
			FileSystem fs = path.getFileSystem(conf);
			
			if(fs.exists(path))
			{
				fs.delete(path, true);
			}

//		}
//		catch (Exception e)
//		{
//			// TODO: handle exception
//		}
	}
	
	
	
	/// get a list of aged files from NFS
	public List<String> getOldFiles(String directoryPath, int daysAgo, Configuration conf) throws IOException
	{
		List<String> fileNames = new ArrayList<String>();
		Date currentDate = new Date();
		long miSecs = daysAgo * 24 * 60 * 60 * 1000; 
		
		Path path = new Path(directoryPath);
		FileSystem fs = path.getFileSystem(conf);
		
		for(FileStatus status : fs.listStatus(path))
		{
			if(status.getModificationTime() <= currentDate.getTime() - miSecs)
			{
				fileNames.add(status.getPath().toString());
			}
		}
		
		return fileNames;
	}
	
	
	
	
	//return a hive partition path in HDFS based on the source data file name
	//return value like hdfs://localhost:8020/CCR_OUT/CDM/2016/12/06/
//	public String GetHivePartitionPath(String systemHivePath, String fileName)
//	{
//		try
//		{
//			String patitionPath = null;
//			
//			String basePath = systemHivePath;
//			
//			if(!basePath.endsWith("/"))
//			{
//				basePath = basePath + "/";
//			}
//			
//			fileName = fileName.split("\\.")[0];  //remove extension
//			String year = fileName.split("-")[0];
//			String month = fileName.split("-")[1];
//			String day = fileName.split("-")[2];
//			
//			patitionPath = basePath + year + "/" + month + "/" + day + "/";
//			
//			return patitionPath;
//		}
//		catch (Exception e)
//		{
//			// TODO: handle exception
//		}
//		return null;
//	}
}

