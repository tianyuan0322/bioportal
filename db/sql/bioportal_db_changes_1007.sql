USE `bioportal`;

ALTER TABLE ncbo_ontology_version_metadata MODIFY
	COLUMN `synonym_slot` varchar(512) character set utf8 collate utf8_bin default NULL;

ALTER TABLE ncbo_ontology_version_metadata MODIFY
	COLUMN `preferred_name_slot` varchar(512) character set utf8 collate utf8_bin default NULL;

ALTER TABLE ncbo_ontology_version_metadata ADD
	COLUMN `description` varchar(1024) default NULL
	AFTER `display_label`;

ALTER TABLE ncbo_ontology_version_metadata ADD
	COLUMN `abbreviation` varchar(48) default NULL
	AFTER `description`;

ALTER TABLE ncbo_ontology_version_metadata AUTO_INCREMENT = 1000;

CREATE OR REPLACE VIEW `v_ncbo_ontology` AS
SELECT 
	ov.id,
	ov.ontology_id,				
	ov.user_id,
	ov.internal_version_number,
	ov.version_number,
	ov.version_status,
	ov.file_path,
	ov.is_remote,
	ov.is_reviewed,
	ov.status_id,
	ov.date_created,
	ov.date_released,
	o.obo_foundry_id,
	o.is_manual,
	ovm.display_label,
	ovm.description,
	ovm.abbreviation,
	ovm.format,
	ovm.contact_name,
	ovm.contact_email,
	ovm.homepage,
	ovm.documentation,
	ovm.publication,
	ovm.urn,
	ovm.coding_scheme,
	ovm.target_terminologies,
	ovm.is_foundry,
	ovm.synonym_slot,
	ovm.preferred_name_slot
FROM
	ncbo_ontology o 
	INNER JOIN ncbo_ontology_version ov on o.id = ov.ontology_id
	INNER JOIN ncbo_ontology_version_metadata ovm ON ov.id = ovm.ontology_version_id;

update ncbo_ontology_version_metadata set abbreviation = 'nif' where display_label = 'NIFSTD';

update ncbo_ontology_version_metadata set abbreviation = 'FFHO' where display_label = 'Family Health History Ontology';

update ncbo_ontology_version_metadata set abbreviation = 'ATMO' where display_label = 'African Traditional Medicine';

update ncbo_ontology_version_metadata set abbreviation = 'amino-acid' where display_label = 'Amino Acid';

update ncbo_ontology_version_metadata set abbreviation = 'AAO' where display_label = 'Amphibian gross anatomy';

update ncbo_ontology_version_metadata set abbreviation = 'basic-vertebrate-gross-anatomy' where display_label = 'Basic Vertebrate Anatomy';

update ncbo_ontology_version_metadata set abbreviation = 'BILA' where display_label = 'Bilateria anatomy';

update ncbo_ontology_version_metadata set abbreviation = 'FBbi' where display_label = 'Biological imaging methods';

update ncbo_ontology_version_metadata set abbreviation = 'GO' where display_label = 'Biological process';

update ncbo_ontology_version_metadata set abbreviation = 'BRO' where display_label = 'Biomedical Resource Ontology';

update ncbo_ontology_version_metadata set abbreviation = 'birnlex' where display_label = 'BIRNLex';

update ncbo_ontology_version_metadata set abbreviation = 'BHO' where display_label = 'Bleeding History Phenotype';

update ncbo_ontology_version_metadata set abbreviation = 'BTO' where display_label = 'BRENDA tissue / enzyme source';

update ncbo_ontology_version_metadata set abbreviation = 'WBls' where display_label = 'C. elegans development';

update ncbo_ontology_version_metadata set abbreviation = 'WBbt' where display_label = 'C. elegans gross anatomy';

update ncbo_ontology_version_metadata set abbreviation = 'WBPhenotype' where display_label = 'C. elegans phenotype';

