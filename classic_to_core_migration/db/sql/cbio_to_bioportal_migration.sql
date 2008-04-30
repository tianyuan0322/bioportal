use bioportal;

insert into ncbo_user (id, username, password, email, firstname, lastname, phone)
select id, login, password, email, firstname, lastname, phone 
from cbio.ncbouser;

insert into ncbo_l_category (id, name, parent_category_id)
select id, name, parentcategoryid 
from cbio.ncbocategories;

insert into ncbo_l_role (id, name, description)
select id, name, description 
from cbio.ncborole;

insert into ncbo_user_role (id, user_id, role_id) 
select id, userid, roleid 
from cbio.ncbouserrole;

SET FOREIGN_KEY_CHECKS = 0;

insert into ncbo_ontology_version 
	(id, parent_id, user_id, internal_version_number, version_number, version_status, file_path, is_current, is_remote, is_reviewed, date_released, date_created)
select 	
	f.id, 
	fvi.parentversionid, 
	f.userid,
	fvi.versionnumber,
	fm.currentversion, 
	fm.currentversionstatus,
	f.filepath, 
	fvi.iscurrent, 
	f.isremote, 
	f.isreviewed, 
	f.releasedate, 
	f.creationdate
from 
	cbio.ncbofile f 
	inner join cbio.ncbofileversioninfo fvi on f.id = fvi.id
	inner join cbio.ncbofilemetadatainfo fm on f.metadadatainfoid = fm.id;

SET FOREIGN_KEY_CHECKS = 1;

insert into ncbo_ontology_category (id, ontology_version_id, category_id)
select 	fc.id, o.id, fc.categoryid 
from cbio.ncbofilecategory fc, ncbo_ontology_version o
where o.id = fc.ncbofileid;

insert into ncbo_ontology_metadata (ontology_version_id, display_label, format, contact_name, contact_email, homepage, documentation, publication, urn, is_foundry)
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

update ncbo_ontology_version set internal_version_number = 1 where internal_version_number = 0;

insert into ncbo_seq_ontology_id (id) values (1000);

DELIMITER $$

DROP PROCEDURE IF EXISTS `sp_update_ontology_id`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_update_ontology_id`()
BEGIN
		DECLARE dl VARCHAR(128);
		DECLARE done INT DEFAULT 0;
		DECLARE cur1 CURSOR FOR select distinct display_label from ncbo_ontology_metadata order by display_label;
		
		OPEN cur1;
  
		REPEAT
			FETCH cur1 INTO dl;
			IF NOT done THEN
				update ncbo_seq_ontology_id set id = last_insert_id(id + 1);
				update ncbo_ontology_version set ontology_id = last_insert_id() where id in (select ontology_version_id from ncbo_ontology_metadata where display_label = dl);
			END IF;
		UNTIL done END REPEAT;
		CLOSE cur1;
	END$$

DELIMITER ;

CALL sp_update_ontology_id();

DROP PROCEDURE IF EXISTS `sp_update_ontology_id`;

update ncbo_ontology_version set file_path = CONCAT("/", ontology_id, "/", internal_version_number)
where is_remote = 0;

update ncbo_l_role set name = 'ROLE_DEVELOPER' where id = 2822;
update ncbo_l_role set name = 'ROLE_LIBRARIAN' where id = 2823;
update ncbo_l_role set name = 'ROLE_ADMINISTRATOR' where id = 2824;

