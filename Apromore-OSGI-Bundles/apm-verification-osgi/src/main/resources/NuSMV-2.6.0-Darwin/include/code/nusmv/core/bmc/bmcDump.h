/* ---------------------------------------------------------------------------


  This file is part of the ``bmc'' package of NuSMV version 2.
  Copyright (C) 2000-2001 by FBK-irst and University of Trento.

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
  \author Roberto Cavada
  \brief Public interface for dumping functionalities, like dimacs

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_BMC_BMC_DUMP_H__
#define __NUSMV_CORE_BMC_BMC_DUMP_H__

#include <stdio.h>

#include "nusmv/core/enc/be/BeEnc.h"

#include "nusmv/core/utils/utils.h"
#include "nusmv/core/prop/Prop.h"


/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief 

  
*/
typedef enum { 
  BMC_DUMP_NONE, 
  BMC_DUMP_DIMACS, 
  BMC_DUMP_DA_VINCI, 
  BMC_DUMP_GDL 
} Bmc_DumpType;

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Macro declarations                                                        */
/*---------------------------------------------------------------------------*/


/**AutomaticStart*************************************************************/

/*---------------------------------------------------------------------------*/
/* Function prototypes                                                       */
/*---------------------------------------------------------------------------*/

/*!
  \brief Dumps a cnf in different formats

  

  \se None
*/
void 
Bmc_Dump_WriteProblem(const BeEnc_ptr be_enc, 
                const Be_Cnf_ptr cnf, 
                Prop_ptr prop, 
                const int k, const int loop, 
                const Bmc_DumpType dump_type,
                const char* dump_fname_template);

/*!
  \brief Opens a new file named filename, than dumps the given
  invar problem in DIMACS format

  
*/
int 
Bmc_Dump_DimacsInvarProblemFilename(const BeEnc_ptr be_enc, 
                      const Be_Cnf_ptr cnf, 
                      const char* filename);

/*!
  \brief Opens a new file named filename, than dumps the given
  LTL problem in DIMACS format

  
*/
int 
Bmc_Dump_DimacsProblemFilename(const BeEnc_ptr be_enc, 
                     const Be_Cnf_ptr cnf,
                     const char* filename,  
                     const int k);

/*!
  \brief Dumps the given invar problem in the given file

  dimacsfile must be writable
*/
void 
Bmc_Dump_DimacsInvarProblem(const BeEnc_ptr be_enc, 
                  const Be_Cnf_ptr cnf,
                  FILE* dimacsfile);

/*!
  \brief Dumps the given LTL problem in the given file

  dimacsfile must be writable
*/
void 
Bmc_Dump_DimacsProblem(const BeEnc_ptr be_enc, 
                 const Be_Cnf_ptr cnf,
                 const int k, FILE* dimacsfile); 

/**AutomaticEnd***************************************************************/

#endif /* __NUSMV_CORE_BMC_BMC_DUMP_H__ */