update ncbo_ontology_version_metadata set abbreviation = 'CCO' where display_label = 'Cell Cycle Ontology (A. thaliana)';

update ncbo_ontology_version_metadata set abbreviation = 'CCO' where display_label = 'Cell Cycle Ontology (H. sapiens)';

update ncbo_ontology_version_metadata set abbreviation = 'CCO' where display_label = 'Cell Cycle Ontology (S. cerevisiae)';

update ncbo_ontology_version_metadata set abbreviation = 'CCO' where display_label = 'Cell Cycle Ontology (S. pombe)';

update ncbo_ontology_version_metadata set abbreviation = 'CL' where display_label = 'Cell type';

update ncbo_ontology_version_metadata set abbreviation = 'GO' where display_label = 'Cellular component';

update ncbo_ontology_version_metadata set abbreviation = 'GRO' where display_label = 'Cereal plant development';

update ncbo_ontology_version_metadata set abbreviation = 'PO' where display_label = 'Cereal plant gross anatomy';

update ncbo_ontology_version_metadata set abbreviation = 'TO' where display_label = 'Cereal plant trait';

update ncbo_ontology_version_metadata set abbreviation = 'ChEBI' where display_label = 'Chemical entities of biological interest';

update ncbo_ontology_version_metadata set abbreviation = 'CARO' where display_label = 'Common Anatomy Reference Ontology';

update ncbo_ontology_version_metadata set abbreviation = 'CDAO' where display_label = 'Comparative Data Analysis Ontology';

update ncbo_ontology_version_metadata set abbreviation = 'DDANAT' where display_label = 'Dictyostelium discoideum anatomy';

update ncbo_ontology_version_metadata set abbreviation = 'FBdv' where display_label = 'Drosophila development';

update ncbo_ontology_version_metadata set abbreviation = 'FBbt' where display_label = 'Drosophila gross anatomy';

update ncbo_ontology_version_metadata set abbreviation = 'ENVO' where display_label = 'Environment Ontology';

update ncbo_ontology_version_metadata set abbreviation = 'ClinicalTrialOntology' where display_label = 'Epoch Clinical Trial Ontologies';

update ncbo_ontology_version_metadata set abbreviation = 'IEV' where display_label = 'Event (INOH pathway ontology)';

update ncbo_ontology_version_metadata set abbreviation = 'ECO' where display_label = 'Evidence codes';

update ncbo_ontology_version_metadata set abbreviation = 'EV' where display_label = 'eVOC (Expressed Sequence Annotation for Humans)';

update ncbo_ontology_version_metadata set abbreviation = 'FBsp' where display_label = 'Fly taxonomy';

update ncbo_ontology_version_metadata set abbreviation = 'FMA' where display_label = 'FMA';

update ncbo_ontology_version_metadata set abbreviation = 'FMA' where display_label = 'Foundational Model of Anatomy (subset)';

update ncbo_ontology_version_metadata set abbreviation = 'FAO' where display_label = 'Fungal gross anatomy';

update ncbo_ontology_version_metadata set abbreviation = 'BOOTStrep' where display_label = 'Gene Regulation Ontology';

update ncbo_ontology_version_metadata set abbreviation = 'GRO' where display_label = 'Gene Regulation Ontology (GRO)';

update ncbo_ontology_version_metadata set abbreviation = 'EHDAA' where display_label = 'Human developmental anatomy, abstract version';

update ncbo_ontology_version_metadata set abbreviation = 'EHDA' where display_label = 'Human developmental anatomy, timed version';

update ncbo_ontology_version_metadata set abbreviation = 'DOID' where display_label = 'Human disease';

update ncbo_ontology_version_metadata set abbreviation = 'HP' where display_label = 'human phenotype ontology';

update ncbo_ontology_version_metadata set abbreviation = 'IDO' where display_label = 'Infectious disease';

update ncbo_ontology_version_metadata set abbreviation = 'ZEA' where display_label = 'Maize gross anatomy';

