package com.axway.pct.st.plugins.azure.storage.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.axway.pct.st.plugins.azure.storage.bean.AzureBlobStorageBeanWrapper;
import com.axway.st.plugins.site.RemotePartner;

public final class RemotePartnerBuilder {

	private static final String REMOTE_HOST_SEPARATOR = "//";
	private static final Logger log = Logger.getLogger(RemotePartnerBuilder.class);
	private static final String LOGGER_KEY= "[AzureRemotePartner]: ";

	public static RemotePartner getDownloadPartner(AzureBlobStorageBeanWrapper bean) {
		String remoteHost = getRemoteHostString(bean);
		String remoteFolder = StringUtils.EMPTY;
		if (bean.getAzureDownloadObjectKey() == null) {
			remoteFolder = REMOTE_HOST_SEPARATOR;
		} else if (bean.getAzureDownloadObjectKey().contains("/")) {
			remoteFolder = bean.getAzureDownloadObjectKey();
		}
		log.info(LOGGER_KEY + "- Download - Remote Host: " + remoteHost);
		log.info(LOGGER_KEY + "- Download - Remote Folder: " + remoteFolder);
		return new RemotePartner(remoteHost, remoteFolder);
	}

	public static RemotePartner getUploadPartner(AzureBlobStorageBeanWrapper bean) {
		String remoteHost = getRemoteHostString(bean);
		String remoteFolder = StringUtils.EMPTY;
		if (bean.getAzureUploadDestination() == null) {
			remoteFolder = REMOTE_HOST_SEPARATOR;
		} else if (bean.getAzureUploadDestination().contains("/")) {
			remoteFolder = bean.getAzureUploadDestination();
		}
		log.info(LOGGER_KEY + "- Upload - Remote Host: " + remoteHost);
		log.info(LOGGER_KEY + "- Upload - Remote Folder: " + remoteFolder);
		return new RemotePartner(remoteHost, remoteFolder, true);
	}

	private static String getRemoteHostString(AzureBlobStorageBeanWrapper bean) {
		StringBuilder result = new StringBuilder();

		result.append(bean.getAzureAccountName()).append(REMOTE_HOST_SEPARATOR);
		result.append(bean.getAzureContainer());

		return result.toString();
	}

}
