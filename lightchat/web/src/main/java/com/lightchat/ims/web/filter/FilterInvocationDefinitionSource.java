package com.lightchat.ims.web.filter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * 一种<code>FilterInvocationDefinitionSource<code>的实现.<br>
 * 可以根据<code>HttpServletRequest<code>的PahtInfo和参数查找匹配的控制访问配置信息。<br>
 * 例如：/user/deliver_address_list.htm?oper_type=edit:ROLE_LOGIN_NORMAL&ROLE_STATUS_T<br>
 * 
 * @author calvin.lil@alibaba-inc.com
 *
 * @version $Id: AlipayFilterInvocationDefinitionSource.java,v 1.3 2006/01/25 09:02:09 calvin Exp $
 * @author dongmin 
 * @version 2.0
 */
public class FilterInvocationDefinitionSource implements FilterInvocationSecurityMetadataSource {
    private static final Log   logger   = LogFactory.getLog(FilterInvocationDefinitionSource.class);

    /**
     * 访问控制配置
     */
    private List                  requestMap                              = new Vector();

    /** 在查找匹配的配置条目之前，将URL装换成小写 */
    private static final String   URL_CASE_FOLDING_LOWER                  = "LOWER";

    /** 在查找匹配的配置条目之前，将URL转换成小写加下划线。 */
    private static final String   URL_CASE_FOLDING_LOWER_WITH_UNDERSCORES = "LOWER_WITH_UNDERSCORES";

    /** 在查找匹配的配置条目之前，将URL转换成大写。 */
    private static final String   URL_CASE_FOLDING_UPPER                  = "UPPER";

    /** 在查找匹配的配置条目之前，将URL转换成大写加下划线。 */
    private static final String   URL_CASE_FOLDING_UPPER_WITH_UNDERSCORES = "UPPER_WITH_UNDERSCORES";

    /**
     * 在查找匹配的配置条目之前，将URL转换成何种Pattern
     */
    private String                convertUrlToPattern                     = URL_CASE_FOLDING_LOWER_WITH_UNDERSCORES;

    private PathMatcher           pathMatcher                             = new AntPathMatcher();

    private EntryHolderComparator comparator                              = new EntryHolderComparator();

    /* (non-Javadoc)
     * @see net.sf.acegisecurity.intercept.ObjectDefinitionSource#getAttributes(java.lang.Object)
     */
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if ((object == null) || !this.supports(object.getClass())) {
            throw new IllegalArgumentException("Object must be a FilterInvocation");
        }

        FilterInvocation fi = (FilterInvocation) object;

