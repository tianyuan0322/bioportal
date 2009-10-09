use strict;
use LWP::UserAgent;
use URI::Escape;
use XML::LibXML;

use constant CONCEPT_LIMIT => 5;

$|=1;

# This script will query the OBS services to get a list of ontologies and their concepts and 
# then query Bioportal for getting the concept bean for each concept.
# The script skips ontologies that are unlikely to be in Bioportal (e.g. the ones that have non-numeric ontology ids)
# The testing is done in this round about manner because the "AllConcepts" service does not work yet.

my $bp_serverurl = "http://localhost:8080";
my $obs_serverurl = "http://stagerest.bioontology.org";

# Create a parse object and set up the REST APIs
my $parser = XML::LibXML->new();

# Get list of all ontologies:
my ($Ontologies_bp_REF, $Ontology_namespaceREF) = &getAllOntologiesFromBioportal();
my ($Ontologies_obs_REF, $Virtual_to_localREF) = &getAllOntologiesFromOBS();

my $count = 0;
my $calls = 0;
my $errors = 0;
my $cutoff = 0;

foreach my $vid (keys %{$Virtual_to_localREF}){
	# Skip the ones in OBS that Bioportal won't have
	next if ($vid !~ m/^\d+/);
	$count++;
	next if ($count <= $cutoff);
	
	print "getting concepts for $vid, $$Virtual_to_localREF{$vid}, $$Ontologies_obs_REF{$vid}\n";

	# get all concepts
	my @Concepts = &getAllConcepts($$Virtual_to_localREF{$vid});
	
	print "got ", scalar @Concepts, " concepts for $$Ontologies_obs_REF{$vid} ontology\n";
	
	# foreach concept, strip out the local ontology id from the concept identifier
	# call the full concept bean at BioPortal using the virtual ontology id
	foreach my $C (@Concepts){		
		$C =~s/$$Virtual_to_localREF{$vid}\///ig;
		$calls++;		
		my $Test= &Testconcept($C, $vid);
	}
	
	print "$calls calls made, $errors errors till now\n";
	print "done with no. $count, $$Ontologies_obs_REF{$vid} ontology\n\n";
}
exit;

###############################################################################
sub getAllOntologiesFromOBS {
###############################################################################
	my %Ontologies;
	my %Ontologies_to_local;
	
	my $baseurl = "$obs_serverurl/obs/ontologies";
	
	# create a user agent and make a service call
	my $req = new HTTP::Request GET => "$baseurl";
	my $ua = new LWP::UserAgent;
	my $res = $ua->request($req);
	
	# check if call is successfull, if so, find the root node and then get the subclasses
	if ($res->is_success){
		my $dom = $parser->parse_string($res->decoded_content);
		my $root = $dom->getDocumentElement();
		my $results = $root->findnodes('/success/data/list/ontologyBean');
		
		foreach my $c_node ($results->get_nodelist){
			$Ontologies{$c_node->findvalue('virtualOntologyID')} = $c_node->findvalue('ontologyName');
			$Ontologies_to_local{$c_node->findvalue('virtualOntologyID')} = $c_node->findvalue('localOntologyID');			
#			print $c_node->findvalue('localOntologyID'),"\t";
#			print $c_node->findvalue('virtualOntologyID'),"\t";
#			print $c_node->findvalue('ontologyName'),"\n";
		}
	}else{
	    print $res->status_line, "\n";
	    print $res->decoded_content;
	}
	return (\%Ontologies, \%Ontologies_to_local);
}

###############################################################################
sub getAllConcepts {
###############################################################################
	my $ontologyid = @_[0];
	my $baseurl = "$obs_serverurl/obs/concepts";
	my @Concepts;
	
	# create a user agent and make a service call
	my $ua = new LWP::UserAgent;
	my $c;
	my $inc = 0;

	for ($c = 0; $c < 100000; $c += 500){
		my $req = new HTTP::Request GET => "$baseurl/$ontologyid?offset=$c";
		my $res = $ua->request($req);		

		# check if call is successfull, if so, find the root node and then get the subclasses
		if ($res->is_success){
			my $dom = $parser->parse_string($res->decoded_content);
			my $root = $dom->getDocumentElement();
			my $results = $root->findnodes('/success/data/list/conceptBean');

			foreach my $c_node ($results->get_nodelist){
				print $c_node->findvalue('localConceptId'),"\t";
				print $c_node->findvalue('preferredName'),"\n";
				push (@Concepts, $c_node->findvalue('localConceptId'));

				if (defined "CONCEPT_LIMIT" && $inc++ >= eval("CONCEPT_LIMIT") - 1) {
					return @Concepts;
				}		
			}
		}else{
			if ($res->status_line eq "404 Not Found"){
				return @Concepts;
			}else {
			    print $res->status_line, "\n";
			    #print $res->decoded_content;
			    my $time = localtime;
			    print "$baseurl/$ontologyid?offset=$c at $time after $calls\n";
			}
		}
	}

	return @Concepts;
}