update ncbo_ontology_version_metadata set abbreviation = 'MP' where display_label = 'Mammalian phenotype';

update ncbo_ontology_version_metadata set abbreviation = 'MS' where display_label = 'Mass spectrometry';

update ncbo_ontology_version_metadata set abbreviation = 'MFO' where display_label = 'Medaka fish anatomy and development';

update ncbo_ontology_version_metadata set abbreviation = 'MHC' where display_label = 'MHC Ontology';

update ncbo_ontology_version_metadata set abbreviation = 'MO' where display_label = 'Microarray experimental conditions';

update ncbo_ontology_version_metadata set abbreviation = 'GO' where display_label = 'Molecular function';

update ncbo_ontology_version_metadata set abbreviation = 'IMR' where display_label = 'Molecule role (INOH Protein name/family name ontology)';

update ncbo_ontology_version_metadata set abbreviation = 'TGMA' where display_label = 'Mosquito gross anatomy';

update ncbo_ontology_version_metadata set abbreviation = 'MIRO' where display_label = 'Mosquito insecticide resistance';

update ncbo_ontology_version_metadata set abbreviation = 'MA' where display_label = 'Mouse adult gross anatomy';

update ncbo_ontology_version_metadata set abbreviation = 'EMAP' where display_label = 'Mouse gross anatomy and development';

update ncbo_ontology_version_metadata set abbreviation = 'MPATH' where display_label = 'Mouse pathology';

update ncbo_ontology_version_metadata set abbreviation = 'RO' where display_label = 'Multiple alignment';

update ncbo_ontology_version_metadata set abbreviation = 'NPO' where display_label = 'NanoParticle Ontology (NPO)';

update ncbo_ontology_version_metadata set abbreviation = 'NCIt' where display_label = 'NCI Thesaurus';

update ncbo_ontology_version_metadata set abbreviation = 'NMR' where display_label = 'NMR-instrument specific component of metabolomics investigations';

update ncbo_ontology_version_metadata set abbreviation = 'OBO_REL' where display_label = 'OBO relationship types';

update ncbo_ontology_version_metadata set abbreviation = 'OBI' where display_label = 'Ontology for Biomedical Investigations';

update ncbo_ontology_version_metadata set abbreviation = 'OCRe' where display_label = 'Ontology of Clinical Research (OCRe)';

update ncbo_ontology_version_metadata set abbreviation = 'TRANS' where display_label = 'Pathogen transmission';

update ncbo_ontology_version_metadata set abbreviation = 'PW' where display_label = 'Pathway ontology';

update ncbo_ontology_version_metadata set abbreviation = 'PATO' where display_label = 'Phenotypic quality';

update ncbo_ontology_version_metadata set abbreviation = 'FIX' where display_label = 'Physico-chemical methods and properties';

update ncbo_ontology_version_metadata set abbreviation = 'REX' where display_label = 'Physico-chemical process';

update ncbo_ontology_version_metadata set abbreviation = 'EO' where display_label = 'Plant environmental conditions';

update ncbo_ontology_version_metadata set abbreviation = 'PO' where display_label = 'Plant growth and developmental stage';

update ncbo_ontology_version_metadata set abbreviation = 'PO' where display_label = 'Plant structure';

update ncbo_ontology_version_metadata set abbreviation = 'MOD' where display_label = 'Protein modification';

update ncbo_ontology_version_metadata set abbreviation = 'PRO' where display_label = 'PRotein Ontology (PRO)';

update ncbo_ontology_version_metadata set abbreviation = 'MI ' where display_label = 'Protein-protein interaction';

update ncbo_ontology_version_metadata set abbreviation = 'ProPreO' where display_label = 'Proteomics data and process provenance';

update ncbo_ontology_version_metadata set abbreviation = 'RID' where display_label = 'RadLex';

update ncbo_ontology_version_metadata set abbreviation = 'SEP' where display_label = 'Sample processing and separation techniques';

