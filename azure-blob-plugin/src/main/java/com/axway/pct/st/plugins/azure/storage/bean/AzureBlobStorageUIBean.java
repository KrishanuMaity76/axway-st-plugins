package com.axway.pct.st.plugins.azure.storage.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.axway.st.plugins.site.Encrypted;
import com.axway.st.plugins.site.UIBean;

public class AzureBlobStorageUIBean implements UIBean {

	@NotNull(message = "Azure Storage Account Name cannot be null")
	@Size(min = 1, message = "Azure Account name cannot not be empty")
	@Pattern(regexp = "\\S*", message = "Account name cannot contain whitespaces.")
	private String mAzureAccountName;

	@NotNull(message = "Azure Storage Account Key cannot be null")
	@Size(min = 1, message = "Azure Account name cannot not be empty")
	private String mAzureAccountKey;

	@NotNull(message = "Container name cannot be null")
	@Size(min = 3, max = 63, message = "Container name must be between 3 and 63 characters long")
	// @Pattern(regexp = "\\^[a-z0-9](?!.*--)[a-z0-9-]{1,61}[a-z0-9]$", message
	// = "Container can only have Lowercase letters, numbers and hyphens.")
	private String mAzureContainer;

	private String mAzureAutoCreateContainerCheck;
	private String mAzureNetworkZone;
	private String mAzureDownloadObjectKey;
	private String mAzureDownloadPattern;
	private String mAzurePatternType;

	@NotNull(message = "Azure Upload Destination cannot be null")
	// @Pattern(regexp = "(^[^/](.)+[/]$)|(^$)", message = "\nUpload Destination
	// should not start with (forward slash) symbol \"/\".\nHowever, it should
	// end with \"/\" symbol.")
	private String mAzureUploadDestination;
	private String mAzureUploadMode;
	private String mAzureStorageMetadata;
	private String mAzureCustomMetadata;

	public String getAzureAccountName() {
		return mAzureAccountName;
	}

	public void setAzureAccountName(String mAzureAccountName) {
		this.mAzureAccountName = mAzureAccountName;
	}

	@Encrypted
	public String getAzureAccountKey() {
		return mAzureAccountKey;
	}

	@Encrypted
	public void setAzureAccountKey(String mAzureAccountKey) {
		this.mAzureAccountKey = mAzureAccountKey;
	}

	public String getAzureContainer() {
		return mAzureContainer;
	}

	public void setAzureContainer(String mAzureContainer) {
		this.mAzureContainer = mAzureContainer;
	}

	public String getAzureAutoCreateContainerCheck() {
		return mAzureAutoCreateContainerCheck;
	}

	public void setAzureAutoCreateContainerCheck(String mAutoCreateContainerCheck) {
		this.mAzureAutoCreateContainerCheck = mAutoCreateContainerCheck;
	}

	public String getAzureNetworkZone() {
		return mAzureNetworkZone;
	}

	public void setAzureNetworkZone(String mAzureNetworkZone) {
		this.mAzureNetworkZone = mAzureNetworkZone;
	}

	public String getAzureDownloadObjectKey() {
		return mAzureDownloadObjectKey;
	}

	public void setAzureDownloadObjectKey(String mAzureDownloadObjectKey) {
		this.mAzureDownloadObjectKey = mAzureDownloadObjectKey;
	}

	public String getAzureDownloadPattern() {
		return mAzureDownloadPattern;
	}

	public void setAzureDownloadPattern(String mAzureDownloadPattern) {
		this.mAzureDownloadPattern = mAzureDownloadPattern;
	}

	public String getAzurePatternType() {
		return mAzurePatternType;
	}

	public void setAzurePatternType(String mAzurePatternType) {
		this.mAzurePatternType = mAzurePatternType;
	}

	public String getAzureUploadDestination() {
		return mAzureUploadDestination;
	}

	public void setAzureUploadDestination(String mAzureUploadDestination) {
		this.mAzureUploadDestination = mAzureUploadDestination;
	}

	public String getAzureUploadMode() {
		return mAzureUploadMode;
	}

	public void setAzureUploadMode(String mAzureUploadMode) {
		this.mAzureUploadMode = mAzureUploadMode;
	}

	public String getAzureStorageMetadata() {
		return mAzureStorageMetadata;
	}

	public void setAzureStorageMetadata(String mAzureStorageMetadata) {
		this.mAzureStorageMetadata = mAzureStorageMetadata;
	}

	public String getAzureCustomMetadata() {
		return mAzureCustomMetadata;
	}

	public void setAzureCustomMetadata(String mAzureCustomMetadata) {
		this.mAzureCustomMetadata = mAzureCustomMetadata;
	}

}
