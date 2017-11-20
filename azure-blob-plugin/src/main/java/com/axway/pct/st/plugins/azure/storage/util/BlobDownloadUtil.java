package com.axway.pct.st.plugins.azure.storage.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.axway.pct.st.plugins.azure.storage.bean.AzureBlobStorageBeanWrapper;
import com.axway.pct.st.plugins.azure.storage.site.pattern.PatternKeyValidator;
import com.axway.pct.st.plugins.azure.storage.site.pattern.PatternKeyValidatorFactory;
import com.axway.st.plugins.site.FileItem;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

public class BlobDownloadUtil {

	private static final Logger log = Logger.getLogger(BlobDownloadUtil.class);
	private static final String LOGGER_KEY = "[Azure-Download-Utility]: ";

	public static List<FileItem> setFileItemList(List<String> files) {
		List<FileItem> result = new ArrayList<FileItem>();
		for (String fileName : files) {
			FileItem fileItem = new FileItem(fileName);
			result.add(fileItem);
		}
		return result;
	}

	public static String sanitizeDownloadObjectKey(String downloadObjectKey) {

		if (downloadObjectKey == null) {
			log.info(LOGGER_KEY + "Download object key set to the root of the container. Null value specified");
			downloadObjectKey = StringUtils.EMPTY;
		}

		if (downloadObjectKey.equalsIgnoreCase("/")) {
			log.info(LOGGER_KEY + "Download object key set to the root of the container");
			downloadObjectKey = StringUtils.EMPTY;
		}

		if (downloadObjectKey.startsWith("/")) {
			log.info(LOGGER_KEY + "Removing leading forward-slashes from download object key");
			downloadObjectKey = downloadObjectKey.substring(1, downloadObjectKey.length());
		}

		log.debug(LOGGER_KEY + "Formatting the Input to match Azure specification - " + downloadObjectKey);
		return downloadObjectKey;
	}

	public static String getDownloadDirectoryPath(String downloadObjectKey) {
		String downloadDirectory = StringUtils.EMPTY;
		if(downloadObjectKey.equalsIgnoreCase(StringUtils.EMPTY))
			return StringUtils.EMPTY;
		
		downloadDirectory = sanitizeDownloadObjectKey(downloadObjectKey).substring(0, downloadObjectKey.lastIndexOf("/"));
		
		log.info(LOGGER_KEY + "Download directory defined as: " + downloadDirectory);
		return downloadDirectory;
	}

	public static String getTarget(String blobName) {
		String target = StringUtils.EMPTY;
		if(blobName.equalsIgnoreCase(StringUtils.EMPTY))
			return StringUtils.EMPTY;
		
		target = sanitizeDownloadObjectKey(blobName).substring(blobName.lastIndexOf("/")+1);
		
		log.info(LOGGER_KEY + "Download file defined as: " + target);
		return target;
	}

	public static List<String> searchBlobItemList(AzureBlobStorageBeanWrapper config,
			ArrayList<ListBlobItem> blobItemList) {
		ArrayList<String> result = new ArrayList<String>();
		String downloadPattern = config.getAzureDownloadPattern();
		String patternType = config.getAzurePatternType();
		//String downloadKey = sanitizeDownloadObjectKey(config.getAzureDownloadObjectKey());
		
		for(ListBlobItem item: blobItemList) {
			if(item instanceof CloudBlob) {
				CloudBlob blob = (CloudBlob)item;
				if(downloadPattern.isEmpty()) {					
					result.add(blob.getName());
					log.debug(LOGGER_KEY + " --- Matched Item[downloadPattern=(none)]: " + blob.getName());
				} else {
					PatternKeyValidator validator = PatternKeyValidatorFactory.createPatternValidator(patternType);
					if(validator.isValid(getTarget(blob.getName()), downloadPattern)) {
						result.add(blob.getName());
						log.debug(LOGGER_KEY + " --- Matched Item[downloadPattern="+downloadPattern+", patternType="+patternType+"]: " + blob.getName());
					} else {
						log.debug(LOGGER_KEY + " --- No Matched Item[downloadPattern="+downloadPattern+", patternType="+patternType+"]: " + blob.getName());
					}					
				} 
			}
		}
		log.info(LOGGER_KEY + "Matched entries in Listing: " + result.size());
		
		return result;
	}

}
