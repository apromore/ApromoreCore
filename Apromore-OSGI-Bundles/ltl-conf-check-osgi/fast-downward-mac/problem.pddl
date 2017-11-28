(define (problem Align) (:domain Mining)
(:objects
t0 - state
t1 - state
t2 - state
t3 - state
t4 - state
t5 - state
t6 - state
s_0_0 - state
s_1_1 - state
s_1_0 - state
s_2_2 - state
s_2_0 - state
s_2_abstract - state
)
(:init
(currstate t0)
(currstate s_0_0)
(currstate s_1_1)
(currstate s_2_2)
(= (total-cost) 0)
)
(:goal
(and
(currstate t6)
(currstate s_0_0)
(currstate s_1_0)
(currstate s_2_abstract)
))
(:metric minimize (total-cost))
)