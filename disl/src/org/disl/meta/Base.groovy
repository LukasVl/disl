package org.disl.meta

import java.lang.reflect.Field;


class Base {
	
	String name
	
	public String getName() {
		if (name==null) {
			return this.getClass().getSimpleName()
		}
		name
	}
	
	public String toString() {
		getName()
	}
	
	public List<Field> getFieldsByType(Class type) {
		getClass().getDeclaredFields().findAll {type.isAssignableFrom(it.getType())}
	}
	
	public List getPropertyValuesByType(Class type) {
		return metaClass.properties.findAll({type.isAssignableFrom(it.type)}).collect({this.getProperty(it.name)});
	}
	
	public List<String> getPropertyNamesByType(Class type) {
		return metaClass.properties.findAll({type.isAssignableFrom(it.type)}).collect({it.name});
	}

}