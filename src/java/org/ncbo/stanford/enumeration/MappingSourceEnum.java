package org.ncbo.stanford.enumeration;

public enum MappingSourceEnum {

	ORGANIZATION {
		public String toString() {
			return "Organization";
		}
	},
	APPLICATION {
		public String toString() {
			return "Application";
		}
	};
}
