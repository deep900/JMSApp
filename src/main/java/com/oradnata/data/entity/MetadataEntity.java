/**
 * 
 */
package com.oradnata.data.entity;

import java.sql.Clob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity to map the metadata.
 */
@Entity
@Table(name = "GFF_NOTIFICATIONMSG_TRACK")
@ToString
@NoArgsConstructor
@Getter
@Setter
public class MetadataEntity {
	
	  @Id	  
	  @Column(name = "SEQ_ID")	 
	private String seqId;

	@Column(name = "FILE_PATH")
	private String filePath;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "CREATED_DATE")
	private String createdDate;

	@Column(name = "UPDATED_DATE")
	private String updatedDate;

	@Column(name = "FLAG")
	private String flag;

	@Column(name = "OIC_INSTANCE_ID")
	private String oicInstanceId;

	@Column(name = "ERROR_MSG")
	private Clob errorMsg;

	@Column(name = "ATTRIBUTE1")
	private String attribute1;

	@Column(name = "ATTRIBUTE2")
	private String attribute2;

	@Column(name = "ATTRIBUTE3")
	private String attribute3;
}
