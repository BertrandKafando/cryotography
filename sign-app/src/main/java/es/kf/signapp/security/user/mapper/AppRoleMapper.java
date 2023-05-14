package es.kf.signapp.security.user.mapper;
import es.kf.signapp.security.user.dto.AppRoleDTO;

import es.kf.signapp.security.user.model.AppRole;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AppRoleMapper {
	AppRoleDTO toDto(AppRole model);

	AppRole toModel (AppRoleDTO dto);
}
