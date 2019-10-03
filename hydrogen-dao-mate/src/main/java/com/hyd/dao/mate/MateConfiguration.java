package com.hyd.dao.mate;

public class MateConfiguration {

    private String srcPath;

    private String pojoPackage;

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getPojoPackage() {
        return pojoPackage;
    }

    public void setPojoPackage(String pojoPackage) {
        this.pojoPackage = pojoPackage;
    }

    @Override
    public String toString() {
        return "MateConfiguration{" +
            "srcPath='" + srcPath + '\'' +
            ", pojoPackage='" + pojoPackage + '\'' +
            '}';
    }
}
