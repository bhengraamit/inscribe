package com.inscribe.inscribefilestorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FileService {

    private final FileMetadataRepository fileMetadataRepository;
    private final Path fileStoragePath;

    @Autowired
    public FileService(FileMetadataRepository fileMetadataRepository, @Value("${file.storage.path}") String fileStoragePath) throws Exception {
        this.fileMetadataRepository = fileMetadataRepository;
        this.fileStoragePath = Paths.get(fileStoragePath).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStoragePath);
        } catch (Exception ex) {
            throw new Exception("Could not create the directory to store the uploaded files.", ex);
        }
    }

    public FileMetadata storeFile(MultipartFile file, Map<String, String> metadata) throws Exception {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new Exception("Invalid file path: " + fileName);
            }

            Path targetLocation = this.fileStoragePath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);

            FileMetadata fileMetadata = new FileMetadata();
            fileMetadata.setFileName(fileName);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode metadataNode = mapper.createObjectNode();

            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                metadataNode.put(entry.getKey(), entry.getValue());
            }

            fileMetadata.setMetadata(metadataNode);

            return fileMetadataRepository.save(fileMetadata);
        } catch (IOException ex) {
            throw new Exception("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Optional<FileMetadata> getFile(Long id) {
        return fileMetadataRepository.findById(id);
    }


    public List<FileMetadata> getFilesByMetadata(Map<String, String> metadata){
        return fileMetadataRepository.findByMetadataContaining(metadata);
    }


//    public List<FileMetadata> getFilesByCategory(String category) {
//        return fileMetadataRepository.findByCategory(category);
//    }

    public boolean deleteFile(Long id) throws Exception {
        Optional<FileMetadata> optionalFileMetadata = fileMetadataRepository.findById(id);

        if (optionalFileMetadata.isPresent()) {
            FileMetadata fileMetadata = optionalFileMetadata.get();

            try {
                Path targetLocation = this.fileStoragePath.resolve(fileMetadata.getFileName());
                Files.delete(targetLocation);

                fileMetadataRepository.delete(fileMetadata);
            } catch (IOException ex) {
                throw new Exception("Could not delete file " + fileMetadata.getFileName() + ". Please try again!", ex);
            }
        }
        return true;
    }
}
