insert into ncbo_user (id, username, password, email, firstname, lastname, phone)
select id, login, password, email, firstname, lastname, phone 
from ncbouser;

insert into ncbo_l_category (id, name, parent_category_id)
select id, name, parentcategoryid 
from ncbocategories;

insert into ncbo_l_role (id, name, description)
select id, name, description 
from ncborole;

insert into ncbo_user_role (id, user_id, role_id) 
select id, userid, roleid 
from ncbouserrole;

SET FOREIGN_KEY_CHECKS = 0;

insert into cbio.ncbo_ontology 
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
	ncbofile f 
	inner join ncbofileversioninfo fvi on f.id = fvi.id
	inner join ncbofilemetadatainfo fm on f.metadadatainfoid = fm.id;

SET FOREIGN_KEY_CHECKS = 1;

insert into ncbo_ontology_category (id, ontology_id, category_id)
select 	fc.id, o.id, fc.categoryid 
from ncbofilecategory fc, ncbo_ontology o
where o.id = fc.ncbofileid;

insert into cbio.ncbo_ontology_metadata (ontology_id, display_label, format, contact_name, contact_email, homepage, documentation, publication, urn, is_foundry)
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
	ncbo_ontology o 
	inner join ncbofile f on o.id = f.id
	inner join ncbofilemetadatainfo fm on f.metadadatainfoid = fm.id;

insert into bioportal.ncbo_ontology_file (id, ontology_id, filename)
select fn.id, o.id, fn.filename
from ncbo_ontology o inner join ncbofilenames fn on o.id = fn.ncbofile;