update ncbo_ontology_version_metadata set abbreviation = 'SO' where display_label = 'Sequence Ontology';

update ncbo_ontology_version_metadata set abbreviation = 'SO' where display_label = 'Sequence types and features';

update ncbo_ontology_version_metadata set abbreviation = 'SPO' where display_label = 'Skin Physiology Ontology';

update ncbo_ontology_version_metadata set abbreviation = 'SOPHARM' where display_label = 'SO-Pharm';

update ncbo_ontology_version_metadata set abbreviation = 'BSPO' where display_label = 'Spatial Ontology';

update ncbo_ontology_version_metadata set abbreviation = 'SPD' where display_label = 'Spider Ontology';

update ncbo_ontology_version_metadata set abbreviation = 'SAO' where display_label = 'Subcellular anatomy ontology';

update ncbo_ontology_version_metadata set abbreviation = 'SOPHARM' where display_label = 'Suggested Ontology for Pharmacogenomics';

update ncbo_ontology_version_metadata set abbreviation = 'SBO' where display_label = 'Systems Biology';

update ncbo_ontology_version_metadata set abbreviation = 'TAO' where display_label = 'Teleost anatomy and development';

update ncbo_ontology_version_metadata set abbreviation = 'TTO' where display_label = 'Teleost taxonomy';

update ncbo_ontology_version_metadata set abbreviation = 'TADS' where display_label = 'Tick gross anatomy';

update ncbo_ontology_version_metadata set abbreviation = 'UBERON' where display_label = 'Uber anatomy ontology';

update ncbo_ontology_version_metadata set abbreviation = 'UO' where display_label = 'Units of measurement';

update ncbo_ontology_version_metadata set abbreviation = 'XAO' where display_label = 'Xenopus anatomy and development';

update ncbo_ontology_version_metadata set abbreviation = 'YPO' where display_label = 'Yeast phenotypes';

update ncbo_ontology_version_metadata set abbreviation = 'ZFA' where display_label = 'Zebrafish anatomy and development';

update ncbo_ontology_version_metadata set description = 'African Traditional Medicine Ontology (ATMO) describes the actors\' function (healer, fetishist or soothsayer); the different types of proposed process treatment, the symptom\'s roles and the disease consideration.' where display_label = 'African Traditional Medicine';

update ncbo_ontology_version_metadata set description = 'An ontology of amino acids and their properties.' where display_label = 'Amino Acid';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of the anatomy of Amphibians. ' where display_label = 'Amphibian gross anatomy';

update ncbo_ontology_version_metadata set description = 'A basic vertebrate anatomy derived mainly from FMA. ' where display_label = 'Basic Vertebrate Anatomy';

update ncbo_ontology_version_metadata set description = 'An ontology of anatomy for animals that have bilateral symmetry. ' where display_label = 'Bilateria anatomy';

update ncbo_ontology_version_metadata set description = 'This CV covers sample preparation, visualization and imaging methods.' where display_label = 'Biological imaging methods';

update ncbo_ontology_version_metadata set description = 'A controlled vocabulary of gene and gene product attributes related to biological process.' where display_label = 'Biological process';

update ncbo_ontology_version_metadata set description = 'An ontology of compuational biomedical resources.' where display_label = 'Biomedical Resource Ontology';

update ncbo_ontology_version_metadata set description = 'The BIRN Project lexicon will provide entities for data and database annotation for the BIRN project, covering anatomy, disease, data collection, project management and experimental design.' where display_label = 'BIRNLex';

update ncbo_ontology_version_metadata set description = 'An application ontology devoted to the standardized recording of phenotypic data related to hemorrhagic disorders.' where display_label = 'Bleeding History Phenotype';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary for the source of an enzyme. It comprises terms for tissues, cell lines, cell types and cell cultures from uni- and multicellular organisms.' where display_label = 'BRENDA tissue / enzyme source';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of the development of Caenorhabditis elegans.' where display_label = 'C. elegans development';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of the anatomy of Caenorhabditis elegans.' where display_label = 'C. elegans gross anatomy';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of Caenorhabditis elegans phenotypes' where display_label = 'C. elegans phenotype';

