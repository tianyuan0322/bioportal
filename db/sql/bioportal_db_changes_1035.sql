USE `bioportal`;

INSERT INTO ncbo_user (username, api_key, password, email, firstname, lastname)
SELECT LOWER(application_name), application_id, 'MaT9mL6DdWI6s471OFTiJUEZlZz6Ycx0OjXfNZZPHFMic8n5uAeyd9GCLKPstDjQ', 
	CONCAT(LOWER(application_name), '@', 'bioontology.org'), application_name, application_name
FROM ncbo_admin_application;