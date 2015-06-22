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
package org.disl.pattern;

public interface ExecutionInfo {

	/**
	 * Get execution duration in miliseconds.
	 * *
	 * @return
	 */
	public abstract Long getDuration()

	/**
	 * Get execution end time.
	 */
	public abstract Long getEndTime()

	/**
	 * Get number of processed rows.
	 * */
	public abstract Long getProcessedRows()

	/**
	 * Get execution start time.
	 * */
	public abstract Long getStartTime()

	/**
	 * Get runtime status.
	 * *
	 * @return
	 */
	public abstract Status getStatus()
	
	abstract class Adapter implements ExecutionInfo {
		
		Status status
		Long startTime
		Long endTime
		Long processedRows
		
		Long getDuration() {
			if (startTime==null) {
				return null;
			}
			if (endTime==null) {
				return System.currentTimeMillis()-startTime
			}
			return endTime-startTime
		}
	}

}
