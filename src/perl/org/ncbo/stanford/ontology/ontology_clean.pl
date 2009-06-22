#!/usr/bin/perl -wT

#############################################################################################################
# 				
#				cleans old ontologies
# 
# leaves intact: 
# 1. one ontology per month in the past. 
# 2. one ontology per week for the last month. 
# 3. all ontologies for the last week.
#
#				
#############################################################################################################


use strict;

use DBI;
use DBD::mysql;
use DBIx::Wrapper::VerySimple;
use HTTP::Request::Common;
require LWP::UserAgent;
my $ua = LWP::UserAgent->new;
#time out setting in seconds
$ua->timeout(1200);
 



###############################################################################################################
# CONFIG VARIABLES
# 
# db connection: 
use constant POST_URL => 'http://rest_url/bioportal/ontologies/delete/?ontologyversionids=';
use constant HOST => "localhost";
my $platform = "mysql";
my $database = "bioportal";
my $port = "3306";
my $tablename = "ncbo_ontology_version";
# auth (disabled now):
my $user = "user";
my $pw = "password";
# output/log file:
my $logfile="logfile.txt";
# to save more/less latest ontologies chage it 
my $length_of_the_week = 7;

##############################################################################################################

my $sql_ontologies;
my $sql_versions;
my $hash_ref_ontologies;
my $hash_ref_versions;
my $array_ref_ontologies;
my $array_ref_versions;


print "clean old ontologies...     start\n";

my $dsn = "dbi:mysql:$database:" . HOST . ":$port";
my $db = DBIx::Wrapper::VerySimple->new($dsn,$user,$pw);

open LOG, ">> $logfile" or die "Cannot create logfile: $!";

my $year =(localtime)[5]+1900;
my $month=(localtime)[4]+1;
my $day  =(localtime)[3];

print "$year-$month-$day\n";
print LOG "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ \n";
print LOG "$year-$month-$day\n";



# get all ontologies

$sql_ontologies = "select distinct ontology_id from $tablename order by ontology_id";
$sql_versions = "select id, date_released from $tablename where ontology_id = ? order by date_released";

$array_ref_ontologies = $db->fetch_all($sql_ontologies);

# check antology vsersions 

foreach $hash_ref_ontologies (@$array_ref_ontologies) {

#
#	print "ontology id: $hash_ref_ontologies->{'ontology_id'}\n";
#
	print LOG "ontology id: $hash_ref_ontologies->{'ontology_id'}\n";

	$array_ref_versions = $db->fetch_all($sql_versions, $hash_ref_ontologies->{'ontology_id'});
	&scheduler($array_ref_versions,$year,$month,$day,$length_of_the_week); 
	
	print LOG "-------------------------------\n";
#	print "-------------------------------\n";
	
}

close LOG;

print "done...\n";
print "log file: $logfile\n";

sub scheduler (){

# checks released date for ontolgy
# keeps first ontolgy
# keeps first ontoly of next month
# keeps weekly otology of this month
# keeps if ontology younger than a week
# deletes everything else

	$main::i_month = 0;
	$main::i_day = 0;
	$main::i_year = 0;
	$main::i_month_prev = 0;
	$main::i_day_prev   = 0;
	$main::i_year_prev  = 0;
	$main::i_row = 1;	
	$main::first_day_of_the_month = 0;
	$main::week_number = 1;

	
	foreach $hash_ref_versions (@$array_ref_versions) {

#		print "$hash_ref_versions->{'id'} => $hash_ref_versions->{'date_released'}\n";
		print LOG "$hash_ref_versions->{'id'} => $hash_ref_versions->{'date_released'}\n";

	};


	foreach $hash_ref_versions (@$array_ref_versions) {

		$_=$hash_ref_versions->{'date_released'};
		/(\d\d\d\d)-(\d\d)-(\d\d)/;

		$main::i_month_prev = $main::i_month;
		$main::i_day_prev   = $main::i_day;
		$main::i_year_prev  = $main::i_year;
 
		$main::i_month=$2;
		$main::i_day=$3;
		$main::i_year=$1;

	
		next if (&first_row);
		next if (&new_month);
		next if (&this_month);
		next if (&this_week);

		&delete_record($hash_ref_versions->{'id'}, $hash_ref_versions->{'date_released'});

	};



	sub first_row {
	
# first ontology 

		if ($main::i_row) {

			$main::i_month_prev = $main::i_month;
			$main::i_day_prev   = $main::i_day;
			$main::i_year_prev  = $main::i_year;
	
			$main::i_row=0;
			return 1;

		} else {

			return 0;

		}; 
	};


	sub new_month {

# new month/year 

		if (  (($main::i_year == $main::i_year_prev) && ($main::i_month > $main::i_month_prev)) || 
			($main::i_year > $main::i_year_prev)  ) {

				$main::first_day_of_the_month = $main::i_day;

				return 1;

		} else {


				return 0;

		}
	};

# weekly ontology of this month

	sub this_month {


		if ( ($main::i_year == $year) && ($main::i_month == $month) && ($main::i_day == 				($main::first_day_of_the_month+$length_of_the_week*
			$main::week_number) ) ) {

			$main::week_number++;
			return 1;

		} else {

			return 0;

		}

	};

# otology of last week

	sub this_week {

	
		if ( ($main::i_year == $year)  && ($main::i_month == $month) ) {


			if  ( ( $main::i_day > ($day-$length_of_the_week) ) || ($day <= $length_of_the_week) ) {

				return 1;

			}  

			else { 

				return 0;

			}
		}

	}; 


	sub delete_record($$) {

# calls delete method 

		#my $response = $ua->post(POST_URL . $_[0],[method => 'DELETE']);
 
 		#if ($response->is_success) {
     		#	print $response->decoded_content;  # or whatever
 		#}
 		#else {
			# print to log file instead
     		#	die $response->status_line;
 		#}

		
		print LOG "Delete ontology version - $_[0] => $_[1] \n";
		# print "Delete ontology version - $_[0] => $_[1] \n";

	};


};
















