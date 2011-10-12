package org.apromore.dao.dao;

import org.apromore.exception.ExceptionDao;

public class XmlFiles2DB extends BasicDao {

	public XmlFiles2DB() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	private static XmlFiles2DB instance;

	public static XmlFiles2DB getInstance() throws ExceptionDao {
		if (instance == null) {
			try {
				instance = new XmlFiles2DB();
			} catch (Exception e) {
				throw new ExceptionDao(
				"Error: not able to get instance for DAO: " + e.getMessage());
			}
		}
		return instance;
	}


}
