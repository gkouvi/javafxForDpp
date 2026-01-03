package gr.uoi.dit.master2025.gkouvas.dppclient.model;

import java.util.List;

public class PageResponse<T> {

    private List<T> content;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}

