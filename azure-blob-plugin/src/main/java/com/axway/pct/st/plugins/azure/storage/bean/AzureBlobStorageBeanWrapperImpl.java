package com.axway.pct.st.plugins.azure.storage.bean;

import com.axway.pct.st.plugins.azure.storage.site.expression.PluginExpressionBuilder;
import com.axway.pct.st.plugins.azure.storage.site.expression.PluginExpressionEvaluator;

public class AzureBlobStorageBeanWrapperImpl implements AzureBlobStorageBeanWrapper {

	private PluginExpressionEvaluator mEvaluator = PluginExpressionBuilder.create().build();
	private AzureBlobStorageUIBean mAzureUIBean;

	public AzureBlobStorageBeanWrapperImpl(AzureBlobStorageUIBean uiBean) {
		this.mAzureUIBean = uiBean;

	}

	@Override
	public PluginExpressionEvaluator getEvaluator() {
		return mEvaluator;
	}

	@Override
	public void setEvaluator(PluginExpressionBuilder builder) {
		this.mEvaluator = builder.build();

	}

	@Override
	public String getAzureContainer() {
		return this.mAzureUIBean.getAzureContainer();
	}

	@Override
	public String getAzureDownloadObjectKey() {
		return this.mAzureUIBean.getAzureDownloadObjectKey();
	}

	@Override
	public String getAzureDownloadPattern() {
		return this.mAzureUIBean.getAzureDownloadPattern();
	}

	@Override
	public String getAzureUploadDestination() {
		return this.mAzureUIBean.getAzureUploadDestination();
	}

	@Override
	public String getAzureCustomMetadata() {
		return this.mAzureUIBean.getAzureCustomMetadata();
	}

	@Override
	public String getAzureStorageMetadata() {
		return this.mAzureUIBean.getAzureStorageMetadata();
	}

	@Override
	public String getAzureNetworkZone() {
		return this.mAzureUIBean.getAzureNetworkZone();
	}

	@Override
	public String getAzurePatternType() {
		return this.mAzureUIBean.getAzurePatternType();
	}

	@Override
	public String getAzureUploadMode() {
		return this.mAzureUIBean.getAzureUploadMode();
	}

	@Override
	public String getAzureAccountKey() {
		return this.mAzureUIBean.getAzureAccountKey();
	}

	@Override
	public String getAzureAccountName() {
		return this.mAzureUIBean.getAzureAccountName();
	}

	@Override
	public String getAzureAutoCreateContainerCheck() {
		return this.mAzureUIBean.getAzureAutoCreateContainerCheck();
	}

}
