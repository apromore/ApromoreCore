/* ---------------------------------------------------------------------------


  This file is part of the ``trans'' package of NuSMV version 2. 
  Copyright (C) 2003 by FBK-irst. 

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
  \brief  The public interface of the <tt>trans</tt> package.

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_TRANS_TRANS_H__
#define __NUSMV_CORE_TRANS_TRANS_H__



/*---------------------------------------------------------------------------*/
/* Constant declarations                                                     */
/*---------------------------------------------------------------------------*/

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRANS_TYPE_MONOLITHIC_STRING  "Monolithic"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRANS_TYPE_THRESHOLD_STRING   "Threshold"

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TRANS_TYPE_IWLS95_STRING      "Iwls95CP" 

/*---------------------------------------------------------------------------*/
/* Type declarations                                                         */
/*---------------------------------------------------------------------------*/

/*!
  \brief Use one of this id when creating a trans

  If you modify this type, also modify the corresponding 
  string definition in transTrans.c
*/

typedef enum TransType_TAG { 
  TRANS_TYPE_INVALID = -1, 
  TRANS_TYPE_MONOLITHIC = 0, 
  TRANS_TYPE_THRESHOLD, 
  TRANS_TYPE_IWLS95 
} TransType; 

/*---------------------------------------------------------------------------*/
/* Structure declarations                                                    */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/* Variable declarations                                                     */
/*---------------------------------------------------------------------------*/


/*---------------------------------------------------------------------------*/
/*  Public methods:                                                          */
/*---------------------------------------------------------------------------*/

/*!
  \brief  string to TransType

  Converts the given transition type from string "name" to
  TransType object. The possible values of name can be "Monolithic",
  "Threshold", or "Iwls95CP".

  \se None.

  \sa  TransType_to_string 
*/
TransType TransType_from_string(const char* name);

/*!
  \brief  TransType to string 

   It takes TransType of self and returns a string
  specifying the type of the transition relation. Returned string is statically
  allocated and must not be freed. 

  \sa TransType_from_string
*/
char* TransType_to_string(const TransType self);



#endif /* __NUSMV_CORE_TRANS_TRANS_H__ */