###############################################################################
sub Testconcept {
###############################################################################
	my ($id, $vid)= @_;
	my $baseurl = "$bp_serverurl/bioportal/virtual/ontology";
	my $RDF;
	
	# create a user agent and make a service call
	my $req = new HTTP::Request GET => "$baseurl/$vid/$id";
	my $ua = new LWP::UserAgent;
	my $res = $ua->request($req);
	
	# check if call is successfull, if so, find the root node and then get the subclasses
	if ($res->is_success){
		my $dom = $parser->parse_string($res->decoded_content);
		my $root = $dom->getDocumentElement();
		
		# make RDF from the concept bean
		
		# get the name
		my $nodeinfo = $root->findnodes('/success/data/classBean');
		foreach my $node ($nodeinfo->get_nodelist){
			my $label = $node->findvalue('label'),"\n";
		}	
		# get the definition	
		my $results = $root->findnodes('/success/data/classBean/relations/entry[string="Definition"]/list');
		if ($results->size() == 0){
			# try alternative tags ..
		}
		foreach my $node ($results->get_nodelist){
			my $definition = $node->findvalue('string'),"\n";
		}
		# get the synonyms
		my $results = $root->findnodes('/success/data/classBean/relations/entry[string="EXACT SYNONYM"]/list/string');
		if ($results->size() == 0){
			# try alternative tags ..
		}			
		foreach my $node ($results->get_nodelist){
			my $synonym = $node->findvalue('.'),"\n";
		}
		# get the xrefs
		my $results = $root->findnodes('/success/data/classBean/relations/entry[string="xref"]/list/string');		
		foreach my $node ($results->get_nodelist){
			my $xref = $node->findvalue('.'),"\n";
		}					
		# get the subclasses
		my $results = $root->findnodes('/success/data/classBean/relations/entry[string="SubClass"]/list/classBean');		
		foreach my $c_node ($results->get_nodelist){
			my $c_id = $c_node->findvalue('id');	
		} 
	}else{
		# need better error handling here ..
	    print $res->status_line, "\t";
	    #print $res->decoded_content;
	    my $time = localtime;
	    print  "$baseurl/$vid/$id at $time after $calls\n";
	    $errors++;
	}	

	return 1;
}

###############################################################################
sub getAllOntologiesFromBioportal {
###############################################################################
	my %Ontologies;
	my %Ontology_namespace;
	my $baseurl = "$bp_serverurl/bioportal/ontologies?email=example\@example.org";
	
	# create a user agent and make a service call
	my $req = new HTTP::Request GET => "$baseurl";
	my $ua = new LWP::UserAgent;
	my $res = $ua->request($req);
	
	# check if call is successfull, if so, find the root node and then get the subclasses
	if ($res->is_success){
		my $dom = $parser->parse_string($res->decoded_content);
		my $root = $dom->getDocumentElement();
		my $results = $root->findnodes('/success/data/list/ontologyBean');
		foreach my $c_node ($results->get_nodelist){
			$Ontologies{$c_node->findvalue('ontologyId')} = $c_node->findvalue('displayLabel');
			$Ontology_namespace{$c_node->findvalue('ontologyId')} = $c_node->findvalue('abbreviation');
#			print $c_node->findvalue('id'),"\t";
#			print $c_node->findvalue('ontologyId'),"\t";
#			print $c_node->findvalue('abbreviation'),"\t";
#			print $c_node->findvalue('displayLabel'),"\n";
		}
	}else{
	    print $res->status_line, "\n";
	    print $res->decoded_content;
	}
	return (\%Ontologies, \%Ontology_namespace);
}
