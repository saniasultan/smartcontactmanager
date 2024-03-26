package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@ModelAttribute
	public void addCoomonData(Model model,Principal principal) {
		String userName = principal.getName();
		System.out.println("username"+userName);
		User user = userRepository.getUserByEmail(userName);
		System.out.println("user="+user);
		
		model.addAttribute("user",user);
	}
	
	
	@GetMapping("/index")
public String dashboard(Model model,Principal principal) {
		model.addAttribute("title","Home-Page");
		
		
		return "normal/user_dashboard";
}
	
	
	
	@GetMapping("/addcontact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/addcontact";
	}
	@PostMapping("/process-contact")
	public String processcontact( @ModelAttribute Contact contact, @RequestParam("profileimage") MultipartFile file,
			Principal principal,HttpSession session) {
		
		try {
		String name = principal.getName();
		User user = this.userRepository.getUserByEmail(name);
		
		if(file.isEmpty()) {
		System.out.println("file is empty");
		contact.setImage("contact.png");
		}
		else {
			contact.setImage(file.getOriginalFilename());
		File saveFile = new ClassPathResource("static/image").getFile();
		Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
		
		System.out.println("image is uploaded");
		}
		
		contact.setUser(user);
		user.getContacts().add(contact);
		this.userRepository.save(user);
		System.out.println("data:"+contact);
		System.out.println("added to database");
		session.setAttribute("message",new Message("your contact is added","success") );
		
		
		}catch(Exception e) {
			System.out.println("error"+e.getMessage());
		e.printStackTrace();
		session.setAttribute("something went wrong",new Message("your contact is not added added","danger") );
		
		}
		return "normal/addcontact"; 
	}
	@GetMapping("/showcontacts/{page}")
	public String showContacts(@PathVariable("page")Integer page,Model m,Principal principal) {
		m.addAttribute("title","View Contacts");
		String userName = principal.getName();
		User user = this.userRepository.getUserByEmail(userName);
		
	//  this.contactRepository.findContactsByUser(user.getId());
	  Pageable pageable = PageRequest.of(page,10);
	  Page<Contact> contacts=this.contactRepository.findContactsByUser(user.getId(),pageable);
	  m.addAttribute("contacts",contacts);
	  m.addAttribute("currentpage",page);
	  m.addAttribute("totalpages",contacts.getTotalPages());
	  return "normal/showcontacts";
	}
	
	@GetMapping("/{cId}/contact/")
	public String showContactDetail(@PathVariable("cId")Integer cId,Model model,Principal principal) {
		
		System.out.println("cid"+cId);
		Optional<Contact> contactoptional = this.contactRepository.findById(cId);
		Contact contact = contactoptional.get();
		
		//String userName = principal.getName();
		//User user = this.userRepository.getUserByEmail(userName);
		model.addAttribute("contact",contact);
		return "normal/contactdetails";
	}
	@GetMapping("/delete/{cId}")
	public String deletecontact(@PathVariable("cId")Integer cId,Model model,HttpSession session,Principal principal) {
		
//		Optional<Contact> contactoptional = this.contactRepository.findById(cId);
//	
//		Contact contact = contactoptional.get();
//		this.contactRepository.delete(contact);
Contact contact=this.contactRepository.findById(cId).get();	
		User user=this.userRepository.getUserByEmail(principal.getName());
		
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		
		session.setAttribute("message",new Message( "successfully deleted","success"));
		return "redirect:/user/showcontacts/0";
	
	
	}
	
	@PostMapping("/updatecontact/{cId}")
	public String updateform(@PathVariable("cId")Integer cId,Model m) {
		m.addAttribute("title","updatecontact");
		Contact contact = this.contactRepository.findById(cId).get();
		m.addAttribute("contact",contact);
		
		return "normal/updateform";
	}
	@RequestMapping(value="/process-update", method=RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileimage") MultipartFile file,Model m,HttpSession session,Principal principal) {
	   
	   try {
		   Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get() ;
		   if(!file.isEmpty()) {
			   
			   File deleteFile = new ClassPathResource("static/image").getFile();
				File file1=new File(deleteFile,oldcontactDetail.getImage());
			   file1.delete();
			   
			   
			   
			   File saveFile = new ClassPathResource("static/image").getFile();
				
			   Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
				Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			   
		   }else {
			   contact.setImage( oldcontactDetail.getImage());
		   }
		   User user=this.userRepository.getUserByEmail(principal.getName());
	contact.setUser(user);
		   this.contactRepository.save(contact);
		   session.setAttribute("message", new Message("your contact is updated","success"));
	} catch (Exception e) {
		e.printStackTrace();
	}
		
		System.out.println("contact"+contact.getName());
	    return "redirect:/user/"+contact.getcId()+"/contact/";
	}
	
@GetMapping("/profile")
	public String yourprofile(Model model) {
		model.addAttribute("title","profile page");
		
		
		return "normal/profile";
	}
	
	
}
