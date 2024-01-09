/**
 * 
 */
package com.oradnata.data.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataEntityRepository extends CrudRepository<MetadataEntity,String> {

}