update ncbo_ontology_version_metadata set description = 'An ontology that integrates and manages knowledge about the cell cycle components and regulatory aspects of A. thaliana.' where display_label = 'Cell Cycle Ontology (A. thaliana)';

update ncbo_ontology_version_metadata set description = 'An ontology that integrates and manages knowledge about the cell cycle components and regulatory aspects of H. sapiens.' where display_label = 'Cell Cycle Ontology (H. sapiens)';

update ncbo_ontology_version_metadata set description = 'An ontology that integrates and manages knowledge about the cell cycle components and regulatory aspects of S. cerevisiae.' where display_label = 'Cell Cycle Ontology (S. cerevisiae)';

update ncbo_ontology_version_metadata set description = 'An ontology that integrates and manages knowledge about the cell cycle components and regulatory aspects S. pombe.' where display_label = 'Cell Cycle Ontology (S. pombe)';

update ncbo_ontology_version_metadata set description = 'The Cell Ontology is designed as a structured controlled vocabulary for cell types. This ontology is not organism specific; indeed it includes cell types from prokaryotes to mammals, including plants and fungi.' where display_label = 'Cell type';

update ncbo_ontology_version_metadata set description = 'A controlled vocabulary of gene and gene product attributes related to cellular component.' where display_label = 'Cellular component';

update ncbo_ontology_version_metadata set description = 'A controlled vocabulary of describe phenotypic traits in cereal plants. Each trait is a distinguishable feature, characteristic, quality or phenotypic feature of a developing or mature cereal plant.' where display_label = 'Cereal plant development';

update ncbo_ontology_version_metadata set description = 'A controlled vocabulary for plant structure (anatomy) and growth stages.' where display_label = 'Cereal plant gross anatomy';

update ncbo_ontology_version_metadata set description = 'A structured classification of chemical compounds of biological relevance.' where display_label = 'Chemical entities of biological interest';

update ncbo_ontology_version_metadata set description = 'The Common Anatomy Reference Ontology (CARO) is being developed to facilitate interoperability between existing anatomy ontologies for different species, and will provide a template for building new anatomy ontologies' where display_label = 'Common Anatomy Reference Ontology';

update ncbo_ontology_version_metadata set description = 'An ontology intended to provide a common ontological framework for evolutionary analysis regardless of the type of data involved.' where display_label = 'Comparative Data Analysis Ontology';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of the anatomy of the slime-mould Dictyostelium discoideum.' where display_label = 'Dictyostelium discoideum anatomy';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of the development of Drosophila melanogaster.' where display_label = 'Drosophila development';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of the anatomy of Drosophila melanogaster.' where display_label = 'Drosophila gross anatomy';

update ncbo_ontology_version_metadata set description = 'Ontology of environmental features and habitats' where display_label = 'Environment Ontology';

update ncbo_ontology_version_metadata set description = 'A rich ontology for experimental and other evidence statements.' where display_label = 'Evidence codes';

update ncbo_ontology_version_metadata set description = 'Provides structured controlled vocabularies for the annotation of expressed sequences with respect to anatomical system, cell type, developmental stage, experimental technique, microarray platform, pathology, pooling, tissue preparation and treatment.' where display_label = 'eVOC (Expressed Sequence Annotation for Humans)';

update ncbo_ontology_version_metadata set description = 'The taxonomy of the family Drosophilidae (largely after Baechli) and of other taxa referred to in FlyBase.' where display_label = 'Fly taxonomy';

update ncbo_ontology_version_metadata set description = 'FMA is a domain ontology that represents a coherent body of explicit declarative knowledge about human anatomy.' where display_label = 'FMA';

