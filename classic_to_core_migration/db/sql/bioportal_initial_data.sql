USE `bioportal`;

/*Data for the table `ncbo_admin_application` */

insert  into `ncbo_admin_application`(`id`,`application_id`,`application_name`,`application_description`) values (1,'4ea81d74-8960-4525-810b-fa1baab576ff','NcboBioPortal','NCBO Ruby on Rails based BioPortal GUI implementation');

/*Data for the table `ncbo_l_app_text_datatype` */

insert  into `ncbo_l_app_text_datatype`(`datatype_code`,`datatype`) values ('DAT','Date'),('LTX','Long Text'),('NUM','Numeric'),('RTX','Rich Text'),('TXT','Short Text (one line)');

/*Data for the table `ncbo_l_status` */

insert  into `ncbo_l_status`(`id`,`status`,`description`) values (1,'Waiting','The action has not been taken'),(2,'Parsing','The action is in progress'),(3,'Ready','The action has completed successfully'),(4,'Error','The action has encountered an error while executing'),(5,'Not Applicable','The action does not apply to this record');
