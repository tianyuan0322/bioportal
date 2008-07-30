use cbio;

delete from ncbouserrole where userid in (select id from ncbouser where lower(login) = 'lfagan');
delete from ncbouser where lower(login) = 'lfagan';

use bioportal;

insert into ncbo_user (id, username, password, email, firstname, lastname, phone)
select id, login, password, email, firstname, lastname, phone 
from cbio.ncbouser;

insert into ncbo_user_role (id, user_id, role_id) 
select id, userid, roleid 
from cbio.ncbouserrole;

alter table ncbo_ontology
	add column metadatainfo_id int not null;

insert into ncbo_ontology (id, metadatainfo_id) values (999, 1);
delete from ncbo_ontology;

insert into ncbo_ontology 
	(is_manual, metadatainfo_id)
select 
	0, id
from
	cbio.ncbofilemetadatainfo fm;

SET FOREIGN_KEY_CHECKS = 0;

insert into ncbo_ontology_version 
	(id, ontology_id, user_id, internal_version_number, version_number, version_status, file_path, is_remote, is_reviewed, date_released, date_created)
select 	
	f.id,
	o.id,
	f.userid,
	fvi.versionnumber,
	fm.currentversion, 
	fm.currentversionstatus,
	f.filepath, 
	f.isremote, 
	1, 
	f.releasedate, 
	f.creationdate
from 
	cbio.ncbofile f 
	inner join cbio.ncbofileversioninfo fvi on f.id = fvi.id
	inner join cbio.ncbofilemetadatainfo fm on f.metadadatainfoid = fm.id	
	inner join ncbo_ontology o on fm.id = o.metadatainfo_id
	inner join (
		select 
			f.metadadatainfoid, 
			max(fvi.versionnumber) versionnumber
		from 
			cbio.ncbofile f 
			inner join cbio.ncbofileversioninfo fvi on f.id = fvi.id
			inner join cbio.ncbofilemetadatainfo fm on f.metadadatainfoid = fm.id
		group by 
			f.metadadatainfoid
	) b on fm.id = b.metadadatainfoid and fvi.versionnumber = b.versionnumber;

alter table ncbo_ontology
	drop column metadatainfo_id;

SET FOREIGN_KEY_CHECKS = 1;

delete from ncbo_user_role where user_id not in (select distinct user_id from ncbo_ontology_version);
delete from ncbo_user where id not in (select distinct user_id from ncbo_ontology_version);
update ncbo_user set password = 'MaT9mL6DdWI6s471OFTiJUEZlZz6Ycx0OjXfNZZPHFMic8n5uAeyd9GCLKPstDjQ';

insert into ncbo_ontology_category (id, ontology_version_id, category_id)
select 	fc.id, o.id, fc.categoryid 
from cbio.ncbofilecategory fc, ncbo_ontology_version o
where o.id = fc.ncbofileid;

insert into ncbo_ontology_version_metadata (ontology_version_id, display_label, format, contact_name, contact_email, homepage, documentation, publication, urn, is_foundry)
select
	o.id,
	fm.displaylabel,
	fm.format,
	fm.contactname, 
	fm.contactemail, 
	fm.homepage, 
	fm.documentation, 
	fm.publication,
	fm.urn,
	case isfoundry
		when 'Y' then 1
		when 'N' then 0
	end as is_foundry
from 
	ncbo_ontology_version o 
	inner join cbio.ncbofile f on o.id = f.id
	inner join cbio.ncbofilemetadatainfo fm on f.metadadatainfoid = fm.id;

insert into ncbo_ontology_file (id, ontology_version_id, filename)
select fn.id, o.id, fn.filename
from ncbo_ontology_version o inner join cbio.ncbofilenames fn on o.id = fn.ncbofile;

update ncbo_ontology_version set internal_version_number = 1;

DELIMITER $$

DROP PROCEDURE IF EXISTS `sp_remove_duplicate_ontologies`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_remove_duplicate_ontologies`()
BEGIN
		DECLARE ontologyVersionId INT;
		DECLARE recordCount INT;
		DECLARE iterCount INT DEFAULT 1;

		DECLARE cur1 CURSOR FOR select ontology_version_id from ncbo_ontology_version_metadata where display_label = 'Unit' order by ontology_version_id;
		select count(ontology_version_id) into recordCount from ncbo_ontology_version_metadata where display_label = 'Unit';
		
		IF recordCount > 1 THEN
			OPEN cur1;
	  
			REPEAT
				FETCH cur1 INTO ontologyVersionId;
				
				delete from ncbo_ontology_category where ontology_version_id = ontologyVersionId;
				delete from ncbo_ontology_file where ontology_version_id = ontologyVersionId;
				delete from ncbo_ontology_load_queue where ontology_version_id = ontologyVersionId;
				delete from ncbo_ontology_version_metadata where ontology_version_id = ontologyVersionId;
				delete from ncbo_ontology_version where id = ontologyVersionId;
			
				SET iterCount := iterCount + 1;
			UNTIL iterCount = recordCount
			END REPEAT;
			CLOSE cur1;
		END IF;
	END$$

DELIMITER ;

CALL sp_remove_duplicate_ontologies();

DROP PROCEDURE IF EXISTS `sp_remove_duplicate_ontologies`;

update ncbo_ontology_version set file_path = CONCAT("/", ontology_id, "/", internal_version_number)
where is_remote = 0;

update ncbo_ontology_version_metadata set format = 'LEXGRID-XML' where upper(format) = 'LEXGRID_XML';

DELIMITER $$

DROP PROCEDURE IF EXISTS `sp_remove_gene_ontology`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_remove_gene_ontology`()
BEGIN
		DECLARE ontologyVersionId INT;
		DECLARE ontologyId INT;

		select ontology_version_id INTO ontologyVersionId FROM ncbo_ontology_version_metadata WHERE display_label = 'Gene Ontology';
		select ontology_id INTO ontologyId FROM ncbo_ontology_version WHERE id = ontologyVersionId;
				
		delete from ncbo_ontology_category where ontology_version_id = ontologyVersionId;
		delete from ncbo_ontology_file where ontology_version_id = ontologyVersionId;
		delete from ncbo_ontology_load_queue where ontology_version_id = ontologyVersionId;
		delete from ncbo_ontology_version_metadata where ontology_version_id = ontologyVersionId;
		delete from ncbo_ontology_version where ontology_id = ontologyId;
		delete from ncbo_ontology where id = ontologyId;

	END$$

DELIMITER ;

CALL sp_remove_gene_ontology();

DROP PROCEDURE IF EXISTS `sp_remove_gene_ontology`;
