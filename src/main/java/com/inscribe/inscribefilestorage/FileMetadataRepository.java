package com.inscribe.inscribefilestorage;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    public List<FileMetadata> findByMetadataContaining(Map<String, String> metadata);


}

