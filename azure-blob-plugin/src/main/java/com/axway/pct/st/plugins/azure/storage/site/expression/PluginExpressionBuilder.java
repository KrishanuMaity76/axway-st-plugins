package com.axway.pct.st.plugins.azure.storage.site.expression;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class PluginExpressionBuilder {
	private static final Logger log = Logger.getLogger(PluginExpressionBuilder.class);
	private static final String LOGGER_KEY = "[PluginExpressionBuilder]: ";

	private Map<String, String> mRawEnv = new HashMap<String, String>();

	public static PluginExpressionBuilder create() {
		return new PluginExpressionBuilder();
	}

	public PluginExpressionBuilder setTarget(String target) {
		if (target == null) {
			throw new IllegalArgumentException("Target should not be null.");
		}
		this.mRawEnv.put(ExpressionKeys.target.name(), target);
		if (log.isDebugEnabled()) {
			log.debug(LOGGER_KEY + ExpressionKeys.target.name() + " expression set with: " + target);
		}
		return this;
	}
	
	public PluginExpressionBuilder setAccountName(String accountName) {
		if (accountName == null) {
			throw new IllegalArgumentException("Account Name should not be null.");
		}
		this.mRawEnv.put(ExpressionKeys.accountName.name(), accountName);
		if (log.isDebugEnabled()) {
			log.debug(LOGGER_KEY + ExpressionKeys.accountName.name() + " expression set with: " + accountName);
		}
		return this;
	}

	public PluginExpressionEvaluator build() {
		Map<String, String> msgEnv = new HashMap<String, String>(this.mRawEnv);
		log.debug(LOGGER_KEY + "Expression Evaluator created with raw env:" + msgEnv);
		return new PluginExpressionEvaluatorImpl(this.mRawEnv);
	}

	public static enum ExpressionKeys {
		target,
		accountName;
	}
}
