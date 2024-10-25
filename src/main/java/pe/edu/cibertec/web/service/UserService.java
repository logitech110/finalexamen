package pe.edu.cibertec.web.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.municipalidad.model.User;
import com.municipalidad.repository.*;

@Service
public class UserService {
	
	@Autowired
	private IUserRepository userRepository;
	
	public User validateUserByEmailAndPassword(String email, String password) {
		User u = userRepository.findByEmailAndPassword(email, password);
		return u;
	}
	
	public User updateUserLogin(User user) {
//		User u = userRepository.getReferenceById(user.getIduser());
//		u.setLastlogin(new Date());
//		return userRepository.save(u);
		return userRepository.save(user);
	}
		public User save(User user) {
	        return userRepository.save(user);
	}
}
