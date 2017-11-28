(define (domain alignment)
    (:requirements :disjunctive-preconditions :conditional-effects :universal-preconditions)
    (:types trace_state automaton_state - state activity)
    (:predicates
        (trace ?t1 - trace_state ?e - activity ?t2 - trace_state)
        (cur_state ?s - state)
        (automaton ?s1 - automaton_state ?e - activity ?s2 - automaton_state)
        (final_state ?s - state)
	(linked ?t - trace_state ?s - automaton_state)
    )
    (:functions total-cost)

    (:action sync
        :parameters (?t1 - trace_state ?e - activity ?t2 - trace_state)
        :precondition (and (cur_state ?t1)(trace ?t1 ?e ?t2))
        :effect(and
                    (not (cur_state ?t1))
                    (cur_state ?t2)
                    (forall (?s1 ?s2 - automaton_state)
                        (when (and (cur_state ?s1) (automaton ?s1 ?e ?s2) (linked ?t1 ?s1)(linked ?t1 ?s2))
                            (and (not (cur_state ?s1))(cur_state ?s2)))
                    )
                )
    )

    (:action add
        :parameters (?e - activity)
        :effect (and
                    (increase (total-cost) 1)
                    (forall (?s1 ?s2 - automaton_state)
                        (when (and (cur_state ?s1) (automaton ?s1 ?e ?s2))
                            (and (not (cur_state ?s1))(cur_state ?s2)))
                    )
                )
    )

    (:action del
        :parameters (?t1 - trace_state ?e - activity ?t2 - trace_state)
        :precondition (and (cur_state ?t1)(trace ?t1 ?e ?t2))
        :effect(and
                    (increase (total-cost) 1)
                    (not (cur_state ?t1)) (cur_state ?t2)
                )
    )
)


