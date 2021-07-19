package pl.harpi.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.harpi.model.Workspace;
import pl.harpi.repository.WorkspaceRepository;
import pl.harpi.resolver.model.Pagination;
import pl.harpi.resolver.model.WorkspaceFilter;

import java.util.Collections;
import java.util.Optional;

@Component
public final class Query implements GraphQLQueryResolver {
    private final WorkspaceRepository workspaceRepository;

    @Autowired
    public Query(final WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public Iterable<Workspace> findAllWorkspaces(final WorkspaceFilter inputFilter, final Pagination inputPagination) {
        var pagination = new Pagination(inputPagination);
        var filter = new WorkspaceFilter(inputFilter);

        Pageable pageRequest = PageRequest.of(pagination.getStart(), pagination.getSize(),
                pagination.getOrder(), pagination.getSort());

        if (filter.getId() == null) {
            if (StringUtils.isEmpty(filter.getName())) {
                if (StringUtils.isEmpty(filter.getNameLike())) {
                    return workspaceRepository.findAll(pageRequest);
                } else {
                    return workspaceRepository.findAllByNameStartingWith(filter.getNameLike(), pageRequest);
                }
            } else {
                Optional<Workspace> repository = workspaceRepository.findByName(filter.getName());

                if (repository.isPresent()) {
                    return Collections.singleton(repository.get());
                }
            }
        } else {
            Optional<Workspace> repository = workspaceRepository.findById(filter.getId());

            if (repository.isPresent()) {
                return Collections.singleton(repository.get());
            }
        }

        return Collections.emptyList();
    }
}
