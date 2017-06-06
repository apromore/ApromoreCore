/* ---------------------------------------------------------------------------


  This file is part of the ``utils'' package of NuSMV version 2. 
  Copyright (C) 2008 by FBK-irst. 

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
  \brief Public interface of class TimerBench

  \todo: Missing description

*/


#ifndef __NUSMV_CORE_UTILS_TIMER_BENCH_H__
#define __NUSMV_CORE_UTILS_TIMER_BENCH_H__

#include "nusmv/core/utils/utils.h"

/*!
  \struct TimerBench
  \brief \todo Missing synopsis

  \todo Missing description
*/
typedef struct TimerBench_TAG* TimerBench_ptr;

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TIMER_BENCH(self) \
     ((TimerBench_ptr) self)

/*!
  \brief \todo Missing synopsis

  \todo Missing description
*/
#define TIMER_BENCH_CHECK_INSTANCE(self) \
      (nusmv_assert(TIMER_BENCH(self) != TIMER_BENCH(NULL)))


/*!
  \methodof TimerBench
  \todo
*/
TimerBench_ptr TimerBench_create(const char* name);
/*!
  \methodof TimerBench
  \todo
*/
void TimerBench_destroy(TimerBench_ptr self);

/*!
  \methodof TimerBench
  \todo
*/
void TimerBench_start(TimerBench_ptr self);
/*!
  \methodof TimerBench
  \todo
*/
void TimerBench_stop(TimerBench_ptr self);
/*!
  \methodof TimerBench
  \todo
*/
void TimerBench_reset(TimerBench_ptr self);

/*!
  \methodof TimerBench
  \todo
*/
boolean TimerBench_is_running(const TimerBench_ptr self);

/*!
  \methodof TimerBench
  \todo
*/
long TimerBench_get_time(const TimerBench_ptr self);
/*!
  \methodof TimerBench
  \todo
*/
long TimerBench_get_laps(const TimerBench_ptr self);

/*!
  \methodof TimerBench
  \todo
*/
void TimerBench_print(const TimerBench_ptr self, FILE* file, 
                      const char* msg);


#endif /* __NUSMV_CORE_UTILS_TIMER_BENCH_H__ */
