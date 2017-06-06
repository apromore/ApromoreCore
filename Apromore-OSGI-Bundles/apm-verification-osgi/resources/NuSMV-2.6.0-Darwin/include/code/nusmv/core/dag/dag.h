/* ---------------------------------------------------------------------------


  This file is part of the ``dag'' package of NuSMV version 2.
  Copyright (C) 2000-2001 by University of Genova.
  Copyright (C) 2011 by FBK.

  NuSMV version 2 is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2 of the License, or (at your option) any later version.

  NuSMV version 2 is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.

  For more information on NuSMV see <http://nusmv.fbk.eu>
  or email to <nusmv-users@fbk.eu>.
  Please report bugs to <nusmv-users@fbk.eu>.

  To contact the NuSMV development board, email to <nusmv@fbk.eu>. 

-----------------------------------------------------------------------------*/

/*!
  \author Armando Tacchella
  \brief Directed acyclic graphs with sharing.

  External functions and data strucures of the dag package.

*/


#ifndef __NUSMV_CORE_DAG_DAG_H__
#define __NUSMV_CORE_DAG_DAG_H__

#if HAVE_CONFIG_H
# include "nusmv-config.h"
#endif


/* Standard includes. */
#if NUSMV_HAVE_MALLOC_H
# if NUSMV_HAVE_SYS_TYPES_H
#  include <sys/types.h>
# endif
# include <malloc.h>
#elif defined(NUSMV_HAVE_SYS_MALLOC_H) && NUSMV_HAVE_SYS_MALLOC_H
# if NUSMV_HAVE_SYS_TYPES_H
#  include <sys/types.h>
# endif
# include <sys/malloc.h>
#elif NUSMV_HAVE_STDLIB_H
# include <stdlib.h>
#endif

#if NUSMV_HAVE_UNISTD_H
#include <unistd.h>
#endif

/* GLU library includes. */
#include "nusmv/core/utils/utils.h"
#include "cudd/util.h"
#include "cudd/st.h"
#include "nusmv/core/utils/list.h"

/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/* Default parameters for the unique table. */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DAG_DEFAULT_VERTICES_NO    65537

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DAG_DEFAULT_DENSITY           20

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DAG_DEFAULT_GROWTH           1.5

/* Constants for setting and clearing pointer annotation bit which is
   lowest (rightmost) bit.
*/
#if !defined(NUSMV_SIZEOF_VOID_P) || !defined(NUSMV_SIZEOF_LONG)
#error Constants NUSMV_SIZEOF_VOID_P and NUSMV_SIZEOF_LONG must be defined
#endif

#if (NUSMV_SIZEOF_VOID_P == NUSMV_SIZEOF_LONG)
/* The lowest bit of a pointer is used to store additional info
   (about complementation). We can use this bit only because pointers
   never use it (it is always 0 for any pointer). This is
   ensured by the alignment of pointers which is required to be at
   least 2 bytes.

   Note: in past the highest (leftmost) bit was used as the annotation
   bit. But it appeared that if the library is invoked from Java on
   multi-processor machine then malloc behaves differently and it is
   possible for a pointer to be from the higher half of memory, i.e.
   in memory address the highest bit could be 0 as well as 1. As
   result we could not use it any more and changed the
   implementation.

   Current implementation is similar to CUDD's one.

   Warning: on most machine alignment is 4 bytes which is more than
   enough for us.  However in some case additional compilation options
   (e.g. -malign-double) may be required to force proper alignment.
*/
# define DAG_ANNOTATION_BIT  ((nusmv_ptrint) 1)
#else
/* this fallback setting is for 32-bit only */
/* Size of pointers and long are different (may hide a serious problem) */
# define DAG_ANNOTATION_BIT   ((nusmv_ptrint) 1)
#endif


/* Dag statistics. */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DAG_NODE_NO  (int)  0  /* How many nodes created (overall). */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DAG_GC_NO    (int)  1  /* How many nodes collected. */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define DAG_MAX_STAT (int)  2

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/* WARNING [MD] BAD! Using those we lose the check on function parameters!
   They should be substituted */
/* typedef void  (*Dag_ProcPtr_t)(); */   /* Procedures. */
/* typedef int   (*Dag_IntPtr_t)();  */   /* Functions returning int. */

typedef struct DagManager       Dag_Manager_t;       /* The dag manager. */
typedef struct Dag_Vertex       Dag_Vertex_t;        /* The vertices. */
typedef struct Dag_DfsFunctions Dag_DfsFunctions_t;  /* Depth First Search. */

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef void (*PF_VPVPCPI)(void*, char*, nusmv_ptrint);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef int (*PF_IVPCPI)(void*, char*, nusmv_ptrint);
typedef void (*PF_VPCP)(char*);

