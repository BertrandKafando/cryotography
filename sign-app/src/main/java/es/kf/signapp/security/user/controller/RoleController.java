package es.kf.signapp.security.user.controller;

import es.kf.signapp.exceptions.RecordNotFoundException;
import es.kf.signapp.security.user.dto.AppRoleDTO;
import es.kf.signapp.security.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
public class RoleController {
     private final RoleService service;

    @GetMapping
    public List<AppRoleDTO> getAll(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        return service.getPage(page, size);
    }

    @PostMapping
    public AppRoleDTO add(@RequestBody AppRoleDTO role) {
        return service.add(role);
    }

    @GetMapping("/{id}")
    public AppRoleDTO getById(@PathVariable Long id) {
        AppRoleDTO roleDTO= service.getById(id);
        if(roleDTO==null) {
            throw new RecordNotFoundException("City not found");
        }
        return roleDTO;
    }

    @PutMapping("/{id}")
    public AppRoleDTO update(@PathVariable Long id,@RequestBody AppRoleDTO roleDTO) {
        return service.update(id,roleDTO);
    }

    @DeleteMapping("/{id}")
    public boolean deleteCity(@PathVariable Long id) {
        if(service.getById(id)==null){
            throw new RecordNotFoundException("La ville que vous cherchez n'existe pas");
        }
        return service.deleteCity(id);
    }
}
