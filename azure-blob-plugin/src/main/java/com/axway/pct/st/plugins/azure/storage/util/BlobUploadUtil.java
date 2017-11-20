package com.axway.pct.st.plugins.azure.storage.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.axway.pct.st.plugins.azure.storage.bean.AzureBlobStorageBeanWrapper;
import com.microsoft.azure.storage.blob.BlobProperties;

public class BlobUploadUtil {

	private static Logger log = Logger.getLogger(BlobUploadUtil.class);
	private static final String LOGGER_KEY = "[Azure-Upload-Utility]: ";

	public static final String AUTO_MODE = "auto";
	public static final String REGULAR_MODE = "regular";
	public static final String MULTIPART_MODE = "multipart";

	public static final long BLOCK_SIZE = (10 * 1024 * 1024);

	private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
	private static final String CACHE_CONTROL_HEADER = "Cache-Control";
	private static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
	private static final String CONTENT_LANGUAGE_HEADER = "Content-Language";
	private static final String CONTENT_MD5_HEADER = "Content-MD5";
	private static final String CONTENT_TYPE_HEADER = "Content-Type";

	public static boolean isRegularUploadMode(long fileSizeInBytes, String uploadMode) {
		boolean result = true;

		if (uploadMode.equalsIgnoreCase(AUTO_MODE)) {
			result = fileSizeInBytes < (1024 * 1024 * 1024);

			log.debug(LOGGER_KEY + "Auto mode is enabled. File Size is lesser than 100MB? [" + result
					+ "]. Multipart mode: [" + !result + "]");
		} else if (uploadMode.equalsIgnoreCase(MULTIPART_MODE)) {
			int chunks = (int) (fileSizeInBytes / BLOCK_SIZE);
			log.debug(LOGGER_KEY + "Multipart mode selected. Data will be sent in " + BLOCK_SIZE
					+ " byte sized chunks. Total Chunks are: " + chunks);
			result = false;
		} else {
			log.debug(LOGGER_KEY + "Regular upload mode is selected.");
		}
		return result;
	}

	public static boolean isFolderIncluded(String objectName) {
		return objectName.contains("/");
	}

	public static BlobProperties getStorageMetadata(AzureBlobStorageBeanWrapper configuration) {
		Map<String, String> keyValuePairs = parseMetadata(configuration.getAzureStorageMetadata());
		BlobProperties storageMetadata = new BlobProperties();

		for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
			log.debug(LOGGER_KEY + "Storage Metadata [Key = " + entry.getKey() + ", Value: " + entry.getValue() + "]");
			if (entry.getKey().equalsIgnoreCase(CONTENT_DISPOSITION_HEADER))
				storageMetadata.setContentDisposition(entry.getValue());
			if (entry.getKey().equalsIgnoreCase(CACHE_CONTROL_HEADER))
				storageMetadata.setCacheControl(entry.getValue());
			if (entry.getKey().equalsIgnoreCase(CONTENT_ENCODING_HEADER))
				storageMetadata.setContentEncoding(entry.getValue());
			if (entry.getKey().equalsIgnoreCase(CONTENT_LANGUAGE_HEADER))
				storageMetadata.setContentLanguage(entry.getValue());
			if (entry.getKey().equalsIgnoreCase(CONTENT_MD5_HEADER))
				storageMetadata.setContentMD5(entry.getValue());
			if (entry.getKey().equalsIgnoreCase(CONTENT_TYPE_HEADER))
				storageMetadata.setContentType(entry.getValue());
		}

		return storageMetadata;
	}

	public static HashMap<String, String> getCustomMetadata(AzureBlobStorageBeanWrapper configuration) {
		HashMap<String, String> customMetadata = new HashMap<String, String>();
		Map<String, String> keyValuePairs = parseMetadata(configuration.getAzureCustomMetadata());
		for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
			log.debug(LOGGER_KEY + "User Metadata [Key = " + entry.getKey() + ", Value: " + entry.getValue() + "]");
			customMetadata.put(entry.getKey().trim(), entry.getValue().trim());
		}
		return customMetadata;
	}

	private static Map<String, String> parseMetadata(String metaData) {
		Map<String, String> entries = new HashMap<String, String>();
		if (isNullOrEmptyTrimmed(metaData)) {
			return entries;
		}

		Pattern pattern = Pattern.compile("(.*?)=(.*)");
		Matcher matcher = pattern.matcher(metaData);
		while (matcher.find()) {
			String key = matcher.group(1).trim();
			String value = matcher.group(2).trim();
			entries.put(key, value);
		}

		return entries;
	}

	private static boolean isNullOrEmptyTrimmed(String str) {
		return (str == null) || (str.trim().length() == 0);
	}

	public static String constructUploadTargetFileName(String originalFileName, String azureDestinationPath,
			String sendFileAs) {
		String result = originalFileName;
		log.debug(LOGGER_KEY + "Original File Name: " + originalFileName);
		log.debug(LOGGER_KEY + "Destination Directory Path: " + azureDestinationPath);
		String uploadDestination = sanitizeUploadDestination(azureDestinationPath);

		if (!StringUtils.isAllEmpty(sendFileAs))
			result = new StringBuilder().append(uploadDestination).append(sendFileAs).toString();
		else {
			result = new StringBuilder().append(uploadDestination).append(originalFileName).toString();
		}
		log.info(LOGGER_KEY + "Azure Upload Target: " + result);
		return result;
	}

	private static String sanitizeUploadDestination(String azureDestinationPath) {
		String result = azureDestinationPath;

		if (azureDestinationPath.isEmpty()) {
			return result;
		}

		if (azureDestinationPath.startsWith("/")) {
			result = azureDestinationPath.substring(1);
		}

		if (!azureDestinationPath.endsWith("/")) {
			result = new StringBuilder().append(result).append("/").toString();
		}
		log.debug(LOGGER_KEY + "Sanitized version of Destination directory: " + result);

		return result;
	}

}
