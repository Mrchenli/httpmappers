package mrchenli.proxy;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.reflect.Reflection;
import mrchenli.config.Configuration;
import mrchenli.crypt.des.DesService;
import mrchenli.crypt.rsa.RsaService;
import mrchenli.crypt.rsa.SignService;
import mrchenli.crypt.rsa.bean.RsaBean;
import mrchenli.crypt.rsa.bean.SignBean;
import mrchenli.handler.PostProcessor;
import mrchenli.http.executor.HttpExecutor;
import mrchenli.propertiesconfig.Config;
import mrchenli.propertiesconfig.ConfigManager;
import mrchenli.request.MapperRequest;
import mrchenli.request.param.*;
import mrchenli.utils.MapperRequestKeyUtil;
import mrchenli.utils.ObjectFieldSortUtil;
import mrchenli.utils.ReflectUtil;
import mrchenli.utils.StringUtil;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 这个类主要用来生成mapper接口的代理对象的 用jdk代理就好了
 */
public class MapperProxyFactory extends AbstractInvocationHandler {

	private static Logger logger = LoggerFactory.getLogger(MapperProxyFactory.class);

	private final Configuration configuration;
	private final HttpExecutor httpExecutor;

	public static <T> T newProxy(Configuration configuration, Class<T> interfaceType) {
		return Reflection.newProxy(interfaceType, new MapperProxyFactory(configuration));
	}

	public MapperProxyFactory(Configuration configuration) {
		this.configuration = configuration;
		this.httpExecutor = configuration.getHttpExecutor();
	}

	@Override
	protected Object handleInvocation(Object o, Method method, Object[] args) throws Throwable {
		String mrKey = MapperRequestKeyUtil.getKey(method);
		MapperRequest mapperRequest = configuration.getMapperRequest(mrKey);
		checkNotNull(mapperRequest);
		HttpRequestBean requestBean = resolveRequestParameter(mapperRequest, method, args);
		HttpResponse response = httpExecutor.execute(mapperRequest, requestBean);
		logger.info("response statusline is ==>{}", response.getStatusLine());
		Object target = mapperRequest.getResponseHandler().handle(mapperRequest, response);
		//执行后置的操作 然后返回
		return invokePostProcessAfter(mapperRequest, requestBean, target, mapperRequest.getPostProcessors());
	}


