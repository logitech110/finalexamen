package pe.edu.cibertec.thymeleaf.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import pe.edu.cibertec.web.bd.MySQLDataSource;
import pe.edu.cibertec.web.model.Person;
import pe.edu.cibertec.web.model.Role;
import pe.edu.cibertec.web.model.User;
import pe.edu.cibertec.web.repository.IPersonRepository;
import pe.edu.cibertec.web.repository.IRoleRepository;
import pe.edu.cibertec.web.repository.IUserRepository;
import pe.edu.cibertec.web.service.PersonService;
import pe.edu.cibertec.web.service.RoleService;
import pe.edu.cibertec.web.service.UserService;

@Controller
public class ProyectoController {
	
	
	@Autowired
	private IRoleRepository repos;
	
	@Autowired
	private IPersonRepository reposPerson;
	
	@Autowired
	private IUserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PersonService personService;
	
	
	    @Autowired
	    private RoleService roleService;

	    @GetMapping
	    public List<Role> getAllRoles() {
	        return roleService.findAll(); 
	    }
	
	@GetMapping("/greeting")
	public String greeting (@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		model.addAttribute("name", name);
		return "greeting";
	}
	
	@GetMapping("/listar")
	public String listRole(Model model) {
		try {
			model.addAttribute("ltsRole", repos.findAll());
			model.addAttribute("ltsPerson", reposPerson.findAll());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "listado";
	}
	
	
	
	@GetMapping("/login")
	public String loginView(Model model) {
		System.out.println("Mostrando login");
		model.addAttribute("userLogin", new User());
		return "login";
	}
	
	@PostMapping("/login")
	public String login(@ModelAttribute User user, Model model) {
		System.out.println("Validando login");		
		String redirect = "login";
		User u = userService.validateUserByEmailAndPassword(user.getEmail(), user.getPassword());
		if (u != null) {
			u.setLastlogin(new Date());
			System.out.println("Actualizando usuario");
			User updUser = userService.updateUserLogin(u);
			model.addAttribute("userLogin", updUser);
			redirect = "greeting";
		} else {
			model.addAttribute("errors", "Usuario o clave incorrectos");
			model.addAttribute("userLogin", new User());
		}
		
		return redirect;
	}
	
	@GetMapping("/registro")
	public String registrarView(Model model) {
	    model.addAttribute("user", new User()); 
	    return "registro";  
	}

	@PostMapping("/registro")
	public String registrarUsuario(@ModelAttribute User user, Model model) {
	    try {
	        userService.save(user); 
	        return "greeting";  
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("errors", "Error al registrar el usuario");
	        return "registro";  
	    }
	}


    //@GetMapping("/login")
    //public String showLoginForm() {
      //  return "login";}
	//@GetMapping("/listar")
    //public List<Person> obtenerTodos() {
    //   return personService.findAll();
    //}
	

	
	@RequestMapping(value = "/UsuariosReport", method = RequestMethod.GET)
	public void personaReporte(HttpServletResponse response) throws JRException, IOException {
		System.out.println("Generando Reporte.....");
		InputStream is = this.getClass().getResourceAsStream("/Wood.jasper");
		Map<String, Object> params = new HashMap<String, Object>();
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(is);	
		Connection con = MySQLDataSource.getMySQLConnection();
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, con);		
		response.setContentType("application/x-pdf");
		response.setHeader("Content-disposition", "inline; filename=usuarios-report.pdf");		
		OutputStream outputStream = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
}
}
