package com.itq.userService.service;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.itq.userService.business.UserBusiness;
import com.itq.userService.dto.Ack;
import com.itq.userService.dto.User;

@RestController
@Validated
public class UserServiceController {

	@Autowired
	private UserBusiness userBusiness;
	
	@GetMapping("/user/{userId}")
	public User getUser(@PathVariable("userId") int userId) {
		return userBusiness.getUser(userId);
	}

	@PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
	public Ack createUser(@Valid @RequestBody User user) {
		Ack ack = new Ack();
		if (userBusiness.createUser(user)) {
			ack.setCode(0);
			ack.setDescription("User created successfully");
		} else {
			ack.setCode(1);
			ack.setDescription("ERROR: User not created");
		}	
		return ack;
	}
	
	@PutMapping(value = "/user/{userId}", consumes = "application/json", produces = "application/json")
	public Ack updateUser(@PathVariable("userId") int userId, @Valid @RequestBody User user) {
		Ack ack = new Ack();
		if (userBusiness.updateUser(userId, user)) {
			ack.setCode(0);
			ack.setDescription("User updated successfully");
		} else {
			ack.setCode(1);
			ack.setDescription("ERROR: User not updated");
		}	
		return ack;
	}

	@DeleteMapping("/user/{userId}")
	public Ack deleteUser(@PathVariable("userId") int userId) {
		Ack ack = new Ack();
		if (userBusiness.deleteUser(userId)) {
			ack.setCode(0);
			ack.setDescription("User deleted successfully");
		} else {
			ack.setCode(1);
			ack.setDescription("ERROR: User not deleted");
		}	
		return ack;
	}

	@GetMapping("/users")
	public List<User> getAll() {
		return (List<User>) userBusiness.getAllUsers();
	}

}
