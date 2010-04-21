package org.ncbo.stanford.enumeration;

public enum NoteAppliesToTypeEnum {

	Class {
		public String toString() {
			return "Class";
		}
	},
	Individual {
		public String toString() {
			return "Individual";
		}
	},
	Property {
		public String toString() {
			return "Property";
		}
	},
	Note {
		public String toString() {
			return "Note";
		}
	},
	Ontology {
		public String toString() {
			return "Ontology";
		}
	},
	Default {
		public String toString() {
			return "Ontology";
		}
	};

}
