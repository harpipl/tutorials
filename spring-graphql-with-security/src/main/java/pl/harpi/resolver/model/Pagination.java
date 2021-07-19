package pl.harpi.resolver.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Getter
@NoArgsConstructor
public final class Pagination {
    @JsonProperty("sort")
    private String sort;

    @JsonProperty("order")
    private Sort.Direction order;

    @JsonProperty("start")
    private Integer start;

    @JsonProperty("size")
    private Integer size;

    public Pagination(Pagination pagination) {
        String sort = "id";
        Sort.Direction order = ASC;
        int start = 0;
        int size = 10;

        if (pagination != null) {
            sort = pagination.getSort() == null ? sort : pagination.getSort();
            order = pagination.getOrder() == null ? order : pagination.getOrder();
            start = pagination.getStart() == null ? start : pagination.getStart();
            size = pagination.getSize() == null ? size : pagination.getSize();
        }

        this.sort = sort;
        this.order = order;
        this.start = start;
        this.size = size;
    }
}
