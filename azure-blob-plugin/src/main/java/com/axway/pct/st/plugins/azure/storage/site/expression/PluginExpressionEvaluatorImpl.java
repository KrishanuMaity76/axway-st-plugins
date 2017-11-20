package com.axway.pct.st.plugins.azure.storage.site.expression;

import java.util.Map;

import org.apache.log4j.Logger;

import com.tumbleweed.st.util.expressions.STExpressionEvaluator;

class PluginExpressionEvaluatorImpl implements PluginExpressionEvaluator {

	private static final Logger log = Logger.getLogger(PluginExpressionEvaluatorImpl.class);
	private static final String LOGGER_KEY = "[PluginExpressionEvaluator]: ";
	private static final String PLUGIN_NAMESPACE = "site";
	private STExpressionEvaluator mStExprEval = new STExpressionEvaluator();

	PluginExpressionEvaluatorImpl(Map<String, String> rawEnv) {
		this.mStExprEval.addVariable(PLUGIN_NAMESPACE, rawEnv);
	}

	@Override
	public String getExpressionEvaluatedValue(String expression) {
		String evaluatedValue = "";
		try {
			evaluatedValue = this.mStExprEval.evaluate(expression);
			if (log.isDebugEnabled())
				log.debug(LOGGER_KEY + "Currently evaluated expression from [" + expression + "] to [" + evaluatedValue + "]");
		} catch (Exception e) {
			String errorMsg = "Could not evaluate value from expression: " + expression;
			log.error(LOGGER_KEY + errorMsg, e);
		}

		return evaluatedValue;
	}

}
