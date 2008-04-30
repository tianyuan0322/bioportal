1. Create a new MySQL database called "cbio".
2. Run MySQL Migration toolkit. Specify the Oracle production database as the source and MySQL "cbio" database as the destination.
3. Export the following tables (including data) from Oracle to MySQL "cbio" database using identical table names:
	NCBOCATEGORIES
	NCBOFILE
	NCBOFILECATEGORY
	NCBOFILEMETADATAINFO
	NCBOFILENAMES
	NCBOFILEVERSIONINFO
	NCBOROLE
	NCBOUSER
	NCBOUSERROLE
4. Run bioportal_db.sql script located in the bioportal/db/sql directory. This will create a new MySQL database called "bioportal".
5. Run cbio_to_bioportal_migration.sql script located in the bioportal/classic_to_core_migration/db/sql directory.
6. Compile and execute OntologyFileCopier.java file. Pass a single command parameter that specifies the full path to the directory containing the ontology files.