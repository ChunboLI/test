package com.lightchat.ims.web.filter;

import java.beans.PropertyEditorSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

/**
 * 用于解析*.acf文件。该文件主要用于定义角色
 * 
 * @author calvin.lil@alibaba-inc.com
 * @version $Id: AlipayFilterInvocationDefinitionSourceEditor.java,v 1.2
 *          2006/01/23 06:06:43 calvin Exp $
 * @author dongmin
 * @version 2.0
 * @author wgyi
 * @version 2015年6月11日
 */
public class FilterInvocationDefinitionSourceEditor extends PropertyEditorSupport implements InitializingBean {
	private static final Log logger = LogFactory.getLog(FilterInvocationDefinitionSourceEditor.class);
	/** key/value的分隔符 **/
	private String seperator = "=";

	private List<Resource> locations;// 资源配置

	/**
	 * Set a location of a properties file to be loaded.
	 * <p>
	 * Can point to a classic properties file or to an XML file that follows JDK
	 * 1.5's properties XML format.
	 */
	public void setLocation(Resource location) {
		this.locations = new ArrayList<Resource>();
		this.locations.add(location);
	}

	/**
	 * Set locations of properties files to be loaded.
	 * <p>
	 * Can point to classic properties files or to XML files that follow JDK
	 * 1.5's properties XML format.
	 */
	public void setLocations(List<Resource> locations) {
		this.locations = locations;
	}

	/**
	 * @param s 配置文件地址
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	public void setAsText(String s) throws IllegalArgumentException {
		//对传入的配置文件地址进行转换
		String[] locations = s.split(",");
		this.locations = new ArrayList<Resource>();
		for (String string : locations) {
			this.locations.add((Resource) new ClassPathResource(string));
		}
		FilterInvocationDefinitionSource source = new FilterInvocationDefinitionSource();
		try {
			s = readAcfFile();

			BufferedReader br = new BufferedReader(new StringReader(s));
			int counter = 0;
			String line;

			while (true) {
				counter++;

				try {
					line = br.readLine();
				} catch (IOException ioe) {
					throw new IllegalArgumentException(ioe.getMessage());
				}

				if (line == null) {
					break;
				}

				line = line.trim();

				if (logger.isDebugEnabled()) {
					logger.debug("Line " + counter + ": " + line);
				}

				if (line.startsWith("//")) {
					continue;
				}

				if (line.startsWith("CONVERT_URL_TO_")) {
					String pattern = org.apache.commons.lang.StringUtils.substringAfter(line, "CONVERT_URL_TO_");
					if (logger.isDebugEnabled()) {
						logger.debug("Line " + counter + ": Instructing mapper to convert URLs to " + pattern);
					}

					source.setConvertUrlToPattern(pattern);

					continue;
				}

				if (!StringUtils.contains(line, seperator)) {
					continue;
				}

				// Tokenize the line into its name/value tokens
				String[] nameValue = line.split(seperator);
				String name = nameValue[0];
				String value = nameValue[1];

				// Convert value to series of security configuration attributes
				// ConfigAttributeEditor configAttribEd = new
				// ConfigAttributeEditor();
				// configAttribEd.setAsText(value);

				Collection<ConfigAttribute> attr = (Collection<ConfigAttribute>) SecurityConfig.createList(value.split(","));

				// Register the regular expression and its attribute
				source.addConfigEntry(name, attr);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		setValue(source);
	}

	/**
	 * @param seperator
	 */
	public void setSeperator(String seperator) {
		this.seperator = seperator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		
	}

	/**
	 * 根据配置信息读取权限映射文件
	 * @return
	 * @throws IOException
	 */
	private String readAcfFile() throws IOException{
		if (locations == null) {
			throw new IllegalArgumentException("location or locations can't be null!");
		}

		// 所有的配置文件都会加载到这个StringBuffer中
		StringBuffer allResourcesBuffer = new StringBuffer();
		Velocity.init();
		VelocityContext context = new VelocityContext();
		for (Resource resource : locations) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}

				allResourcesBuffer.append(line + "\n");
			}
		}

		// 将配置文件当作一个Velocity模板文件render一下
		StringWriter evaluatedStr = new StringWriter();
		boolean isParseSucess = Velocity.evaluate(context, evaluatedStr, "security", allResourcesBuffer.toString());
		if (!isParseSucess) {
			// throw new
			// ParseErrorException(CommonMessages.parseConfigFile("security-head.acf"));
		}
		// 用润色好的配置信息生成<code>AlipayFilterInvocationDefinitionSource<code>
		return evaluatedStr.toString();
	}
	
}
