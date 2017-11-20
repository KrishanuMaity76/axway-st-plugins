package com.axway.pct.st.plugins.azure.storage.site.expression;

import com.axway.pct.st.plugins.azure.storage.bean.AzureBlobStorageBeanWrapper;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.log4j.Logger;

public final class ExpressionProxy implements InvocationHandler {
	private static Logger sLogger = Logger.getLogger(ExpressionProxy.class);
	private Object mTargetObject;

	public static Object newInstance(Object targetObject) {
		Object result = Proxy.newProxyInstance(targetObject.getClass().getClassLoader(),
				targetObject.getClass().getInterfaces(), new ExpressionProxy(targetObject));

		return result;
	}

	private ExpressionProxy(Object targetObject) {
		this.mTargetObject = targetObject;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		try {
			result = method.invoke(this.mTargetObject, args);
			boolean isAnnotationPresent = method.isAnnotationPresent(ExpressionEvaluated.class);
			if ((isAnnotationPresent) && (isCorrectResultType(result)) && ((proxy instanceof AzureBlobStorageBeanWrapper))) {
				AzureBlobStorageBeanWrapper uiWrapper = (AzureBlobStorageBeanWrapper) proxy;
				result = uiWrapper.getEvaluator().getExpressionEvaluatedValue((String) result);
			}
		} catch (InvocationTargetException e) {
			sLogger.error(e);
			throw e.getTargetException();
		} catch (Exception e) {
			throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
		}
		return result;
	}

	private boolean isCorrectResultType(Object result) {
		return (result != null) && ((result instanceof String));
	}
}