package org.ncbo.stanford.enumeration;

public enum ConceptTypeEnum {

	CONCEPT_TYPE_CLASS {
		public String toString() {
			return "Class";
		}
	},
	CONCEPT_TYPE_INDIVIDUAL {
		public String toString() {
			return "Individual";
		}
	},
	CONCEPT_TYPE_PROPERTY {
		public String toString() {
			return "Property";
		}
	};
}
