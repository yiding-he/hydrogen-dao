package com.hyd.daotest.bean;

/**
 * (description)
 * created at 2014/11/18
 *
 * @author Yiding
 */
public class LobRecord {

    private Long id;

    private byte[] blobContent;

    private String clobContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getBlobContent() {
        return blobContent;
    }

    public void setBlobContent(byte[] blobContent) {
        this.blobContent = blobContent;
    }

    public String getClobContent() {
        return clobContent;
    }

    public void setClobContent(String clobContent) {
        this.clobContent = clobContent;
    }
}
