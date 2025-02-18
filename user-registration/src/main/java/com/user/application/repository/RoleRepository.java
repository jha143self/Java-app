package com.user.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.application.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{

	
	Role findByName(String name);

    @Override
    void delete(Role role);
}
