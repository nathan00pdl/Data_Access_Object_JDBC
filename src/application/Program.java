package application;

import java.sql.Statement;
import java.util.Date;
import java.util.List;

import dao.SellerDAO;
import dao.daoFactory;
import entities.Department;
import entities.Seller;

public class Program {

	public static void main(String[] args) {

		System.out.println("====TEST 1: Department====");
		Department dep1 = new Department(1, "department 1");
		System.out.println(dep1);
		
		System.out.println();
		
		System.out.println("====TEST 2: Seller====");
		Seller seller1 = new Seller(20, "joao", "joao@gmail.com", new Date(), 300.00, dep1);
		System.out.println(seller1);
		
		System.out.println();
		
		System.out.println("====TEST 3: Seller findById====");
		SellerDAO sellerDao = daoFactory.createSellerDAO();
		Seller seller2 = sellerDao.findById(3);
		System.out.println(seller2);
		
		System.out.println();
		
		System.out.println("====TEST 4: Seller findByDepartment====");
		Department dep2 = new Department(2, null);
		List<Seller> list = sellerDao.findByDepartement(dep2);
		for(Seller obj : list) {
			System.out.println(obj);
		}
		
		System.out.println();
		
		System.out.println("====TEST 5: Seller findAll====");
		list = sellerDao.findAll();
		for(Seller obj : list) {
			System.out.println(obj);
		}
		
		System.out.println();
		
		System.out.println("====TEST 6: Seller insert====");
		Seller seller3 = new Seller(null, "Greg", "greg@gmail.com", new Date(), 4000.00, dep2);
		sellerDao.insert(seller3);
		System.out.println("Seller3 inserted!");
		System.out.println("Seller3 Id: " + seller3.getId());  //Obs: essa linha testa o comando 'Statement.RETURN_GENERATED_KEYS);'
	}
}
