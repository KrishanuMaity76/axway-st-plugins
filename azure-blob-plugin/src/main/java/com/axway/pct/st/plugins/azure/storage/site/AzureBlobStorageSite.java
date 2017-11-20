package com.axway.pct.st.plugins.azure.storage.site;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.axway.pct.st.plugins.azure.storage.bean.AzureBlobStorageBeanWrapper;
import com.axway.pct.st.plugins.azure.storage.bean.AzureBlobStorageBeanWrapperImpl;
import com.axway.pct.st.plugins.azure.storage.bean.AzureBlobStorageUIBean;
import com.axway.pct.st.plugins.azure.storage.site.expression.ExpressionProxy;
import com.axway.pct.st.plugins.azure.storage.site.expression.PluginExpressionBuilder;
import com.axway.pct.st.plugins.azure.storage.util.BlobDownloadUtil;
import com.axway.pct.st.plugins.azure.storage.util.RemotePartnerBuilder;
import com.axway.st.plugins.site.CustomSite;
import com.axway.st.plugins.site.DestinationFile;
import com.axway.st.plugins.site.FileItem;
import com.axway.st.plugins.site.RemotePartner;
import com.axway.st.plugins.site.SourceFile;
import com.axway.st.plugins.site.services.ProxyService;

public class AzureBlobStorageSite extends CustomSite {
	private static final Logger log = Logger.getLogger(AzureBlobStorageSite.class);
	private static final String LOGGER_KEY = "[AzureBlobStorageSite]: ";

	private AzureBlobStorageUIBean mBlobBean = new AzureBlobStorageUIBean();
	private AzureBlobStorageBeanWrapper mBlobBeanWrapper;
	private AbstractAzureBlobStorageConnector mBlobConnector;

	@SuppressWarnings("unused")
	@Inject
	private ProxyService mProxyService;

	public AzureBlobStorageSite() {
		setUIBean(this.mBlobBean);
		this.mBlobBeanWrapper = (AzureBlobStorageBeanWrapper) ExpressionProxy
				.newInstance(new AzureBlobStorageBeanWrapperImpl(this.mBlobBean));
	}

	private void connect() throws IOException {
		initAzureStorageConnector();
		this.mBlobConnector.connect();
	}

	private void disconnect() throws IOException {
		initAzureStorageConnector();
		this.mBlobConnector.disconnect();
	}

	private void initAzureStorageConnector() {
		if (this.mBlobConnector == null) {
			this.mBlobConnector = new AzureBlobStorageConnectorImpl(this.mBlobBeanWrapper);
		}
	}

	@Override
	public void finalizeExecution() throws IOException {
		log.debug(LOGGER_KEY + "Finalizing execution ... and disconnecting.");
		disconnect();

	}

	@Override
	public void getFile(DestinationFile destinationFile) throws IOException {
		log.debug(LOGGER_KEY + "GET File(s) being requested ...");
		connect();
		this.mBlobConnector.download(destinationFile);
	}

	@Override
	public List<FileItem> list() throws IOException {
		log.debug(LOGGER_KEY + "LIST File(s) being requested ...");
		connect();
		String downloadObjectKey = this.mBlobBeanWrapper.getAzureDownloadObjectKey();
		log.debug(LOGGER_KEY + "Download object key: " + downloadObjectKey);
		List<String> files = this.mBlobConnector.listFiles(downloadObjectKey);

		List<FileItem> result = BlobDownloadUtil.setFileItemList(files);
		return result;
	}

	@Override
	public void putFile(SourceFile sourceFile) throws IOException {
		log.debug(LOGGER_KEY + "PUT File(s) being requested ...");
		connect();

		this.mBlobBeanWrapper.setEvaluator(PluginExpressionBuilder.create().setTarget(sourceFile.getName()));

		RemotePartner uploadPartner = RemotePartnerBuilder.getUploadPartner(this.mBlobBeanWrapper);
		InputStream input = sourceFile.getInputStream(uploadPartner);
		log.info(LOGGER_KEY + "Remote Partner Host		: " + uploadPartner.getRemoteHost());
		log.info(LOGGER_KEY + "Remote Partner Directory	: " + uploadPartner.getRemoteFolder());
		log.info(LOGGER_KEY + "File being uploaded		: " + sourceFile.getName());
		log.info(LOGGER_KEY + "Size being uploaded		: " + sourceFile.getSize());
		this.mBlobConnector.upload(uploadPartner.getRemoteFolder(), input, sourceFile.getName(), sourceFile.getSize());

	}

}
