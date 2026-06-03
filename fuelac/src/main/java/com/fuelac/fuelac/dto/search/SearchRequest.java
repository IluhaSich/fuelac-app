package com.fuelac.fuelac.dto.search;

import java.util.List;

public class SearchRequest {
    private List<FilterRequest> filters;
    private List<SortRequest> sorts;
    private Integer page = 0;
    private Integer size = 20;

    public List<FilterRequest> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterRequest> filters) {
        this.filters = filters;
    }

    public List<SortRequest> getSorts() {
        return sorts;
    }

    public void setSorts(List<SortRequest> sorts) {
        this.sorts = sorts;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
