package com.itq.userService.service;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.itq.userService.business.UserBusiness;
import com.itq.userService.dto.Ack;
import com.itq.userService.dto.User;

@RestController
public class UserServiceController {

	@Autowired
	private UserBusiness userBusiness;
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceController.class);

	@PostMapping(value = "/user", consumes = "application/json", produces = "application/json")
	public Ack createAuto(@RequestBody User user) {
		Ack ack = new Ack();
		if (userBusiness.createUser(user)) {
			ack.setCode(0);
			ack.setDescription("User created successfully");
			//LOGGER.info("User created successfully");
		} else {
			ack.setCode(1);
			ack.setDescription("ERROR: User not created");
			//LOGGER.error("ERROR: User not created");
		}	
		return ack;
	}

}