        return lookupAttributes((HttpServletRequest) fi.getRequest());
    }

    /**
     * 寻找与<code>HttpServletRequest<code>匹配的配置信息
     * 
     * @param request
     * @return
     */
    protected Collection<ConfigAttribute> lookupAttributes(HttpServletRequest request) {
    	Collection<ConfigAttribute> matchedAttribute = null;

        // 获得与request.pathInfo匹配的所有配置条目
        List matchedEntries = getPathMatchedEntries(getRequestUrl(request));

        // 将路径匹配的条目集合进行排序，配置中参数个数多的排在前面，个数少的排在后面。
        Collections.sort(matchedEntries, comparator);

        for (Iterator iter = matchedEntries.iterator(); iter.hasNext();) {
            // 寻找与request的参数列表匹配的配置条目
            EntryHolder entry = (EntryHolder) iter.next();

            if (isParameterMatched(request, entry)) {
                // 找到第一个参数匹配的配置就返回
                matchedAttribute = entry.getConfigAttributeDefinition();
                break;
            }
        }

        return matchedAttribute;
    }

    /**
     * 判断request中的参数信息是否和配置匹配
     * 
     * @param request
     * @param entry
     * @return
     */
    protected boolean isParameterMatched(HttpServletRequest request, EntryHolder entry) {
        boolean matched = false;
        Map params = entry.getParamMap();
        int mathCount = 0;

        for (Iterator iterator = params.keySet().iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            String value = (String) params.get(name);
            if (request.getMethod().equalsIgnoreCase("GET")) {
                if (StringUtils.contains(request.getQueryString(), name + "=" + value)) {
                    mathCount++;
                }
            } else {
                if (StringUtils.equals(value, request.getParameter(name))) {
                    mathCount++;
                }
            }
        }

        if (mathCount == params.size()) {
            matched = true;
        } else {
            matched = false;
        }

        if (matched && logger.isDebugEnabled()) {
            logger.debug("HTTP请求: [" + getRequestUrl(request) + "], 匹配：[" + entry.getUrl() + "]");
        }

        return matched;
    }

    /**
     * 获得与<code>HttpServletRequest<code>中的PathInfo匹配的所有配置条目
     * 
     * @param path
     * @return
     */
    protected List getPathMatchedEntries(String path) {
        List configs = new ArrayList();
        Iterator iter = requestMap.iterator();

        if (convertUrlToPattern != null) {
            path = convertURL(path);
        }

        while (iter.hasNext()) {
            EntryHolder entryHolder = (EntryHolder) iter.next();
            boolean matched = pathMatcher.match(entryHolder.getPath(), path);

            if (matched) {
                configs.add(entryHolder);
            }
        }

        return configs;
    }

    /**
     * 获得<code>HttpServletRequest<code>的URL信息，不包括ContextPath。
     * 
     * @param request
     * @return
     */
    public String getRequestUrl(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();

        String uri = request.getServletPath();

        if (uri == null) {
            uri = request.getRequestURI();
            uri = uri.substring(request.getContextPath().length());
        }

        return uri + ((pathInfo == null) ? "" : pathInfo);
    }

    /**
     * @see ParserRequestContextImpl.convertCase(String str)
     * 
     * @param str
     * @return
     */
    public String convertURL(String str) {
        String oldStr = StringUtils.trimToEmpty(str);

        String newStr = str;

        if (URL_CASE_FOLDING_LOWER.equals(convertUrlToPattern)) {
            newStr = StringUtils.lowerCase(oldStr);
        } /*else if (URL_CASE_FOLDING_LOWER_WITH_UNDERSCORES.equals(convertUrlToPattern)) {
            newStr = StringUtils.toLowerCaseWithUnderscores(oldStr);
        } */else if (URL_CASE_FOLDING_UPPER.equals(convertUrlToPattern)) {
            newStr = StringUtils.upperCase(oldStr);
        } /*else if (URL_CASE_FOLDING_UPPER_WITH_UNDERSCORES.equals(convertUrlToPattern)) {
            newStr = StringUtils.toUpperCaseWithUnderscores(oldStr);
        }*/

        if (logger.isDebugEnabled()) {
            logger.debug("将HTTP URL转换为[" + convertUrlToPattern + "]样式, from: '" + oldStr
                         + "'; to: '" + newStr + "'");
        }

        return newStr;
    }

    /**
     * @param url
     * @param attr
     */
    @SuppressWarnings("unchecked")
	public void addConfigEntry(String url, Collection<ConfigAttribute> attr) {
        requestMap.add(new EntryHolder(url, attr));

        logger.info("添加访问控制条目[url=" + url + "; attributes=" + attr + "]");
    }

    /* (non-Javadoc)
     * @see net.sf.acegisecurity.intercept.ObjectDefinitionSource#getConfigAttributeDefinitions()
     */
    @SuppressWarnings("unchecked")
	public Iterator getConfigAttributeDefinitions() {
        Set set = new HashSet();
        Iterator iter = requestMap.iterator();

        while (iter.hasNext()) {
            EntryHolder entryHolder = (EntryHolder) iter.next();
            set.add(entryHolder.getConfigAttributeDefinition());
        }

        return set.iterator();
    }

    /* (non-Javadoc)
     * @see net.sf.acegisecurity.intercept.ObjectDefinitionSource#supports(java.lang.Class)
     */
    public boolean supports(Class<?> clazz) {
        if (FilterInvocation.class.isAssignableFrom(clazz)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param convertUrlToPattern
     */
    public void setConvertUrlToPattern(String convertUrlToPattern) {
        this.convertUrlToPattern = convertUrlToPattern;
    }

    /**
     * @author calvin.lil@alibaba-inc.com
     *
     * @version $Id: AlipayFilterInvocationDefinitionSource.java,v 1.3 2006/01/25 09:02:09 calvin Exp $
     */
    protected class EntryHolder {
        private Collection<ConfigAttribute> configAttributeDefinition;
        private String                    url;
        private Map                       paramMap = new HashMap();
        private String                    path;

        /**
         * @param url
         * @param attr
         */
        public EntryHolder(String url, Collection<ConfigAttribute> attr) {
            this.url = url;
            this.configAttributeDefinition = attr;
            parseUrl(url);
        }

        /**
         * @return
         */
        public String getUrl() {
            return url;
        }

        /**
         * @return
         */
        public String getPath() {
            return path;
        }

        /**
         * @return
         */
        public Map getParamMap() {
            return Collections.unmodifiableMap(paramMap);
        }

        /**
         * @return
         */
        public Collection<ConfigAttribute> getConfigAttributeDefinition() {
            return configAttributeDefinition;
        }

        /**
         * 将给定的URL解析成path和parameter<br>
         * 如：/user/forgot_validator.htm?ac=987c67dc43f50b0519a23eaf5d5d62e2&fc=Q&email=calvin.lil@alibaba-inc.com<br>
         * 解析成 path=/user/forgot_validator.htm <br>
         *       paramMap={ac=987c67dc43f50b0519a23eaf5d5d62e2&fc=Q&email=calvin.lil@alibaba-inc.com}
         * 
         * @param url
         */
        protected void parseUrl(String url) {
            path = StringUtils.substringBefore(url, "?");

            if (StringUtils.indexOf(url, "?") < 0) {
                return;
            }

            String paramStr = StringUtils.substringAfter(url, "?");
            String[] params = StringUtils.split(paramStr, "&");
            for (int i = 0; i < params.length; i++) {
                String[] tokens = StringUtils.split(params[i], "=");
                if (tokens.length != 2) {
                    throw new IllegalArgumentException("解析控制访问配置列表出错：[" + url + "]格式不正确！");
                }

                paramMap.put(tokens[0], tokens[1]);
            }
        }
    }

    /**
     * 根据EntryHolder中参数的个数决定EntryHolder在集合中的位置。<br>
     * 参数个数多的排在前面，参数个数少的排在后面
     * 
     * @author calvin.lil@alibaba-inc.com
     *
     * @version $Id: AlipayFilterInvocationDefinitionSource.java,v 1.3 2006/01/25 09:02:09 calvin Exp $
     */
    protected class EntryHolderComparator implements Comparator {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {
            if (!(o1 instanceof EntryHolder) || !(o2 instanceof EntryHolder)) {
                throw new IllegalArgumentException("o1 and o2 must be EntryHolder object");
            }

            int paramCount1 = ((EntryHolder) o1).getParamMap().size();
            int paramCount2 = ((EntryHolder) o2).getParamMap().size();

            // 参数个数多的排在前面，参数个数少的排在后面
            if (paramCount1 == paramCount2) {
                return 0;
            } else if (paramCount1 < paramCount2) {
                return 1;
            } else {
                return -1;
            }
        }
    }

	public Collection<ConfigAttribute> getAllConfigAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

}
