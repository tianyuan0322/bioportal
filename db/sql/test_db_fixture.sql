/* Drop old users*/
DROP USER 'bioportal_user';
DROP USER 'lexgrid_user';
DROP USER 'protege_user';


/* Create LexGrid database */
CREATE DATABASE `bioportal_lexgrid`;

/* Create Protege database */
CREATE DATABASE `bioportal_protege`;

/* Create Users */
create user 'bioportal_user'@'localhost' identified by 'password';
GRANT SELECT, CREATE, INSERT, UPDATE, DELETE ON bioportal.* TO 'bioportal_user'@'localhost';

create user 'lexgrid_user'@'localhost' identified by 'password';
GRANT SELECT, CREATE, INSERT, UPDATE, DELETE ON bioportal_lexgrid.* TO 'lexgrid_user'@'localhost';

create user 'protege_user'@'localhost' identified by 'password';
GRANT SELECT, CREATE, INSERT, UPDATE, DELETE ON bioportal_protege.* TO 'protege_user'@'localhost';
