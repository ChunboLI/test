package com.lightchat.ims.dal.domain;

/**
 * ��ҳʵ����
 * @author chunbo
 *
 * @version $Id: PageDO.java, v 0.1 2016��3��7�� ����2:45:22 chunbo Exp $
 */
public class PageDO {

    /**ÿҳ��ʾ����*/
    private Integer pageSize;

    /**set:��ǰҳ��   ,get:��ǰ��ҳ��ʼ��*/
    private Integer pageIndex;

    /** ��ҳ��ʼҳ*/
    private Integer pageStartNum;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageStartNum() {
        if (pageSize == null || pageIndex == null) {
            pageStartNum = null;
        } else {
            if (pageIndex <= 0) {
                pageStartNum = 0;
            } else {
                pageStartNum = (pageIndex - 1) * pageSize;
            }
        }
        return pageStartNum;
    }

    public void setPageStartNum(Integer pageStartNum) {
        this.pageStartNum = pageStartNum;
    }
}