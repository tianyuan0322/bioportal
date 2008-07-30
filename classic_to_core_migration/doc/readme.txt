***********************************************************************************
***** Instructions on migrating data from BioPortal Classic to BioPortal Core *****
*****                        Michael Dorf, 04/30/2008                         *****
***********************************************************************************

1.  Create a new empty MySQL database called cbio.
2.  Run MySQL Migration toolkit. Specify the Oracle production database as the source and MySQL cbio database as the destination.
3.  Export the following tables (including data) from Oracle to MySQL cbio database using identical table names:
		NCBOCATEGORIES
		NCBOFILE
		NCBOFILECATEGORY
		NCBOFILEMETADATAINFO
		NCBOFILENAMES
		NCBOFILEVERSIONINFO
		NCBOROLE
		NCBOUSER
		NCBOUSERROLE
4.  Run bioportal_db.sql script located in the bioportal/db/sql directory. This will create a new MySQL database called bioportal.
		mysql -login -password -dbserver < /apps/bmir.apps/bioportal/db/sql/bioportal_db.sql
5.  Create a database user with the username that matches the bioportal.jdbc.username property in the build.properties file. The password must match the bioportal.jdbc.password property in the build.properties file. Assign this user dba rights to bioportal database.
6.  Run bioportal_lookup_data.sql script located in the bioportal/db/sql directory.
		mysql -login -password -dbserver <  /apps/bmir.apps/bioportal/db/sql/bioportal_lookup_data.sql
7.  Run cbio_to_bioportal_migration.sql script located in the bioportal/classic_to_core_migration/db/sql directory.
		mysql -login -password -dbserver <  /apps/bmir.apps/bioportal/classic_to_core_migration/db/sql/cbio_to_bioportal_migration.sql
8.  Compile and execute OntologyFileCopier.java file. Pass a single command argument that specifies the full path from the root to the directory containing the ontology files. For example:
		OntologyFileCopier /data/ncboapp/files
    The program will copy all files from the source directory to the new bioportal file repository (specified in build.properties via bioportal.ontology.filepath property). The files will be copied using the Bioportal Core directory structure.
		cp /apps/bmir.apps/bioportal/classic_to_core_migration/src/java/OntologyFileCopier.java /apps/bmir.apps/bioportal
		javac OntologyFileCopier.java
		java -classpath /apps/bmir.apps/bioportal/WebRoot/WEB-INF/lib/mysql-connector-java-5.1.6-bin.jar:. OntologyFileCopier /apps/bmir.apps/classic_files
9.  Verify that ontology files are present in the new files directory and that the directory structure matches the values in the column "file_path" of "ncbo_ontology_version" table.
10. Run populate_load_queue.sql script located in the bioportal/classic_to_core_migration/db/sql directory.
		mysql -login -password -dbserver <  /apps/bmir.apps/bioportal/classic_to_core_migration/db/sql/populate_load_queue.sql 
11. Drop cbio database (it is no longer required).
12. Compile and execute OBOFoundryIdUpdater.java file. No arguments are required.
		a. Create jar for complete bioportal_core:
			zip -r /apps/bmir.apps/bioportal/ncbo.jar /apps/bmir.apps/bioportal/WebRoot/WEB-INF/classes/*
		b. Compile and execute updater utility:
			cp /apps/bmir.apps/bioportal/classic_to_core_migration/src/java/OBOFoundryIdUpdater.java /apps/bmir.apps/bioportal
  			javac -classpath /apps/bmir.apps/bioportal/WebRoot/WEB-INF/lib/mysql-connector-java-5.1.6-bin.jar:/apps/bmir.apps/bioportal/ncbo.jar:. OBOFoundryIdUpdater.java
			java -classpath /apps/bmir.apps/bioportal/WebRoot/WEB-INF/lib/mysql-connector-java-5.1.6-bin.jar:/apps/bmir.apps/bioportal/ncbo.jar:. OBOFoundryIdUpdater
