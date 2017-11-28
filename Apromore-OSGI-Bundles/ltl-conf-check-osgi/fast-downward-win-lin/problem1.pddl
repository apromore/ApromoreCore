(define (problem prob-trace)
	(:domain alignment)
	(:objects
		t11 t12 t13 t14 t15 t16 
		t21 t22 t23 t24 t25 - trace_state

		s11 s12 s13 s21 s22 s23 s31 s32 s33 s34
		s41 s42 s51 s52 s53 s61 s62 - automaton_state
		
		a b c d e f l - activity
	)
	(:init
		(= (total-cost) 0)

		;trace transitions
			
		(cur_state t11)
		(trace t11 a t12)
		(trace t12 c t13)
		(trace t13 d t14)
		(trace t14 e t15)
		(trace t15 f t16)
		(final_state t16)
		
		(cur_state t21)
		(trace t21 a t22)
		(trace t22 b t23)
		(trace t23 e t24)
		(trace t24 f t25)
		(final_state t25)

		;DECLARE automata

		;precedence(d,e) per t1
			
		(cur_state s11)
		(final_state s11)
		(final_state s12)
		(automaton s11 e s13)
		(automaton s11 d s12)

		(linked t11 s11)
		(linked t11 s12)
		(linked t11 s13)

		(linked t12 s11)
		(linked t12 s12)
		(linked t12 s13)

		(linked t13 s11)
		(linked t13 s12)
		(linked t13 s13)

		(linked t14 s11)
		(linked t14 s12)
		(linked t14 s13)

		(linked t15 s11)
		(linked t15 s12)
		(linked t15 s13)

		(linked t16 s11)
		(linked t16 s12)
		(linked t16 s13)

					
		
		;precedence(d,e) per t2
			
		(cur_state s21)
		(final_state s21)
		(final_state s22)
		(automaton s21 e s23)
		(automaton s21 d s22)

		(linked t21 s21)
		(linked t21 s22)
		(linked t21 s23)

		(linked t22 s21)
		(linked t22 s22)
		(linked t22 s23)

		(linked t23 s21)
		(linked t23 s22)
		(linked t23 s23)

		(linked t24 s21)
		(linked t24 s22)
		(linked t24 s23)

		(linked t25 s21)
		(linked t25 s22)
		(linked t25 s23)

		

		
		;existence(e)

		(cur_state s41)
		(final_state s42)
		(automaton s41 e s42)

		(linked t21 s41)
		(linked t21 s42)

		(linked t22 s41)
		(linked t22 s42)

		(linked t23 s41)
		(linked t23 s42)

		(linked t24 s41)
		(linked t24 s42)

		(linked t25 s41)
		(linked t25 s42)

	
	)
	(:goal (forall (?s - state) (imply (cur_state ?s)(final_state ?s))))
	(:metric minimize (total-cost))
)
