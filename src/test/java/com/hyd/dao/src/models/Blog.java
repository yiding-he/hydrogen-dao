package com.hyd.dao.src.models;

import java.util.Date;
import java.util.Objects;

public class Blog {

    private Integer id;

    private String title;

    private String content;

    private Date lastUpdate;

    private Boolean hidden;

    public Blog() {
    }

    public Blog(Integer id, String title, String content, Boolean hidden) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.hidden = hidden;

        this.lastUpdate = new Date();
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", hidden=" + hidden +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blog blog = (Blog) o;
        return Objects.equals(id, blog.id) &&
                Objects.equals(title, blog.title) &&
                Objects.equals(content, blog.content) &&
                Objects.equals(lastUpdate, blog.lastUpdate) &&
                Objects.equals(hidden, blog.hidden);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, lastUpdate, hidden);
    }
}