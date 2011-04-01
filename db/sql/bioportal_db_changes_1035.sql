USE `bioportal`;

ALTER TABLE `ncbo_user` MODIFY COLUMN `api_key` VARCHAR(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT 'UUID()';

INSERT INTO ncbo_user (username, api_key, password, email, firstname, lastname)
SELECT LOWER(application_name), application_id, 'MaT9mL6DdWI6s471OFTiJUEZlZz6Ycx0OjXfNZZPHFMic8n5uAeyd9GCLKPstDjQ', 
	CONCAT(LOWER(application_name), '@', 'bioontology.org'), application_name, application_name
FROM ncbo_admin_application;

-- A temporary admin user to fallback on during the security grace period
INSERT INTO ncbo_user (username, api_key, PASSWORD, email, firstname, lastname)
VALUES ('temp-admin-user', 'fcc74490-5a25-11e0-9a6e-005056aa215e', 'jczJAOPkgEuT6mxRJUMlvi1GNmfK9oZZq/Dce0D3Ka4Ove6NWmroWbNb/1IB0gWj',
	'temp-admin-user@bioontology.org', 'Temporary', 'Administrator');
	
INSERT INTO ncbo_user_role (user_id, role_id)
SELECT id, 2824 FROM ncbo_user WHERE username = 'temp-admin-user' AND api_key = 'fcc74490-5a25-11e0-9a6e-005056aa215e';
