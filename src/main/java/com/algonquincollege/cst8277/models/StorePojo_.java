package com.algonquincollege.cst8277.models;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2020-11-26T15:49:14.302-0500")
@StaticMetamodel(StorePojo.class)
public class StorePojo_ extends PojoBase_ {
	public static volatile SingularAttribute<StorePojo, String> storeName;
	public static volatile SetAttribute<StorePojo, ProductPojo> products;
}
