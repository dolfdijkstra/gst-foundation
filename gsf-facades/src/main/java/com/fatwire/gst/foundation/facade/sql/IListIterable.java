package com.fatwire.cs.sql;

import java.util.Date;
import java.util.Iterator;

import COM.FutureTense.Interfaces.IList;

import com.fatwire.cs.core.db.Util;

public class IListIterable implements Iterable<Row> {
	private final IList list;

	private final int numRows;

	public IListIterable(final IList list) {
		super();
		this.list = list;
		if (list != null) {
			this.numRows = list.numRows();
		} else {
			this.numRows = 0;
		}
	}

	public Iterator<Row> iterator() {
		if (list == null || !list.hasData()) {
			return new Iterator<Row>() {

				public boolean hasNext() {
					return false;
				}

				public Row next() {
					return null;
				}

				public void remove() {
					throw new RuntimeException("Can not remove");
				}

			};
		}
		return new Iterator<Row>() {
			private int rowNum = 0;

			public boolean hasNext() {
				return rowNum < numRows;
			}

			public Row next() {
				rowNum++;
				list.moveTo(rowNum);
				return new Row() {

					public byte[] getBytes(String key) {
						try {
							return (byte[]) list.getObject(key);
						} catch (NoSuchFieldException e) {
							throw new RuntimeException(e);
						}
					}

					public char getChar(String key) {
						try {
							String s = list.getValue(key);
							if (s != null && s.length() > 0) {
								return s.charAt(0);
							}
							throw new RuntimeException("no value for " + key);
						} catch (NoSuchFieldException e) {
							throw new RuntimeException(e);
						}
					}

					public Date getDate(String key) {
						try {
							String s = list.getValue(key);
							if (s != null && s.length() > 0) {
								return Util.parseJdbcDate(s);
							}
							throw new RuntimeException("no value for " + key);
						} catch (NoSuchFieldException e) {
							throw new RuntimeException(e);
						}
					}

					public long getLong(String key) {
						try {
							String s = list.getValue(key);
							if (s != null && s.length() > 0) {
								return Long.parseLong(s);
							}
							throw new RuntimeException("no value for " + key);
						} catch (NoSuchFieldException e) {
							throw new RuntimeException(e);
						}
					}

					public String getString(String key) {
						try {
							return list.getValue(key);
						} catch (NoSuchFieldException e) {
							throw new RuntimeException(e);
						}
					}

				};
			}

			public void remove() {
				throw new RuntimeException("Can not remove");
			}
		};
	}

	public int size() {
		return numRows;
	}

	public void flush() {
		if (list != null)
			list.flush();
	}

	public String getColumnName(int i) {
		if (list != null)
			return list.getColumnName(i);
		return "";
	}

	public String getIndirectColumnName(int index) {
		if (list != null)

			return list.getIndirectColumnName(index);
		return "";
	}

	public int numColumns() {
		if (list != null)
			return list.numColumns();
		return 0;
	}

	public int numIndirectColumns() {
		if (list != null)
			return list.numIndirectColumns();
		return 0;
	}

}
