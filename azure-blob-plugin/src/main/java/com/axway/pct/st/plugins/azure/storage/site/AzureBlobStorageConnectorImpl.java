package com.axway.pct.st.plugins.azure.storage.site;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.axway.pct.st.plugins.azure.storage.bean.AzureBlobStorageBeanWrapper;
import com.axway.pct.st.plugins.azure.storage.util.BlobDownloadUtil;
import com.axway.pct.st.plugins.azure.storage.util.BlobUploadUtil;
import com.axway.pct.st.plugins.azure.storage.util.RemotePartnerBuilder;
import com.axway.st.plugins.site.DestinationFile;
import com.axway.st.plugins.site.RemotePartner;
import com.axway.st.plugins.site.TransferFailedException;
import com.axway.st.plugins.site.services.ProxyService;
import com.google.common.collect.Lists;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobProperties;
import com.microsoft.azure.storage.blob.BlockEntry;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

public class AzureBlobStorageConnectorImpl implements AbstractAzureBlobStorageConnector {

	private static Logger log = Logger.getLogger(AzureBlobStorageConnectorImpl.class);
	private static String LOGGER_KEY = "[Azure-Storage-Connector]: ";

	private CloudBlobClient mBlobClient;

	/**
	 * Transfer Plugin specific attributes
	 */
	private AzureBlobStorageBeanWrapper config;
	private ProxyService mProxyService;

	/**
	 * @return the mProxyService
	 */
	public ProxyService getProxyService() {
		return mProxyService;
	}

	/**
	 * @param proxyService
	 *            the mProxyService to set
	 */
	public void setProxyService(ProxyService proxyService) {
		if (proxyService == null) {
			String errMsg = "No Proxy Service Available";
			if (log.isDebugEnabled()) {
				log.debug(LOGGER_KEY + errMsg);
			}
			return;
		}
	}

	public AzureBlobStorageConnectorImpl(AzureBlobStorageBeanWrapper beanConfiguration) {
		setBeanConfiguration(beanConfiguration);
	}

	private void setBeanConfiguration(AzureBlobStorageBeanWrapper beanConfiguration) {
		if (beanConfiguration == null) {
			String errMsg = "UI Bean cannot be null.";
			log.warn(LOGGER_KEY + errMsg);
			return;
		}
		this.config = beanConfiguration;
		log.info(LOGGER_KEY + "UI Bean registered successfully.");
	}

	private void setAzureBlobClient(CloudStorageAccount azureStorageAccount) {
		if (azureStorageAccount == null) {
			log.error(LOGGER_KEY + "Azure storage account not available. Unable to create a client connection.");
			return;
		}
		this.mBlobClient = azureStorageAccount.createCloudBlobClient();
	}

	@Override
	public void connect() throws TransferFailedException {
		log.debug(LOGGER_KEY + "Connecting");
		log.info(LOGGER_KEY + "POC Code. Not implementing the Network Zone/Proxy integration: "
				+ this.config.getAzureNetworkZone());
		CloudBlobContainer container = null;

		try {
			setAzureBlobClient(CloudStorageAccount.parse(
					createConnectionString(this.config.getAzureAccountName(), this.config.getAzureAccountKey())));
			container = this.mBlobClient.getContainerReference(this.config.getAzureContainer());
			log.debug(
					LOGGER_KEY + "Auto create container is set to: " + Boolean.parseBoolean(this.config.getAzureAutoCreateContainerCheck()));

			if (container.exists()) {
				log.info(LOGGER_KEY + "Container: " + this.config.getAzureContainer() + " exists.");
			} else if (Boolean.parseBoolean(this.config.getAzureAutoCreateContainerCheck())) {
				log.info(LOGGER_KEY + "Container: " + this.config.getAzureContainer() + " doesn't exist. Creating ...");
				log.info(LOGGER_KEY + "Auto-created container status: " + container.createIfNotExists());
			} else {
				log.error(LOGGER_KEY + "Container: " + this.config.getAzureContainer()
						+ " doesn't exist. Auto-creating container is disabled");
				throw new TransferFailedException("Container: " + this.config.getAzureContainer()
						+ " doesn't exist. Auto-creating container is disabled");
			}

		} catch (InvalidKeyException e) {
			log.error(LOGGER_KEY + e.getMessage());
		} catch (URISyntaxException e) {
			log.error(LOGGER_KEY + e.getMessage());
		} catch (StorageException e) {
			log.error(LOGGER_KEY + e.getMessage());
		}
	}

