package com.sgt.walmart.account;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {
	public List<SeatHold> findAllByLevel(Integer level);

	@Modifying
	@Transactional
	@Query("delete from SeatHold s where s.expirationDate < CURRENT_TIMESTAMP")
	void deleteExpired();
}