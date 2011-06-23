USE `bioportal`;

-- A dedicated API key for BioPortal Core Aplication
DELETE FROM ncbo_user_role WHERE user_id = (
	SELECT id FROM ncbo_user WHERE username = 'bioportalcore' AND api_key = '328e93d2-9dd1-11e0-a996-005056bd0024'
);

DELETE FROM ncbo_user WHERE username = 'bioportalcore' AND api_key = '328e93d2-9dd1-11e0-a996-005056bd0024';

INSERT INTO ncbo_user (username, api_key, PASSWORD, email, firstname, lastname)
VALUES ('bioportalcore', '328e93d2-9dd1-11e0-a996-005056bd0024', 'MBznvzDsct9kuRawS7dnHH7bZDtUkygmvkq9ED7X4t1NdMvdSVTWlKQ6lWzPZb13',
	'bioportalcore@bioontology.org', 'BioPortalCore', 'User');

INSERT INTO ncbo_user_role (user_id, role_id)
SELECT id, 2824 FROM ncbo_user WHERE username = 'bioportalcore' AND api_key = '328e93d2-9dd1-11e0-a996-005056bd0024';