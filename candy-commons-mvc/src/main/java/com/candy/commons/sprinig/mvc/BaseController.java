package com.candy.commons.sprinig.mvc;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

public class BaseController implements MessageSourceAware  {

	private MessageSource messageSource;
	
	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	protected String getMessage(String code, Object[] args, Locale locale) {
		return this.messageSource.getMessage(code, args, locale);
	}
	
}

