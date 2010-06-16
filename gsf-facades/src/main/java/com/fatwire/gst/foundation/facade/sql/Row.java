package com.fatwire.cs.sql;

import java.util.Date;

/**
 * 
 * Repesents a row in an IList.
 * 
 * Wrapper over IList so that it can be used by an iterator()
 * 
 * @author Dolf.Dijkstra
 * 
 */

public interface Row {
	String getString(String key);

	long getLong(String key);

	byte[] getBytes(String key);

	char getChar(String key);

	Date getDate(String key);
}
