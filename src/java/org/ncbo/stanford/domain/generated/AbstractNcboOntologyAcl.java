package org.ncbo.stanford.domain.generated;



/**
 * AbstractNcboOntologyAcl entity provides the base persistence definition of the NcboOntologyAcl entity. @author MyEclipse Persistence Tools
 */

public abstract class AbstractNcboOntologyAcl  implements java.io.Serializable {


    // Fields    

     private Integer id;
     private Integer ontologyId;
     private Integer userId;
     private Boolean isOwner;


    // Constructors

    /** default constructor */
    public AbstractNcboOntologyAcl() {
    }

    
    /** full constructor */
    public AbstractNcboOntologyAcl(Integer ontologyId, Integer userId, Boolean isOwner) {
        this.ontologyId = ontologyId;
        this.userId = userId;
        this.isOwner = isOwner;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOntologyId() {
        return this.ontologyId;
    }
    
    public void setOntologyId(Integer ontologyId) {
        this.ontologyId = ontologyId;
    }

    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getIsOwner() {
        return this.isOwner;
    }
    
    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }
   








}