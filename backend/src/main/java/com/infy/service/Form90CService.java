package com.infy.service;

import com.infy.dto.Form90CDTO;
import com.infy.exception.TaxTrackerException;

public interface Form90CService {
	public Integer saveForm(Form90CDTO dto) throws TaxTrackerException;
	public Form90CDTO getFormByEmail(String emailId) throws TaxTrackerException;
}
