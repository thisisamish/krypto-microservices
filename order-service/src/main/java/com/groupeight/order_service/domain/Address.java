package com.groupeight.order_service.domain;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
public class Address implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fullName;
	private String line1;
	private String line2;
	private String city;
	private String state;
	private String postalCode;
	private String country;
	private String phone;
}
