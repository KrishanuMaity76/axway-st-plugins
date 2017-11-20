package com.axway.pct.st.plugins.azure.storage.site;

import com.axway.pct.st.plugins.azure.storage.bean.AzureBlobStorageBeanWrapper;

/**
 * @author cmanda Builder pattern.
 */
public class AzureBlobStorageBuilder {

	/**
	 * Creates and configures AzureBlobStorageConnector from the given bean.
	 * 
	 * @param config
	 *            the bean, holding the configuration.
	 * @return a AbstractBlobStorageConnector instance
	 */
	public AbstractAzureBlobStorageConnector build(AzureBlobStorageBeanWrapper config) {
		AbstractAzureBlobStorageConnector storageConnector = null;

		storageConnector = new AzureBlobStorageConnectorImpl(config);

		return storageConnector;
	}

}