***********************************************************************************
***** Instructions on migrating data from BioPortal Classic to BioPortal Core *****
*****                        Michael Dorf, 04/30/2008                         *****
***********************************************************************************

1.  Create a new empty MySQL database called "cbio".
2.  Run MySQL Migration toolkit. Specify the Oracle production database as the source and MySQL "cbio" database as the destination.
3.  Export the following tables (including data) from Oracle to MySQL "cbio" database using identical table names:
		NCBOCATEGORIES
		NCBOFILE
		NCBOFILECATEGORY
		NCBOFILEMETADATAINFO
		NCBOFILENAMES
		NCBOFILEVERSIONINFO
		NCBOROLE
		NCBOUSER
		NCBOUSERROLE
4.  Run bioportal_db.sql script located in the bioportal/db/sql directory. This will create a new MySQL database called "bioportal".
5.  Run bioportal_initial_data.sql script located in the bioportal/classic_to_core_migration/db/sql directory.
6.  Run cbio_to_bioportal_migration.sql script located in the bioportal/classic_to_core_migration/db/sql directory.
7.  Compile and execute OntologyFileCopier.java file. Pass a single command argument that specifies the full path from the root to the directory containing the ontology files. For example:
		java OntologyFileCopier /data/ncboapp/files
    The program will copy all files from the source directory to the new bioportal file repository (specified in build.properties via "bioportal.ontology.filepath" property). The files will be copied using the Bioportal Core directory structure.
8.  Verify that ontology files are present in the new files directory and that the directory structure matches the values in the column "file_path" of "ncbo_ontology_version" table.
9.  Run populate_load_queue.sql script located in the bioportal/classic_to_core_migration/db/sql directory.
10. Drop "cbio" database (it is no longer required).