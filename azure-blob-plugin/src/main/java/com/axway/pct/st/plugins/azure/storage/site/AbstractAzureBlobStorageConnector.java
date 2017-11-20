package com.axway.pct.st.plugins.azure.storage.site;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.axway.st.plugins.site.DestinationFile;
import com.axway.st.plugins.site.TransferFailedException;

public abstract interface AbstractAzureBlobStorageConnector {

	public abstract void connect() throws TransferFailedException;

	public abstract void upload(String destinationPath, InputStream inputStream, String sendFileAs, long fileSize) throws IOException;

	public abstract void download(DestinationFile destinationFile) throws IOException;

	public abstract List<String> listFiles(String fileFilter) throws IOException;
	
	public abstract void disconnect();

}
