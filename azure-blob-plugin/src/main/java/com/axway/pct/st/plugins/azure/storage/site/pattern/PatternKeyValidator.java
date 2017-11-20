package com.axway.pct.st.plugins.azure.storage.site.pattern;

public abstract interface PatternKeyValidator {
	
	public abstract boolean isValid(String input, String pattern);

}
