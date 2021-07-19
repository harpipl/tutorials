package pl.harpi.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.harpi.model.Workspace;

import java.util.Optional;

public interface WorkspaceRepository extends PagingAndSortingRepository<Workspace, Long> {
    @NotNull Optional<Workspace> findById(@NotNull final Long id);

    @NotNull Optional<Workspace> findByName(@NotNull final String name);

    @NotNull Page<Workspace> findAllByNameStartingWith(final String nameLike, final Pageable pageable);
}
