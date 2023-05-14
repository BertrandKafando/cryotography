package es.kf.signapp.mappers;
import es.kf.signapp.dtos.GetWorklfowDTO;
import es.kf.signapp.dtos.WorkflowDTO;
import es.kf.signapp.model.Workflow;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkflowMapper {
    GetWorklfowDTO toDto(Workflow model);

    WorkflowDTO toDto2(Workflow model);
    Workflow toModel (WorkflowDTO dto);
}