	private static String createConnectionString(String azureAccountName, String azureAccountKey) {
		String connectionString = String.format(
				"DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net",
				azureAccountName, azureAccountKey);
		log.debug(LOGGER_KEY + "ConnectionString: " + connectionString);
		return connectionString;

	}

	@Override
	public void upload(String destinationDirectory, InputStream inputStream, String fileName, long fileSize)
			throws IOException {
		boolean status = false;
		log.debug(LOGGER_KEY + "Initiating Upload");
		String container = this.config.getAzureContainer();
		if (BlobUploadUtil.isFolderIncluded(fileName)) {
			throw new TransferFailedException(
					"File name contains special characters used for folders in Azure. Aborting.");
		}

		try {
			fileName = BlobUploadUtil.constructUploadTargetFileName(fileName, destinationDirectory, StringUtils.EMPTY);
			boolean isRegularUploadMode = BlobUploadUtil.isRegularUploadMode(fileSize,
					this.config.getAzureUploadMode());
			BlobProperties storageMetadata = BlobUploadUtil.getStorageMetadata(this.config);
			HashMap<String, String> customMetadata = BlobUploadUtil.getCustomMetadata(this.config);
			log.info(LOGGER_KEY + "Azure Storage Upload to [" + container + "] under path [" + destinationDirectory
					+ "] for file [" + fileName + "] underway");
			if (isRegularUploadMode) {
				status = regularUpload(container, fileName, inputStream, storageMetadata, customMetadata, fileSize);
			} else {
				status = chunkUpload(container, fileName, inputStream, storageMetadata, customMetadata, fileSize);
			}
		} finally {
			log.info(LOGGER_KEY + "Azure storage upload completed [" + status + "]");
		}
	}

	private boolean regularUpload(String containerName, String fileName, InputStream input, BlobProperties metadata,
			HashMap<String, String> userMetadata, long fileSize) throws IOException {
		boolean status = false;
		CloudBlockBlob singleBlob;
		CloudBlobContainer container;

		try {
			container = this.mBlobClient.getContainerReference(containerName);
			if (container.exists()) {
				log.info(LOGGER_KEY + "Container exists: " + containerName);
				singleBlob = container.getBlockBlobReference(fileName);

				log.info(LOGGER_KEY + "User metadata being applied ..." + userMetadata.size() + " key(s)");
				singleBlob.setMetadata(userMetadata);

				// singleBlob.getProperties().setCacheControl(metadata.getCacheControl());
				// singleBlob.getProperties().setContentDisposition(metadata.getContentDisposition());
				// singleBlob.getProperties().setContentEncoding(metadata.getContentEncoding());
				// singleBlob.getProperties().setContentType(metadata.getContentType());
				log.info(LOGGER_KEY + "Initiating blob upload with regular mode");
				singleBlob.upload(input, fileSize);
				if (singleBlob.exists())
					log.info(LOGGER_KEY + "Azure Blob upload finished with 'Blob' mode successfully.");
			}
		} catch (URISyntaxException urie) {
			log.error(LOGGER_KEY + "Azure Resource URI constructed based on the containerName is invalid. "
					+ urie.getMessage());
			throw new TransferFailedException(urie.getMessage(), urie, true);
		} catch (StorageException se) {
			log.error(LOGGER_KEY + "Azure Storage Service Error occurred. " + se.getMessage());
			throw new TransferFailedException(se.getMessage(), se, false);
		} catch (IOException ioe) {
			log.error(LOGGER_KEY + "Azure Storage I/O exception occurred. " + ioe.getMessage());
			throw new TransferFailedException(ioe.getMessage(), ioe, false);
		} finally {
			input.close();
			status = true;
		}
		return status;
	}

