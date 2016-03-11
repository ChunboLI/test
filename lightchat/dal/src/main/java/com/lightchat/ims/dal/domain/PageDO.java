package com.lightchat.ims.dal.domain;

/**
 * 分页实体类
 * @author chunbo
 *
 * @version $Id: PageDO.java, v 0.1 2016年3月7日 下午2:45:22 chunbo Exp $
 */
public class PageDO {

    /**每页显示条数*/
    private Integer pageSize;

    /**set:当前页码   ,get:当前分页起始数*/
    private Integer pageIndex;

    /** 分页起始页*/
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