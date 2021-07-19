package pl.harpi.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.harpi.model.VisibilityType;
import pl.harpi.model.Workspace;
import pl.harpi.repository.WorkspaceRepository;

@Component
public final class Mutation implements GraphQLMutationResolver {
    private final WorkspaceRepository workspaceRepository;

    @Autowired
    public Mutation(final WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public Workspace createWorkspace(final String name, final String description, final VisibilityType visibility) {
        Workspace workspace = Workspace.builder()
                .name(name)
                .description(description)
                .visibility(visibility)
                .build();

        workspaceRepository.save(workspace);

        return workspace;
    }
}

