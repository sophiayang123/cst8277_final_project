package com.algonquincollege.cst8277.models;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2020-11-23T17:34:09.795-0500")
@StaticMetamodel(OrderLinePojo.class)
public class OrderLinePojo_ {
	public static volatile SingularAttribute<OrderLinePojo, OrderLinePk> pk;
	public static volatile SingularAttribute<OrderLinePojo, OrderPojo> owningOrder;
	public static volatile SingularAttribute<OrderLinePojo, Double> amount;
	public static volatile SingularAttribute<OrderLinePojo, ProductPojo> product;
}
