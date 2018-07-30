package org.jianzhao.jojo.test;

/**
 * @author cbdyzj
 * @since 2018-07-30
 */
public class Box<T> {

    private String label;

    private T data;

    public void setLabel(String label) {
        this.label = label;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getLabel() {
        return label;
    }

    public T getData() {
        return data;
    }
}