	private boolean chunkUpload(String containerName, String fileName, InputStream input, BlobProperties metadata,
			HashMap<String, String> userMetadata, long fileSize) throws IOException {
		boolean status = false;
		CloudBlockBlob chunkedBlob;
		CloudBlobContainer container;
		int blockCount = (int) ((float) fileSize / (float) BlobUploadUtil.BLOCK_SIZE) + 1;

		try {
			container = this.mBlobClient.getContainerReference(containerName);
			if (container.exists()) {
				log.info(LOGGER_KEY + "Container exists: " + containerName);
				// Create reference to the blob being uploaded -- filename
				chunkedBlob = container.getBlockBlobReference(fileName);

				log.info(LOGGER_KEY + "User metadata being applied ..." + userMetadata.size() + " key(s)");
				chunkedBlob.setMetadata(userMetadata);

				log.info(LOGGER_KEY + "Initiating blob upload with 'BlockList' mode");

				long bytesLeft = fileSize;
				int blockNumber = 0;
				@SuppressWarnings("unused")
				long bytesRead = 0;

				log.info(LOGGER_KEY + "Block Size set to: " + BlobUploadUtil.BLOCK_SIZE + ". Calculated blocks are: "
						+ blockCount);

				// Managed list of all block ids being uploaded
				List<BlockEntry> blockList = new ArrayList<BlockEntry>();

				// Iterate & Upload individual blocks till the last block is
				// uploaded
				while (bytesLeft > 0) {

					blockNumber++;

					// how much to read (only last chunk may be smaller)
					// Source: http://www.redbaronofazure.com/?p=1
					long bytesToRead = 0;
					if (bytesLeft >= (long) BlobUploadUtil.BLOCK_SIZE) {
						bytesToRead = BlobUploadUtil.BLOCK_SIZE;
					} else {
						bytesToRead = bytesLeft;
					}

					// trace out progress
					float percentageDone = ((float) blockNumber / (float) blockCount) * (float) 100;

					// save block id in array (must be base64)
					String blockId = Base64.getEncoder()
							.encodeToString(String.format("BlockId%07d", blockNumber).getBytes(StandardCharsets.UTF_8));
					BlockEntry block = new BlockEntry(blockId);

					blockList.add(block);

					// upload block chunk to Azure Storage
					chunkedBlob.uploadBlock(blockId, input, (long) bytesToRead);
					log.debug(LOGGER_KEY + " -- Block [Id: " + blockId + "]: " + String.format("%.0f%%", percentageDone)
							+ " uploaded.");

					// increment/decrement counters
					bytesRead += bytesToRead;
					bytesLeft -= bytesToRead;
				}
				// Commit the block list to merge the blocks on Storage
				// NOTE: Microsoft removes orphan blocks after 7 days
				chunkedBlob.commitBlockList(blockList);

				log.info(LOGGER_KEY + "Block list with elements[" + blockList.size() + "] committed. Upload finished");
				if (chunkedBlob.exists())
					log.info(LOGGER_KEY + "Azure Blob 'multipart' upload finished successfully.");
			}
		} catch (URISyntaxException urie) {
			log.error(LOGGER_KEY + "Azure Resource URI constructed based on the containerName is invalid. "
					+ urie.getMessage());
			throw new TransferFailedException(urie.getMessage(), urie, true);
		} catch (StorageException se) {
			log.error(LOGGER_KEY + "Azure Storage Service Error occurred. " + se.getMessage());
			throw new TransferFailedException(se.getMessage(), se, false);
		} catch (IOException ioe) {
			log.error(LOGGER_KEY + "Azure Storage I/O exception occurred. " + ioe.getMessage());
			throw new TransferFailedException(ioe.getMessage(), ioe, false);
		} finally {
			input.close();
			status = true;
		}
		return status;
	}

