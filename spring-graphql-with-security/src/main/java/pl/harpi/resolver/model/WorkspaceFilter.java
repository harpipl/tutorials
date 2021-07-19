package pl.harpi.resolver.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public final class WorkspaceFilter {
    private Long id;

    private String name;

    private String nameLike;

    public WorkspaceFilter(WorkspaceFilter workspaceFilter) {
        if (workspaceFilter != null) {
            this.id = workspaceFilter.getId();
            this.name = workspaceFilter.getName();
            this.nameLike = workspaceFilter.getNameLike();
        }
    }
}
