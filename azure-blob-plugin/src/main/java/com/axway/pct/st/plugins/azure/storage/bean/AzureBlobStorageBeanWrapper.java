package com.axway.pct.st.plugins.azure.storage.bean;

import com.axway.pct.st.plugins.azure.storage.site.expression.ExpressionEvaluated;
import com.axway.pct.st.plugins.azure.storage.site.expression.PluginExpressionBuilder;
import com.axway.pct.st.plugins.azure.storage.site.expression.PluginExpressionEvaluator;

public abstract interface AzureBlobStorageBeanWrapper {

	public abstract PluginExpressionEvaluator getEvaluator();

	public abstract void setEvaluator(PluginExpressionBuilder paramPluginsExpressionBuilder);

	@ExpressionEvaluated
	public abstract String getAzureContainer();

	@ExpressionEvaluated
	public abstract String getAzureDownloadObjectKey();

	@ExpressionEvaluated
	public abstract String getAzureDownloadPattern();

	@ExpressionEvaluated
	public abstract String getAzureUploadDestination();

	@ExpressionEvaluated
	public abstract String getAzureCustomMetadata();

	@ExpressionEvaluated
	public abstract String getAzureStorageMetadata();

	public abstract String getAzureNetworkZone();

	public abstract String getAzurePatternType();

	public abstract String getAzureUploadMode();

	public abstract String getAzureAccountKey();

	public abstract String getAzureAccountName();

	public abstract String getAzureAutoCreateContainerCheck();
}