	@Override
	public void download(DestinationFile destinationFile) throws IOException {
		log.debug(LOGGER_KEY + "Initiating Download");
		String target = destinationFile.getName();
		CloudBlobContainer container;
		CloudBlockBlob downloadBlob;
		String containerName = this.config.getAzureContainer();
		OutputStream targetStream = null;
		try {
			targetStream = getDestinationFileOutputStream(destinationFile);
			container = this.mBlobClient.getContainerReference(containerName);
			if (container.exists()) {
				downloadBlob = container.getBlockBlobReference(target);
				downloadBlob.download(targetStream);
			}

		} catch (URISyntaxException e) {
			log.error(LOGGER_KEY + e.getMessage());
			throw new TransferFailedException("Unable to initiate DOWNLOAD request.", e);
		} catch (StorageException e) {
			log.error(LOGGER_KEY + e.getMessage());
			throw new TransferFailedException("Unable to initiate DOWNLOAD request.", e);
		} finally {

		}
	}

	private OutputStream getDestinationFileOutputStream(DestinationFile file) throws IOException {
		RemotePartner downloadPartner = RemotePartnerBuilder.getDownloadPartner(this.config);
		return file.getOutputStream(downloadPartner);
	}

	@Override
	public List<String> listFiles(String downloadObjectKey) throws IOException {
		List<String> objectList = new ArrayList<String>();
		CloudBlobContainer container;
		CloudBlobDirectory downloadDirectory;
		String containerName = this.config.getAzureContainer();
		@SuppressWarnings("unused")
		String downloadObjectPrefix = null;
		log.debug(LOGGER_KEY + "Listing Storage with Filtering criteria: " + downloadObjectKey);
		if (downloadObjectKey == null) {
			log.warn(LOGGER_KEY + "Download Object Key defined as 'null'. Verify transfer site definition");
		}
		downloadObjectKey = BlobDownloadUtil.sanitizeDownloadObjectKey(downloadObjectKey);
		try {
			container = this.mBlobClient.getContainerReference(containerName);
			if (!container.exists()) {
				log.error(LOGGER_KEY + "Container [" + containerName + "] doesn't exist. Aborting download request");
			} else {
				downloadDirectory = container
						.getDirectoryReference(BlobDownloadUtil.getDownloadDirectoryPath(downloadObjectKey));
				downloadObjectPrefix = BlobDownloadUtil.getTarget(downloadObjectKey);
				objectList = BlobDownloadUtil.searchBlobItemList(this.config, getBlobItemList(downloadDirectory));
			}
		} catch (StorageException e) {
			log.error(LOGGER_KEY + e.getMessage());
			throw new TransferFailedException("Unable to initiate LIST request.", e);
		} catch (URISyntaxException e) {
			log.error(LOGGER_KEY + e.getMessage());
			throw new TransferFailedException("Unable to initiate LIST request.", e);
		} finally {

		}

		return objectList;
	}

	private ArrayList<ListBlobItem> getBlobItemList(CloudBlobDirectory blobDirectory) {
		ArrayList<ListBlobItem> blobList = null;
		try {
			blobList = Lists.newArrayList(blobDirectory.listBlobs());
			for (Iterator<ListBlobItem> iterator = blobList.iterator(); iterator.hasNext();) {
				ListBlobItem listBlobItem = (ListBlobItem) iterator.next();
				if (listBlobItem instanceof CloudBlob) {
					CloudBlob item = (CloudBlob) listBlobItem;
					log.debug(LOGGER_KEY + "--[Blob]		: " + item.getName());
				} else if (listBlobItem instanceof CloudBlobDirectory) {
					CloudBlobDirectory directory = (CloudBlobDirectory) listBlobItem;
					log.debug(LOGGER_KEY + "--[Directory] 	: " + directory.getUri().toString());
				}
			}
		} catch (StorageException e) {
			log.error(LOGGER_KEY + e.getMessage());
		} catch (URISyntaxException e) {
			log.error(LOGGER_KEY + e.getMessage());
		}

		return blobList;

	}

	@Override
	public void disconnect() {
		log.debug(LOGGER_KEY + "Disconnecting from Azure Storage.");
	}

	@SuppressWarnings("unused")
	private File createTempFile(String fileName, InputStream inputStream) throws IOException {
		File tempFile = File.createTempFile(fileName, null);
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(tempFile);
			IOUtils.copyLarge(inputStream, output);
			File localFile1 = tempFile;
			return localFile1;
		} catch (IOException ex) {
			throw new TransferFailedException(ex.getMessage(), ex, true);
		} finally {
			if (output != null)
				output.close();
		}
	}

}
