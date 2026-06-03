package com.fuelac.fuelac.util;

import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.dto.search.SortRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PageableUtil {

    public static Pageable createPageable(int page, int size, String sort) {
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, property));
    }

    public static Pageable fromSearchRequest(SearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        List<SortRequest> sorts = request.getSorts();
        if (sorts != null && !sorts.isEmpty()) {
            List<Sort.Order> orders = sorts.stream()
                    .map(s -> new Sort.Order(
                            "DESC".equalsIgnoreCase(s.getDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                            s.getKey()))
                    .toList();
            return PageRequest.of(page, size, Sort.by(orders));
        }
        return PageRequest.of(page, size);
    }
}
