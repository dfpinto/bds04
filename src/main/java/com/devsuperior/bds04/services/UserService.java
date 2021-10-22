package com.devsuperior.bds04.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds04.dto.UserDTO;
import com.devsuperior.bds04.entities.User;
import com.devsuperior.bds04.repositories.RoleRepository;
import com.devsuperior.bds04.repositories.UserRepository;
import com.devsuperior.bds04.services.exceptions.ResourceNotFoundException;

@Service
public class UserService implements UserDetailsService {

	private static Logger Logger = LoggerFactory.getLogger(UserDetailsService.class);
	
	@Autowired
	UserRepository repositoryUser;

	@Autowired
	RoleRepository repositoryRole;
	
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public List<UserDTO> findAll() {
		return repositoryUser.findAll().stream().map(x -> new UserDTO(x)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> entity = repositoryUser.findById(id);
		return (new UserDTO(entity.orElseThrow(() -> new ResourceNotFoundException("Entity not found"))));
	}

	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> list = repositoryUser.findAll(pageable);
		
		return list.map(x -> new UserDTO(x));
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repositoryUser.findByEmail(username);
		if(user == null) {
			Logger.error("User not found: " + username);
			throw new UsernameNotFoundException("Email not found");
		}
		
		Logger.info("User found: " + username);
		return user;
	}
}
