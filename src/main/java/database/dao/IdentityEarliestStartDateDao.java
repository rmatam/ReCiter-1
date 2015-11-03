package database.dao;

import java.util.List;

import database.model.IdentityEarliestStartDate;

public interface IdentityEarliestStartDateDao {
	public List<IdentityEarliestStartDate> getAllIdentityEarliestStartDates();
	public IdentityEarliestStartDate getIdentityEarliestStartDateByCwid(String cwid);
}
