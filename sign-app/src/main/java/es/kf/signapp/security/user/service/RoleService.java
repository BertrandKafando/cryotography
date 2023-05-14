package es.kf.signapp.security.user.service;


import es.kf.signapp.security.user.dao.RoleDAO;
import es.kf.signapp.security.user.dto.AppRoleDTO;
import es.kf.signapp.security.user.mapper.AppRoleMapper;
import es.kf.signapp.security.user.model.AppRole;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleService {
    private  final RoleDAO dao ;
    private   AppRoleMapper mapper ;

    public AppRoleDTO add(AppRoleDTO dto){
        AppRole role= dao.save(mapper.toModel(dto));
        return mapper.toDto(role);
    }

    public List<AppRoleDTO> getAll(){
        List<AppRole> roles=dao.findAll();
        List<AppRoleDTO> list=roles.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return list ;
    }

    public List<AppRoleDTO> getPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").descending());
        List<AppRole> roles = dao.findAll(pageRequest).getContent();
        List<AppRoleDTO> list=roles.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        return list;
    }

    public AppRoleDTO getById(long id) {
        AppRole role = dao.findById(id).orElse(null);
        if(role==null){
            return null;
        }
        return mapper.toDto(role);
    }

    public boolean deleteCity(long id) {
        dao.deleteById(id);
        return true;
    }

    public AppRoleDTO update(Long id, AppRoleDTO dto) {
        AppRole role= dao.findById(id).orElse(null);
        if(role==null){
            return null;
        }
        AppRole save =dao.save(mapper.toModel(dto));
        return mapper.toDto(save);
    }
}