/*---------------------------------------------------------------------------*/
/* Stucture declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief DAG vertex.

  The main fields (used for hashing) are:
                 <ul>
                 <li> symbol, an integer code
                 <li> data, a generic pointer (vertex annotation)
                 <li> outList, a list of sons ((lsList)NULL for leafs)
                 </ul>
                 Some fields are for internal purposes:
                 <ul>
                 <li> dag, a reference to the dag manager that owns the node
                 <li> mark, how many fathers (for garbage collection)
                 <li> visit, how many visits (for DFS)
                 <li> vBro, the vertex brother (a fatherless brother is rescued
                                                from GC by its non-orphan one)
                 <li> vHandle, back-reference to the free list
                 </ul>
                 The fields above should never be modified directly, unless
                 it is clear how they work! General purpose fields are:
                 <ul>
                 <li> gRef, a generic char pointer
                 <li> iRef, a generic integer value
                 </ul>
                 The dag manager makes no assumptions about the latter
                 fields and no efforts to ensure their integrity.
                 </ul>
*/

struct Dag_Vertex {
  int             symbol;
  char          * data;

  Dag_Vertex_t* * outList;
  unsigned        numSons;

  Dag_Manager_t * dag;
  int             mark;
  int             visit;
  lsHandle        vHandle;

  char          * gRef;
  int             iRef;
};


/*!
  \brief DFS function struct.

  The generic DFS functions:
                 <ul>
                 <li> Set, may force a different behaviour in the visit
                 <li> FirstVisit, invoked at the beginning of the visit
                 <li> BackVisit, invoked after each operand's visit
                 <li> LastVisit, invoked at the end of the visit
                 </ul>
                 All functions must be of the form:

                 <type> f(Dag_Vertex_t * v, char * d, int b)

                 where <type>=(int) in the case of `Set()' and <type>=(void)
                 in all the other cases. `v' is the current vertex, `d' is
                 a generic data reference, and `b' is set to the incoming edge
                 annotation (if any). DFS beahaves differently according to
                 the return value of `Set()': -1 forces visiting, 0 default
                 behaviour (all nodes visited once and only once), 1 forces
                 backtracking.
*/

/* struct Dag_DfsFunctions { */
/*   Dag_IntPtr_t   Set; */
/*   Dag_ProcPtr_t  FirstVisit; */
/*   Dag_ProcPtr_t  BackVisit; */
/*   Dag_ProcPtr_t  LastVisit; */
/* }; */

struct Dag_DfsFunctions {
  PF_IVPCPI Set;
  PF_VPVPCPI FirstVisit;
  PF_VPVPCPI BackVisit;
  PF_VPVPCPI LastVisit;
};

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/

/*!
  \brief Filters a pointer from bit annotations.

  The annotation bit is filtered to 0. The result is the pointer
               purified from the bit annotation.

  \se none
*/
#define Dag_VertexGetRef(p)\
((Dag_Vertex_t*)((nusmv_ptrint)p & (~ DAG_ANNOTATION_BIT)))

/*!
  \brief Sets (forces) a bit annotation to 1.

  The annotation bit is forced to 1 by a bitwise-or with
               DAG_ANNOTATION_BIT mask.

  \se The value of p changes to the purified value.
*/
#define Dag_VertexSet(p)\
(p = (Dag_Vertex_t*)((nusmv_ptrint)p | DAG_ANNOTATION_BIT))

/*!
  \brief Clears (forces) a bit annotation to 0.

  The annotation bit is forced to 0 by a bitwise-and with
               complement of DAG_ANNOTATION_BIT mask.

  \se The value of p changes to the purified value.
*/
#define Dag_VertexClear(p)\
(p = (Dag_Vertex_t*)((nusmv_ptrint)p & (~ DAG_ANNOTATION_BIT)))

/*!
  \brief Tests if the edge is annotated.

  Uses a bitwise-and with DAG_ANNOTATION_BIT to test the
               annotation bit of p. The result is either 0(false) or
               not 0(true)

  \se none
*/
#define Dag_VertexIsSet(p)\
((nusmv_ptrint)p & DAG_ANNOTATION_BIT)

/*!
  \brief Controls the sign of a dag.

  The pointer is filtered by a bitwise-xor with either
               DAG_ANNOTATION_BIT or !DAG_ANNOTATION_BIT. The pointer is not
               altered, but the leftmost bit is complemented when
               s==DAG_ANNOTATION_BIT and goes unchanged when
               s!=DAG_ANNOTATION_BIT.

  \se none
*/
#define DagId(r,s) \
  (Dag_Vertex_t*)((nusmv_ptrint)s ^ (nusmv_ptrint)r)

/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
void
Dag_Dfs(Dag_Vertex_t* dfsRoot, Dag_DfsFunctions_t* dfsFun, char* dfsData);