	private HttpRequestBean resolveRequestParameter(MapperRequest request, Method method, Object[] args) {
		HttpRequestBean httpRequestBean = new HttpRequestBean();
		try {
			final Object paramObject;
			final Map<String, String> headers;
			final Map<String, String> urlParams;
			if (args == null || args.length == 0) {
				paramObject = Collections.emptyMap();
				headers = Collections.emptyMap();
				urlParams = Collections.emptyMap();
			} else {
				final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
				final Map<String, Object> tmpParams = Maps.newHashMapWithExpectedSize(args.length);
				final Map<String, String> tempHeaders = Maps.newHashMap();
				final Map<String, String> tempUrlParams = Maps.newHashMap();
				outer:
				for (int i = 0; i < parameterAnnotations.length; i++) {
					Annotation[] annotations = parameterAnnotations[i];
					for (Annotation annotation : annotations) {
						if (annotation instanceof RsaParam) {
							String param = ((RsaParam) annotation).value();
							RsaBean rsaBean = doRsaDesPriPub(ConfigManager.getConfig(request.getConfigKey()), args[i]);
							tmpParams.put(param, rsaBean.getRsa_string());
							tmpParams.put(((RsaParam) annotation).signName(), rsaBean.getSign());
							tmpParams.put(((RsaParam) annotation).desKeyName(), rsaBean.getDes_key());
							continue outer;
						}
						if (annotation instanceof RsaPublicParam) {
							String param = ((RsaPublicParam) annotation).value();
							RsaBean rsaBean = doRsaPubSign(ConfigManager.getConfig(request.getConfigKey()), args[i]);
							tmpParams.put(param, rsaBean.getRsa_string());
							tmpParams.put(((RsaPublicParam) annotation).signName(), rsaBean.getSign());
							continue outer;
						}
						if (annotation instanceof SignParam) {//todo
							SignBean signBean = doSign(ConfigManager.getConfig(request.getConfigKey()), args[i]);
							String param = ((SignParam) annotation).value();
							if (StringUtil.isEmpty(param)) {
								ReflectUtil.objectToMap(tmpParams, tempHeaders, tempUrlParams, args[i]);
							} else {
								tmpParams.put(param, args[i]);
							}
							tmpParams.put(((SignParam) annotation).signName(), signBean.getSign());
							continue outer;
						}
						if (annotation instanceof ReqParam) {
							tmpParams.put(((ReqParam) annotation).value(), args[i]);
							continue outer;
						}

						if (annotation instanceof HeaderParam) {
							if (args[i] instanceof String) {
								tempHeaders.put(((HeaderParam) annotation).value(), (String) args[i]);
							}
						}
						if (annotation instanceof UrlParam) {
							if (args[i] instanceof String) {
								tempUrlParams.put(((UrlParam) annotation).value(), (String) args[i]);
							}
						}
					}
					ReflectUtil.objectToMap(tmpParams, tempHeaders, tempUrlParams, args[i]);
				}
				paramObject = tmpParams;
				headers = tempHeaders;
				urlParams = tempUrlParams;
			}
			httpRequestBean.setParam(paramObject);
			httpRequestBean.setHeaders(headers);
			httpRequestBean.setUrlParams(urlParams);
			return httpRequestBean;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public Object invokePostProcessAfter(MapperRequest request, HttpRequestBean requestBean, Object target, List<PostProcessor> processors) {
		for (PostProcessor postProcessor : processors) {
			target = postProcessor.handleAfter(request, requestBean, target);
		}
		return target;
	}

	/**
	 * des 加密 业务参数
	 * rsa 加密  des_key
	 *
	 * @param config
	 * @param data
	 * @return
	 */
	public RsaBean doRsaDesPriPub(Config config, Object data) {
		checkNotNull(data);
		checkNotNull(config);
		String rsaData;
		if (data instanceof String) {
			rsaData = (String) data;
		} else {
			rsaData = JSONObject.toJSONString(data);
		}
		logger.info("des加密前的数据是==>{}", rsaData);
		RsaService rsaService = config.getRsaService();
		DesService desService = config.getDesService();

		String desKey = desService.getRandomDesKey(desService.getKeyLength());

		String biz_data = desService.encrypt(rsaData, desKey);
		String des_key = rsaService.encrypt(desKey);
		String sign = rsaService.generateSign(biz_data);
		return new RsaBean(sign, des_key, biz_data);
	}

	/**
	 * rsa 对方公钥 加密业务数据
	 * rsa 己方私钥 加签业务数据
	 *
	 * @param config
	 * @param data
	 * @return
	 */
	public RsaBean doRsaPubSign(Config config, Object data) {
		checkNotNull(data);
		checkNotNull(config);
		String rsaData;
		if (data instanceof String) {
			rsaData = (String) data;
		} else {
			rsaData = JSONObject.toJSONString(data);
		}
		logger.info("rsa加密前的数据是==>{}", rsaData);
		RsaService rsaService = config.getRsaService();
		String encData = rsaService.encrypt(rsaData);
		String sign = rsaService.generateSign(rsaData);
		return new RsaBean(sign, "", encData);
	}


	public SignBean doSign(Config config, Object data) {
		checkNotNull(data);
		checkNotNull(config);
		String rsaData;
		if (data instanceof String) {
			rsaData = (String) data;
		} else if (Map.class.isAssignableFrom(data.getClass()) || List.class.isAssignableFrom(data.getClass())) {
			rsaData = JSONObject.toJSONString(data);
		} else {
			//rsaData = JSONObject.toJSONString(data);
			SignService signService = config.getSignService();
			if (signService != null) {
				rsaData = signService.objParamSort(data);
				String sign = signService.generateSign(rsaData);
				return new SignBean(sign);
			} else {
				rsaData = ObjectFieldSortUtil.getSignString(data);
			}
		}
		RsaService rsaService;
		//这里适配下变态的给把他们私钥给我们用来签名的
		if (config.getPrivateKey() == null) {
			rsaService = config.getRsaService();
		} else {
			rsaService = config.getThirdRsaService();
		}
		String sign = rsaService.generateSign(rsaData);
		return new SignBean(sign);
	}


}
