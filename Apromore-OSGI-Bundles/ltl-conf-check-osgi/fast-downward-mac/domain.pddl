(define (domain Mining)
(:requirements :typing :equality)
(:types state)

(:predicates
(currstate ?s - state)
)

(:functions
(total-cost)
)

(:action add-A-
:precondition (currstate s_2_2) 
:effect (and (not (currstate s_2_2)) (currstate s_2_0)  (increase (total-cost) 1))
)

(:action sync-A-ct0
:precondition (and (currstate t2) (currstate s_2_2) )
:effect (and (not (currstate t2)) (currstate t3) (not (currstate s_2_2)) (currstate s_2_0) )
)

(:action add-New***Task-
:precondition (currstate s_1_1) 
:effect (and (not (currstate s_1_1)) (currstate s_1_0)  (increase (total-cost) 1))
)

(:action sync-Start-t0t1
:precondition (currstate t0) 
:effect (and (not (currstate t0)) (currstate t1)))

(:action del-Start-t0-t1
:precondition (currstate t0)
:effect (and (not (currstate t0)) (currstate t1)  (increase (total-cost) 1))
)

(:action sync-B-t1t2
:precondition (and (currstate t1) (not (currstate s_2_2) ))
:effect (and (not (currstate t1)) (currstate t2)))

(:action del-B-t1-t2
:precondition (currstate t1)
:effect (and (not (currstate t1)) (currstate t2)  (increase (total-cost) 1))
)

(:action sync-A-t2t3
:precondition (and (currstate t2) (not (currstate s_2_2)) )
:effect (and (not (currstate t2)) (currstate t3)))

(:action del-A-t2-t3
:precondition (currstate t2)
:effect (and (not (currstate t2)) (currstate t3)  (increase (total-cost) 1))
)

(:action sync-C-t3t4
:precondition (currstate t3) 
:effect (and (not (currstate t3)) (currstate t4)))

(:action del-C-t3-t4
:precondition (currstate t3)
:effect (and (not (currstate t3)) (currstate t4)  (increase (total-cost) 1))
)

(:action sync-Down-t4t5
:precondition (currstate t4) 
:effect (and (not (currstate t4)) (currstate t5)))

(:action del-Down-t4-t5
:precondition (currstate t4)
:effect (and (not (currstate t4)) (currstate t5)  (increase (total-cost) 1))
)

(:action sync-End_Activity-t5t6
:precondition (and (currstate t5) (not (currstate s_0_0) ))
:effect (and (not (currstate t5)) (currstate t6)))

(:action del-End_Activity-t5-t6
:precondition (currstate t5)
:effect (and (not (currstate t5)) (currstate t6)  (increase (total-cost) 1))
)

(:action goto-abstract_states-cs0
:precondition (and (currstate t6) (currstate s_0_0) (currstate s_1_0) (currstate s_2_2) )
:effect (and (currstate s_2_abstract) (not (currstate s_2_2)) )
)

(:action goto-abstract_states-cs1
:precondition (and (currstate t6) (currstate s_0_0) (currstate s_1_0) (currstate s_2_0) )
:effect (and (currstate s_2_abstract) (not (currstate s_2_0)) )
)

)