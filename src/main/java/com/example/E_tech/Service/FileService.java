package com.example.E_tech.Service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;

    // Upload file to GridFS
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

        ObjectId fileId = gridFsTemplate.store(
                multipartFile.getInputStream(),
                uniqueFilename,
                multipartFile.getContentType()
        );

        return fileId.toHexString(); // Return the unique file ID
    }

    // Fetch file metadata
    public GridFSFile getFileById(String fileId) {
        return gridFsTemplate.findOne(
                org.springframework.data.mongodb.core.query.Query.query(
                        org.springframework.data.mongodb.core.query.Criteria.where("_id").is(new ObjectId(fileId))
                )
        );
    }

    // Download file as InputStream
    public InputStream downloadFile(String fileId) throws IOException {
        GridFSFile file = getFileById(fileId);

        if (file == null) {
            throw new IllegalArgumentException("File not found with ID: " + fileId);
        }

        GridFsResource resource = gridFsOperations.getResource(file);
        return resource.getInputStream();
    }
}
