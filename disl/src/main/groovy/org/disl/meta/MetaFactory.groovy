/*
 * Copyright 2015 Karel H�bl <karel.huebl@gmail.com>.
 *
 * This file is part of disl.
 *
 * Disl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Disl.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.disl.meta

import groovy.io.FileType

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier

import org.disl.workflow.ClassFinder;
/**
 * Factory for DISL model objects.
 * */
class MetaFactory {
	static <T> T create(Class<T> type, Closure initClosure=null) {
		try {
			createInstance(type,initClosure)
		} catch (Exception e) {
			throw new RuntimeException("Unable to create instance of class ${type.getName()}",e);
		}
	}

	private static <T> T createInstance(Class<T> type, Closure initClosure) {
		Constructor<T> constructor=type.getDeclaredConstructor(new Class[0])
		constructor.setAccessible(true)		 
		T instance=constructor.newInstance(new Object[0])
		initInstance(instance,initClosure)
		instance
	}

	private static void initInstance(Object instance,Closure initClosure=null) {
		if (initClosure) {
			initClosure.call(instance)
		}
		if (instance instanceof Initializable) {
			instance.init();
		}
	}
	
	

	/**
	 * Creates instances for all found classes in given rootPackage (including subpackages) which are assignable to assignableType. 
	 * Only classes located in the same class path element (jar file or directory) as the sourceClass will be found and created!
	 * 
	 * Example: 
	 * //Generate all tables defined in disl model for your data warehouse.
	 * MetaFactory.createAll(com.yourDw.AbstractDwTable,"com.yourDw",AbstractDwTable).each({it.generate})
	 * */
	static Collection createAll(Class sourceClass,String rootPackage,Class assignableType) {
		Collection<Class> typesToCreate=ClassFinder.createClassFinder(sourceClass).findNonAbstractTypes(rootPackage,assignableType)
		if (typesToCreate.size()==0) {
			throw new RuntimeException('No classes found!')
		}
		typesToCreate.collect {create(it)}
	}
	
	


}
