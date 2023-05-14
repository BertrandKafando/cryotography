package es.kf.signapp.mappers;

import es.kf.signapp.dtos.DocumentDTO;
import es.kf.signapp.model.Document;
import org.apache.commons.io.FileUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.io.File;
import java.util.Base64;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    DocumentDTO toDto (Document dto);
    Document toModel (DocumentDTO dto);

    // after mapping
    @AfterMapping
    default void afterMapping(Document model , @MappingTarget DocumentDTO dto) {
        // get file from path and encode to base64
        byte[] fileContent = null;
        try {

         fileContent = FileUtils.readFileToByteArray(new File(model.getPath()));
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            dto.setBase64(encodedString);
        } catch (Exception e) {
            System.out.println("Error reading file");
        }

    }
}


