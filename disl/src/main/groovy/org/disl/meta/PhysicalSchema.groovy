/*
 * Copyright 2015 - 2016 Karel H�bl <karel.huebl@gmail.com>.
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
package org.disl.meta;

import java.util.List;
import java.util.Map;

import org.disl.db.reverseEngineering.ReverseEngineeringService;

import groovy.sql.Sql
import groovy.transform.CompileStatic

/**
 * Generic physical database schema. 
 * Context is used to map logical schemas used in DISL model to physical schemas.
 * Physical schemas are specific for each execution environment (Context).
 * */
@CompileStatic 
public abstract class PhysicalSchema {
	
	String name
	String user
	String password
	String schema
	SqlProxy sqlProxy
	
	abstract String getJdbcDriver()
	abstract String getJdbcUrl()
	
	abstract String evaluateExpressionQuery(String expression)
	abstract String evaluateConditionQuery(String expression)
	abstract getRecordQuery(int index,String expressions);

	
	public void init() {
		Context context=Context.getContext();
		user=getSchemaProperty("user",user)
		password=getSchemaProperty("password",password)
		jdbcDriver=getSchemaProperty("jdbcDriver",jdbcDriver)
		schema=getSchemaProperty("schema",schema)
	}
	
	protected String getSchemaProperty(String key, String defaultValue) {
		Context.getContext().getProperty("${name}.${key}",defaultValue)
	}
	
	protected String getSchemaProperty(String key) {
		Context.getContext().getProperty("${name}.${key}")
	}
	
	public Sql getSql(){
		if (sqlProxy==null || (sqlProxy.sql.connection==null || sqlProxy.sql.connection.isClosed())) {
			sqlProxy=createSqlProxy()	
		}
		sqlProxy.sql
	}

	protected SqlProxy createSqlProxy() {
		def sql=createSql()
		return new SqlProxy(sql: sql)
	}
	
	protected Sql createSql() {
		def sql=Sql.newInstance(getJdbcUrl(), getUser(), getPassword(), getJdbcDriver())
		sql.getConnection().setAutoCommit(false)
		sql.cacheConnection=true
		sql.cacheStatements=false
		return sql
	}
	
	public String recordsToSubquery(List<Map> records) {
		String joinCondition=""
		List aliases=findAliases(records)
		boolean firstSource=true
		String sourceList=aliases.collect {
			String alias=it
			int index=0
			String innerQuery=records.collect {index++; mapToQuery(it,alias,index,firstSource)}.join("union all\n")
			firstSource=false
			return "(${innerQuery}) $alias"
		}.join(",\n")
		joinCondition=aliases.collect({"AND ${it}.DUMMY_KEY=${aliases[0]}.DUMMY_KEY"}).join("\n")
		return """${sourceList}
where
1=1
${joinCondition}"""
	}

	private List findAliases(List<Map> records) {
		List aliases=[]
		records[0].keySet().each {
			String columnName=it.toString()
			if (columnName.contains('.')) {
				aliases.add(columnName.substring(0, columnName.indexOf('.')))
			}
		}
		if (aliases.size()==0) {
			aliases.add("SRC")
		}
		return aliases
	}

	public String mapToQuery(Map<String,String> row, String sourceAlias, int index,boolean includeMissingSourceAliasColumns) {
		Map sourceAliasRow=row.findAll {
			String key=it.key.toString()
			return key.startsWith("${sourceAlias}.") || (includeMissingSourceAliasColumns && !key.contains('.'))
		}
		sourceAliasRow=sourceAliasRow.collectEntries {key, value ->
			if (key.startsWith("${sourceAlias}.")) {
				key=key.substring(key.indexOf('.')+1)
			}
			[key, value]
		}
		String expressions=sourceAliasRow.collect({key, value -> "${value} as ${key}" }).join(",")
		return getRecordQuery(index,expressions)
	}
	
	public ReverseEngineeringService getReverseEngineeringService() {
		return new ReverseEngineeringService()
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (sql!=null) {
			sql.close()
			sql=null
		}
	}
	
	static class SqlProxy {
		Sql sql
		
		SqlProxy() {
			addShutdownHook {close()}
		}
		void close() {
			if (sql!=null) {
				sql.close()
			}
			sql=null
		}
	}

}
