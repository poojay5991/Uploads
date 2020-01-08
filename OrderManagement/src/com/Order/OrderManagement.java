package com.Order;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class OrderManagement {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test_java","root","admin");
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter the number of customer : ");
		int cust = sc.nextInt();
		Random random_id = new Random();
		ArrayList<Customer> list = new ArrayList<Customer>();
		System.out.println("Enter the details :\n1. customer_id\r\n" + 
				"2. customer_name\r\n" + 
				"3. addressLine1\r\n" + 
				"4. addressLine2\r\n" + 
				"5. addressLine3\r\n" + 
				"6. city\r\n" + 
				"7. state\r\n" + 
				"8. country_code\r\n" + 
				"9. contactType\n"+
				"10. Item number (1-77)\n"+
				"11. Quantity(maximum 50)");
		for(int i =0; i<cust; i++)
		{
			Customer customer = new Customer();
			Scanner sc1 = new Scanner(System.in);
			System.out.println("Customer : "+(i+1));
			customer.setCustomer_id(Integer.parseInt(sc1.nextLine()));
			customer.setCustomer_name(sc1.nextLine());
			customer.setAddressLine1(sc1.nextLine());
			customer.setAddressLine2(sc1.nextLine());
			customer.setAddressLine3(sc1.nextLine());
			customer.setCity(sc1.nextLine());
			customer.setState(sc1.nextLine());
			customer.setCountry_code(sc1.nextLine());
			String contactType = sc1.nextLine();
			if(contactType.equalsIgnoreCase("HP") || contactType.equalsIgnoreCase("WP"))
			{
				customer.setContactType(contactType);
			}
			else
			{
				throw new Exception("Please select phone type in HP or WP only");
			}
			OrderDetails order = new OrderDetails();
			order.setItem_id(Integer.parseInt(sc1.nextLine()));
			order.setOrder_quantity(Integer.parseInt(sc1.nextLine()));
			order.setOrder_id(random_id.nextInt(1000));
			customer.setOrder(order);
			
			list.add(customer);
		}
		
		for(int i=0;i<list.size();i++)
		{
			PreparedStatement customerStmt = con.prepareStatement("insert into customer values (?,?,?,?,?,?,?,?,?,?,?,?)");
			customerStmt.setInt(1, list.get(i).getCustomer_id());
			customerStmt.setString(2, list.get(i).getCustomer_name());
			customerStmt.setString(3, list.get(i).getAddressLine1());
			customerStmt.setString(4, list.get(i).getAddressLine2());
			customerStmt.setString(5, list.get(i).getAddressLine3());
			customerStmt.setString(6, list.get(i).getCity());
			customerStmt.setString(7, list.get(i).getState());
			customerStmt.setString(8, list.get(i).getCountry_code());
			customerStmt.setInt(9, list.get(i).getOrder().getOrder_id());
			customerStmt.setString(10, list.get(i).getContactType());
			customerStmt.setString(11, "");
			customerStmt.setInt(12, 0);
			
			PreparedStatement stmt = con.prepareStatement("select cost from inventory where item_id = ?");
			stmt.setInt(1, list.get(i).getOrder().getItem_id());
			ResultSet rs = stmt.executeQuery();
			float itemCost = 0;
			while(rs.next())
			{
			 itemCost = rs.getFloat(1);
			}
			
			PreparedStatement stmt1 = con.prepareStatement("select exchange_rate from exchangerate_table where country_code = ?");
			stmt1.setString(1, list.get(i).getCountry_code());
			ResultSet rs1 = stmt1.executeQuery();
			float exchangeRate = 0;
			while(rs1.next())
			{
			 exchangeRate = rs1.getFloat(1);
			}
			
			float totalCost = list.get(i).getOrder().getOrder_quantity() * itemCost *exchangeRate;
			
			PreparedStatement orderStmt = con.prepareStatement("insert into orderdetails values (?,?,?,?,?)");
			orderStmt.setInt(1, list.get(i).getOrder().getOrder_id());
			orderStmt.setInt(2, list.get(i).getCustomer_id());
			orderStmt.setFloat(3, totalCost);
			orderStmt.setInt(4, list.get(i).getOrder().getItem_id());
			orderStmt.setInt(5, list.get(i).getOrder().getOrder_quantity());
			
			int k = customerStmt.executeUpdate();
			System.out.println("Customer added");
			
			int l = orderStmt.executeUpdate();
			System.out.println("Order added");
		}
		sc.close();
	}

}
