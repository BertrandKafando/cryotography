package es.kf.signapp.security.user.mapper;

import es.kf.signapp.security.user.dto.UserSummary;
import es.kf.signapp.security.user.model.AppUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSummaryMapper {
	UserSummary toDto(AppUser model);
	AppUser toModel (UserSummary dto);
}
