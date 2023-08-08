package impl;  //'impl' -> implementação

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.SellerDAO;
import db.DB;
import db.dbException;
import entities.Department;
import entities.Seller;

//Obs: 'SellerDAOjdbc' é uma implementação jdbc da interface 'SellerDAO'
public class SellerDAOjdbc implements SellerDAO{
	
	//Gerando dependência
	private Connection conn;
	
	public SellerDAOjdbc(Connection conn) {
		this.conn = conn;
	}

	
	//Declarando métodos estáticos
	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO seller \r\n"
				  + "(Name, Email, BirthDate, BaseSalary, DepartmentId) \r\n" 
				  + "VALUES (?, ?, ?, ?, ?)",
				     Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
				
			}else {
				throw new dbException("Unexpected error! No rows affected");
			}
		}
		catch (SQLException e) {
			throw new dbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE seller \r\n"
				  + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? \r\n"
				  + "WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new dbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName \r\n"
				  + "FROM seller INNER JOIN department \r\n"
				  + "ON seller.DepartmentId = department.Id \r\n"
				  + "WHERE seller.Id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			
			//testando se a querie foi executada (se retornou dados ou não)
			if (rs.next()) {
				Department dep = instantiateDepartment(rs);
				Seller obj = instantiateSeller(rs,dep);
				return obj;
			}
			return null;
		}
		catch(SQLException e) {
			throw new dbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep);
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					 "SELECT seller.*, department.Name as DepName \r\n"
				   + "FROM seller \r\n"
				   + "INNER JOIN department ON seller.DepartmentId = department.Id \r\n"
				   + "ORDER BY Name");
			
			rs = st.executeQuery();
			
			//Declaração de uma lista para guardar os resultados da querie
			List<Seller> list = new ArrayList<>();
			
			//Declaração de uma estrutura 'map' para controlar a não repetição de departamentos gerados
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {
				//Controle da não repetição dos departamentos
				Department dep  = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				Seller obj = instantiateSeller(rs,dep);
				list.add(obj);
			}
			return list;
		}
		catch(SQLException e) {
			throw new dbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartement(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					 "SELECT seller.*, department.Name as DepName \r\n"
				   + "FROM seller \r\n"
				   + "INNER JOIN department ON seller.DepartmentId = department.Id \r\n"
				   + "WHERE DepartmentId = ? \r\n"
				   + "ORDER BY Name");
			
			st.setInt(1, department.getId());
			rs = st.executeQuery();
			
			//Declaração de uma lista para guardar os resultados da querie
			List<Seller> list = new ArrayList<>();
			
			//Declaração de uma estrutura 'map' para controlar a não repetição de departamentos gerados
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {
				//Controle da não repetição dos departamentos
				Department dep  = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				Seller obj = instantiateSeller(rs,dep);
				list.add(obj);
			}
			return list;
		}
		catch(SQLException e) {
			throw new dbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}  
}
