//==========================================================================
//   CHEAD.H  - header for
//                             OMNeT++
//            Discrete System Simulation in C++
//
//
//  Declaration of the following classes:
//    cHead     : head of a list of cObjs
//    cIterator : walks along a list
//
//==========================================================================

/*--------------------------------------------------------------*
  Copyright (C) 1992-2001 Andras Varga
  Technical University of Budapest, Dept. of Telecommunications,
  Stoczek u.2, H-1111 Budapest, Hungary.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  `license' for details on this and other legal matters.
*--------------------------------------------------------------*/

#ifndef __CHEAD_H
#define __CHEAD_H

#include "cobject.h"

//=== classes declared here
class  cIterator;
class  cHead;

//=== classes mentioned
class  cModuleInterface;
class  cModuleType;
class  cLinkType;
class  cFunctionType;
class  cNetworkType;
class  cInspectorFactory;
class  cEnum;

//==========================================================================

/**
 * Head of a cObject list. cObject and its derived classes contain
 * pointers that enable the objects to be a node in a double-linked list.
 * cObject has member functions to link and unlink to and from double-linked
 * lists (See documentation on cObject::setOwner()) The head of such
 * lists is always a cHead object. The lists are a means
 * that provide that each object in the system is part of an object
 * tree and can be accessed through pointers starting from a given
 * point. The existence of such hierarchy is necessary for a user
 * interface where we want each object to be 'visible' to the user.
 * It is also unavoidable when we want the simulation to be restartable
 * (we need to destroy objects created by the running simulation
 * to start a new one). Last, it enables that all objects can be
 * reached through forEach() on which many algorithms rely
 * (e.g. saveresults()).
 *
 * NOTE: the dup() and operator=() functions are NOT implemented.
 * dup() would require that every object in the list be duplicated.
 * Since cHead is mostly an internal class and is NOT intended
 * for use by the programmer as a container class, the dup() operation
 * was considered unnecessary.
 */
class SIM_API cHead : public cObject
{
    friend class const_cIterator;
    friend class cIterator;
    friend class cObject;

  public:
    /**
     * Constructor.
     */
   cHead(const char *name=NULL);

    /**
     * Constructor.
     */
    cHead(const char *name, cHead *h);

    /**
     * The destructor deletes all objects in the list that were created
     * on the heap.
     */
    virtual ~cHead()  {deleteChildren();}

    // redefined functions

    /**
     * Returns pointer to a string containing the class name, "cHead".
     */
    virtual const char *className() const {return "cHead";}

    /**
     * Returns the name of the inspector factory class associated with this class.
     * See cObject for more details.
     */
    virtual const char *inspectorFactoryName() const {return "cHeadIFC";}

    /**
     * Calls the function passed for each object
     * in the list.
     */
    virtual void forEach(ForeachFunc f);

       // new functions

    /**
     * Searches the list for an object with the given name and returns
     * its pointer. If no such object was found, NULL is returned.
     */
    cObject *find(const char *objname);

    /**
     * Returns the number of objects in the list.
     */
    int count();
};

//==========================================================================

/**
 * Walks along a cHead-cObject list.
 *
 * NOTE: not a cObject descendant.
 */
class SIM_API cIterator
{
  private:
    cObject *p;

  public:
    /**
     * Constructor.
     */
    cIterator(cObject& h)    {p = &h ? h.firstchildp : NO(cObject);}

    /**
     * MISSINGDOC: cIterator:void init(cObject&)
     */
    void init(cObject& h)    {p = &h ? h.firstchildp : NO(cObject);}

    /**
     * Returns a pointer to the current object.
     */
    cObject *operator()()    {return p;}

    /**
     * Returns true if we reach the end of the list.
     */
    bool end()               {return (bool)(p==NULL);}

    /**
     * MISSINGDOC: cIterator:cObject*operator++()
     */
    cObject *operator++(int) {cObject *t=p; if(p) p=p->nextp; return t;}
};


/**
 * Walks along a cHead-cObject list.
 */
class SIM_API const_cIterator
{
  private:
    const cObject *p;

  public:
    /**
     * MISSINGDOC: const_cIterator:_cIterator(cObject&)
     */
    const_cIterator(const cObject& h)    {p = &h ? h.firstchildp : NO(cObject);}

    /**
     * MISSINGDOC: const_cIterator:void init(cObject&)
     */
    void init(const cObject& h)    {p = &h ? h.firstchildp : NO(cObject);}

    /**
     * MISSINGDOC: const_cIterator:cObject*operator()()
     */
    const cObject *operator()() const {return p;}

    /**
     * MISSINGDOC: const_cIterator:bool end()
     */
    bool end() const               {return (bool)(p==NULL);}

    /**
     * MISSINGDOC: const_cIterator:cObject*operator++()
     */
    const cObject *operator++(int) {const cObject *t=p; if(p) p=p->nextp; return t;}
};

//==========================================================================

/**
 * @name Find global objects by name.
 */
//@{
/// Find a cNetworkType.
inline cNetworkType *findNetwork(const char *s)    {return (cNetworkType *)networks.find(s);}
/// Find a cModuleType.
inline cModuleType *findModuleType(const char *s)  {return (cModuleType *)modtypes.find(s);}
/// Find a cModuleInterface.
inline cModuleInterface *findModuleInterface(const char *s) {return (cModuleInterface *)modinterfaces.find(s);}
/// Find a cLinkType.
inline cLinkType *findLink(const char *s)          {return (cLinkType *)linktypes.find(s);}
/// Find a cFunctionType.
inline cFunctionType *findFunction(const char *s)  {return (cFunctionType *)functions.find(s);}
/// Find a cInspectorFactory.
inline cInspectorFactory *findInspectorFactory(const char *s) {return (cInspectorFactory *)inspectorfactories.find(s);}
/// Find a cEnum.
inline cEnum *findEnum(const char *s)              {return (cEnum *)enums.find(s);}
//@}

#endif

