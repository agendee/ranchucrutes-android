package br.com.wjaa.ranchucrutes.vo;

/**
 * Created by wagner on 03/11/15.
 */
public class ItemListVo {

    public String id;
    public String content;

    public ItemListVo(String id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
