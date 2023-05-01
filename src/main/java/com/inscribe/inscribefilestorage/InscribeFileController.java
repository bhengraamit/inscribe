package com.inscribe.inscribefilestorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class InscribeFileController {



        @Autowired
        private FileService fileService;

        @PostMapping("/files")
        public ResponseEntity<FileMetadata> uploadFile(@RequestParam("file") MultipartFile file,
                                               @RequestParam Map<String, String> metadata) throws Exception {
            FileMetadata savedFile = fileService.storeFile(file, metadata);
            return ResponseEntity.created(URI.create("/files/" + savedFile.getId())).body(savedFile);
        }

        @GetMapping("/files/{id}")
        @RequestMapping()
        public ResponseEntity<FileMetadata> getFileById(@PathVariable Long id) {
            Optional<FileMetadata> optionalFile = fileService.getFile(id);
            if (optionalFile.isPresent()) {
                return ResponseEntity.ok(optionalFile.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/files")
        public ResponseEntity<List<FileMetadata>> getFilesByMetadata(@RequestParam Map<String, String> metadata) {
            List<FileMetadata> matchingFiles = fileService.getFilesByMetadata(metadata);
            return ResponseEntity.ok(matchingFiles);
        }

        @DeleteMapping("/files/{id}")
        public ResponseEntity<Void> deleteFile(@PathVariable Long id) throws Exception {
            boolean success = fileService.deleteFile(id);
            if (success) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }

}