update ncbo_ontology_version_metadata set description = 'FMA is a domain ontology that represents a coherent body of explicit declarative knowledge about human anatomy.' where display_label = 'Foundational Model of Anatomy (subset)';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary for the anatomy of fungi.' where display_label = 'Fungal gross anatomy';

update ncbo_ontology_version_metadata set description = 'A conceptual model for the domain of gene regulation.' where display_label = 'Gene Regulation Ontology';

update ncbo_ontology_version_metadata set description = 'The Habronattus courtship ontology is an ontology of terms used to describe the courtship display of the jumping spider Habronattus californicus.  It was constructed from observations of video clips provided to the curator by Wayne Maddison.' where display_label = 'Habronattus courtship';

update ncbo_ontology_version_metadata set description = 'Creating a comprehensive hierarchical controlled vocabulary for human disease representation.' where display_label = 'Human disease';

update ncbo_ontology_version_metadata set description = 'International Disease Classification.' where display_label = 'ICD9';

update ncbo_ontology_version_metadata set description = 'An ontology of infectious diseases.' where display_label = 'Infectious disease';

update ncbo_ontology_version_metadata set description = 'The loggerhead nesting ontology is an ontology of terms used and defined in a published ethogram of loggerhead nesting (Hailman, J. P. and Elowson, A. M. 1992.  Ethogram of the nesting female loggerhead (Caretta caretta).  Herpetologica 48:1-30).' where display_label = 'Loggerhead nesting';

update ncbo_ontology_version_metadata set description = 'The Mammalian Phenotype Ontology is under development as a community effort to provide standard terms for annotating mammalian phenotypic data.' where display_label = 'Mammalian phenotype';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary for the annotation of experiments concerned with proteomics mass spectrometry. Developed by the HUPO Proteomics Standards Initiative.' where display_label = 'Mass spectrometry';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of the anatomy and development of the Japanese medaka fish, Oryzias latipes.' where display_label = 'Medaka fish anatomy and development';

update ncbo_ontology_version_metadata set description = 'The MHC ontology contains terms necessary for describing and categorizing concepts related to MHC, in general, and for a number of model species, and also for humans. ' where display_label = 'MHC Ontology';

update ncbo_ontology_version_metadata set description = 'Concepts, definitions, terms, and resources for standardized description of a microarray experiment in support of MAGE v.1.' where display_label = 'Microarray experimental conditions';

update ncbo_ontology_version_metadata set description = 'A controlled vocabulary of gene and gene product attributes related to molecular function.' where display_label = 'Molecular function';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of pathway centric biological processes.' where display_label = 'Molecule role (INOH Protein name/family name ontology)';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of the anatomy of mosquitoes.' where display_label = 'Mosquito gross anatomy';

update ncbo_ontology_version_metadata set description = 'Application ontology for entities related to insecticide resistance in mosquitos.' where display_label = 'Mosquito insecticide resistance';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of the adult anatomy of the mouse (Mus).' where display_label = 'Mouse adult gross anatomy';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of stage-specific anatomical structures of the mouse (Mus).' where display_label = 'Mouse gross anatomy and development';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of mutant and transgenic mouse pathology phenotypes.' where display_label = 'Mouse pathology';

update ncbo_ontology_version_metadata set description = 'An ontology that represents the basic knowledge of physical, chemical and functional characteristics of nanotechnology as used in cancer diagnosis and therapy. ' where display_label = 'NanoParticle Ontology (NPO)';

update ncbo_ontology_version_metadata set description = 'A vocabulary for clinical care, translational and basic research, and public information and administrative activities.' where display_label = 'NCI Thesaurus';

update ncbo_ontology_version_metadata set description = 'Descriptors relevant to the experimental conditions of the Nuclear Magnetic Resonance (NMR) component in a metabolomics investigation.' where display_label = 'NMR-instrument specific component of metabolomics investigations';