/*!
  \brief Creates a new DAG manager.

  Allocates the unique table (vTable) and the free list (gcList).
               Initializes the counters for various statistics (stats).
               Returns the pointer to the dag manager.

  \se none

  \sa Dag_ManagerAllocWithParams Dag_ManagerFree
*/
Dag_Manager_t* Dag_ManagerAlloc(void);

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
Dag_Manager_t*
Dag_ManagerAllocWithParams(int dagInitVerticesNo, int maxDensity,
                           int growthFactor);

/*!
  \brief Deallocates a DAG manager.

  Forces a total garbage collection and then deallocates the
               dag manager. `freeData' can be used to deallocate `data'
               fields (user data pointers) in the nodes, while `freeGen'
               is applied to `gRef' fields (user generic pointers).
               `freeData' and `freeGen' are in the form `void f(char * r)'.

  \se none

  \sa Dag_ManagerGC
*/
void
Dag_ManagerFree(Dag_Manager_t* dagManager, PF_VPCP freeData,
                PF_VPCP freeGen);

/*!
  \brief Garbage collects the DAG manager.

  Sweeps out useless vertices, i.e., vertices that are not
               marked as permanent, that are not descendants
               of permanent vertices, or whose brother (if any) is neither
               permanent nor descendant of a permanent vertex.
               The search starts from vertices that are in the garbage
               bin and whose mark is 0.
               `freeData' can be used to deallocate `data'
               fields (user data pointers) in the nodes, while `freeGen'
               is applied to `gRef' fields (user generic pointers).
               `freeData' and `freeGen' are in the form `void f(char * r)'.

  \se none

  \sa Dag_ManagerFree
*/
void
Dag_ManagerGC(Dag_Manager_t* dagManager, PF_VPCP freeData,
              PF_VPCP freeGen);

Dag_DfsFunctions_t* Dag_ManagerGetDfsCleanFun(Dag_Manager_t* dagManager);

/*!
  \brief Prints various statistics.

  Prints the following:
               <ul>
               <li> the number of entries found in every chunk of
                    `clustSz' bins (if `clustSz' is 1 then the number
                    of entries per bin is given, if `clustSz' is 0 no
		    such information is displayed);
               <li> the number of shared vertices, i.e., the number
                    of v's such that v -> mark > 1;
	       <li> the average entries per bin and the variance;
	       <li> min and max entries per bin.
               </ul>

  \se none
*/
void Dag_PrintStats(Dag_Manager_t* dagManager, int clustSz,
                           FILE* outFile);

/*!
  \brief Vertex lookup.

  Uniquely adds a new vertex into the DAG and returns a
               reference to it:
               <ul>
               <li> vSymb is a NON-NEGATIVE  integer (vertex label);
               <li> vData is a pointer to generic user data;
               <li> vSons is a list of vertices (possibly NULL).
               </ul>
               Returns NIL(Dag_vertex_t) if there is no dagManager and 
               if vSymb is negative.

  \se none
*/
Dag_Vertex_t*
Dag_VertexLookup(Dag_Manager_t* dagManager,
                 int vSymb,
                 char* vData,
                 Dag_Vertex_t** vSons,
                 unsigned numSons);

/*!
  \brief Vertex insert.

  Adds a vertex into the DAG and returns a
               reference to it:
               <ul>
               <li> vSymb is an integer code (vertex label);
               <li> vData is a generic annotation;
               <li> vSons must be a list of vertices (the intended sons).
               </ul>
               Returns NIL(Dag_vertex_t) if there is no dagManager and
               if vSymb is negative.

  \se none
*/
Dag_Vertex_t*
Dag_VertexInsert(Dag_Manager_t* dagManager,
                 int vSymb,
                 char* vData,
                 Dag_Vertex_t** vSons,
                 unsigned numSons);

/*!
  \brief Marks a vertex as permanent.

  Increments the vertex mark by one, so it cannot be
               deleted by garbage collection unless unmarked.

  \se none
*/
void Dag_VertexMark(Dag_Vertex_t* v);

/*!
  \brief Unmarks a vertex (makes it volatile).

  Decrements the vertex mark by one, so it can be
               deleted by garbage collection when fatherless.

  \se none
*/
void Dag_VertexUnmark(Dag_Vertex_t* v);

/*!
  \brief Visit a DAG to compute some statistics

  Calls Depth First Search on the DAG dfsRoot to populate
                      the struct Statistics.
                      Then calls _PrintStat to print out them.
*/
void PrintStat(Dag_Vertex_t* dfsRoot, FILE* statFile, char* prefix);

/**AutomaticEnd***************************************************************/




#endif /* __NUSMV_CORE_DAG_DAG_H__ */
