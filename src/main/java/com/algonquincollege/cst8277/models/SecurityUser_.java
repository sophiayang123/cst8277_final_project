package com.algonquincollege.cst8277.models;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2020-11-26T15:49:14.278-0500")
@StaticMetamodel(SecurityUser.class)
public class SecurityUser_ {
	public static volatile SingularAttribute<SecurityUser, Integer> id;
	public static volatile SingularAttribute<SecurityUser, String> username;
	public static volatile SingularAttribute<SecurityUser, String> pwHash;
	public static volatile SetAttribute<SecurityUser, SecurityRole> roles;
	public static volatile SingularAttribute<SecurityUser, CustomerPojo> customer;
}
