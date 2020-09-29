package org.apromore.calender.exception;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CalenderAlreadyExistsException extends Exception {

	@NonNull
	String messageString;
	
	
}