update ncbo_ontology_version_metadata set description = 'Defines core relations used in all OBO ontologies.' where display_label = 'OBO relationship types';

update ncbo_ontology_version_metadata set description = 'OBI models the design of an investigation, the protocols and instrumentation used, the material used, the data generated and the type analysis performed on it.' where display_label = 'Ontology for Biomedical Investigations';

update ncbo_ontology_version_metadata set description = 'This vocabulary describes a process that is the means of how a pathogen is transmitted from one host, reservoir, or source to another host. This transmission may occur either directly or indirectly and may involve animate vectors or inanimate vehicles.' where display_label = 'Pathogen transmission';

update ncbo_ontology_version_metadata set description = 'Phenotypic qualities (properties).' where display_label = 'Phenotypic quality';

update ncbo_ontology_version_metadata set description = 'A controlled vocabulary of growth and developmental stages in various plants.' where display_label = 'Plant growth and developmental stage';

update ncbo_ontology_version_metadata set description = 'A controlled vocabulary of plant morphological and anatomical structures representing organs, tissues, cell types, and their biological relationships based on spatial and developmental organization.' where display_label = 'Plant structure';

update ncbo_ontology_version_metadata set description = 'PSI-MOD is an ontology consisting of terms that describe protein chemical modifications, logically linked by an is_a relationship in such a way as to form a direct acyclic graph (DAG).' where display_label = 'Protein modification';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary for the annotation of experiments concerned with protein-protein interactions. Developed by the HUPO Proteomics Standards Initiative.' where display_label = 'Protein-protein interaction';

update ncbo_ontology_version_metadata set description = 'A comprehensive proteomics data and process provenance ontology.' where display_label = 'Proteomics data and process provenance';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary for the annotation of sample processing and separation techniques in scientific experiments, such as, and including, gel electrophoresis, column chromatography, capillary electrophoresis, centrifugation and so on.' where display_label = 'Sample processing and separation techniques';

update ncbo_ontology_version_metadata set description = 'The Sequence Ontology provides a structured controlled vocabulary for sequence annotation, for the exchange of annotation data and for the description of sequence objects in databases.' where display_label = 'Sequence types and features';

update ncbo_ontology_version_metadata set description = 'A small ontology for anatomical spatiol references, such as dorsal, ventral, axis, and so forth.' where display_label = 'Spatial Ontology';

update ncbo_ontology_version_metadata set description = 'An ontology for spider comparative biology including anatomical parts (e.g. leg, claw), behavior (e.g. courtship, combing) and products (i.g. silk, web, borrow).' where display_label = 'Spider Ontology';

update ncbo_ontology_version_metadata set description = 'SAO describes structures from the dimensional range encompassing cellular and subcellular structure, supracellular domains, and macromolecules.' where display_label = 'Subcellular Anatomy Ontology (SAO)';

update ncbo_ontology_version_metadata set description = 'A multi-species anatomy ontology for teleost fish.' where display_label = 'Teleost anatomy and development';

update ncbo_ontology_version_metadata set description = 'The anatomy of the Tick, Families: Ixodidae, Argassidae. ' where display_label = 'Tick gross anatomy';

update ncbo_ontology_version_metadata set description = 'Uberon is a multi-species anatomy ontology created to facilitate comparison of phenotypes across multiple species. Uberon is generated semi-automatically from the union of existing species-centric anatomy ontologies.' where display_label = 'Uber anatomy ontology';

update ncbo_ontology_version_metadata set description = 'Metrical units for use in conjunction with PATO' where display_label = 'Units of measurement';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of the anatomy and development of the African clawed frog (Xenopus laevis).' where display_label = 'Xenopus anatomy and development';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary for the phenotypes of budding yeast.' where display_label = 'Yeast phenotypes';

update ncbo_ontology_version_metadata set description = 'A structured controlled vocabulary of the anatomy and development of the Zebrafish (Danio rerio).' where display_label = 'Zebrafish anatomy and development';
